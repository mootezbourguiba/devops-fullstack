// Jenkinsfile FINAL v4 (simplifié après erreur accolade)
pipeline {
    agent any

    tools {
        jdk 'jdk17'
        maven 'apache-maven-3.8.6'
        nodejs 'node-20'
        git 'Default'
    }

    environment {
        // --- Credentials Jenkins ---
        DOCKERHUB_CREDENTIALS_ID = 'dockerhub-credentials'
        SSH_CREDENTIALS_ID       = 'ssh-credentials-mon-serveur'

        // --- Configuration Docker Hub ---
        DOCKERHUB_USERNAME       = "mootezbourguiba73"
        IMAGE_NAME_BACKEND       = "devops-backend"
        IMAGE_NAME_FRONTEND      = "devops-frontend"
        IMAGE_BACKEND            = "${env.DOCKERHUB_USERNAME}/${env.IMAGE_NAME_BACKEND}:latest"
        IMAGE_FRONTEND           = "${env.DOCKERHUB_USERNAME}/${env.IMAGE_NAME_FRONTEND}:latest"

        // --- Configuration Déploiement SSH ---
        REMOTE_USER              = "mootez"
        REMOTE_HOST              = "localhost"
        REMOTE_PORT              = "2222"
        REMOTE_DEPLOY_PATH       = "/home/${env.REMOTE_USER}/devops-app"
        PROD_COMPOSE_FILE        = "docker-compose.prod.yml"
        REMOTE_COMPOSE_FILENAME  = "docker-compose.yml"

        // --- Modification du PATH ---
        // Définition plus propre des variables d'outils
        GIT_HOME = tool 'Default'
        JDK_HOME = tool 'jdk17'
        M2_HOME = tool 'apache-maven-3.8.6'
        NODEJS_HOME = tool 'node-20'
        // Ajoute les chemins au PATH
        PATH = "${GIT_HOME}/bin:${JDK_HOME}/bin:${M2_HOME}/bin:${NODEJS_HOME}/bin:${env.PATH}"
    }

    stages {
        stage('0. Debug Tools and PATH') {
            steps {
                echo "--- Vérification Environnement ---"
                echo "PATH complet:"
                sh 'printenv PATH'
                echo "--- Vérification Outils ---"
                sh 'git --version'
                sh 'java -version'
                sh 'mvn -version'
                sh 'node -v'
                sh 'npm -v'
                sh 'docker --version' // Vérifie si docker est dispo pour l'agent
                sh 'docker compose version' // Vérifie docker compose V2
            }
        }

        stage('1. Checkout') {
            steps {
                echo "📥 [${env.BRANCH_NAME}] Récupération du code..."
                checkout scm
                echo '>>> Workspace après checkout:'
                sh 'ls -la'
            }
        }

        stage('2. Build et Test Backend') {
            steps {
                echo "⚙️ [${env.BRANCH_NAME}] Build/Test backend..."
                dir('devops-fullstack/backend/backendDevops') {
                    // Normalement plus besoin de withEnv car JAVA_HOME/PATH sont OK globalement
                    sh "mvn clean package"
                }
            }
            post {
                success {
                    archiveArtifacts artifacts: 'devops-fullstack/backend/backendDevops/target/*.jar', fingerprint: true
                }
            }
        }

        stage('3. Build et Test Frontend') {
            steps {
                echo "🌐 [${env.BRANCH_NAME}] Build/Test frontend..."
                dir('devops-fullstack/frontend/frontenddevops') {
                     // Normalement plus besoin de withEnv
                    sh "npm install"
                    sh "npm test -- --watchAll=false"
                    sh "npm run build"
                }
            }
            post {
                success {
                    archiveArtifacts artifacts: 'devops-fullstack/frontend/frontenddevops/build/**', fingerprint: true
                }
            }
        }

        stage('4. Build et Push Docker Images') {
            steps {
                withCredentials([usernamePassword(credentialsId: env.DOCKERHUB_CREDENTIALS_ID,
                                               passwordVariable: 'DOCKERHUB_PASSWORD',
                                               usernameVariable: 'DOCKERHUB_USER')]) {
                    echo "🐳 [${env.BRANCH_NAME}] Login Docker Hub..."
                    sh "docker login -u '${env.DOCKERHUB_USERNAME}' -p '${DOCKERHUB_PASSWORD}'"

                    echo "🔨 [${env.BRANCH_NAME}] Build backend image..."
                    dir('devops-fullstack/backend/backendDevops') {
                        sh "docker build -t ${IMAGE_BACKEND} ."
                    }
                    echo "🚀 [${env.BRANCH_NAME}] Push backend image..."
                    sh "docker push ${IMAGE_BACKEND}"

                    echo "🔨 [${env.BRANCH_NAME}] Build frontend image..."
                    dir('devops-fullstack/frontend/frontenddevops') {
                        sh "docker build -t ${IMAGE_FRONTEND} ."
                    }
                    echo "🚀 [${env.BRANCH_NAME}] Push frontend image..."
                    sh "docker push ${IMAGE_FRONTEND}"

                    echo "🚪 [${env.BRANCH_NAME}] Logout Docker Hub..."
                    sh 'docker logout'
                }
            }
        }

        stage('5. Deploy to VM via SSH') {
             when { branch 'main' }
             steps {
                echo "🛰️ [${env.BRANCH_NAME}] Deploying to VM (${REMOTE_USER}@${REMOTE_HOST}:${REMOTE_PORT})..."
                sshagent(credentials: [env.SSH_CREDENTIALS_ID]) {
                    echo "📄 Copying ${PROD_COMPOSE_FILE}..."
                    sh "scp -o StrictHostKeyChecking=no -P ${REMOTE_PORT} ${PROD_COMPOSE_FILE} ${REMOTE_USER}@${REMOTE_HOST}:${REMOTE_DEPLOY_PATH}/${REMOTE_COMPOSE_FILENAME}"

                    echo "🚀 Running Docker Compose on VM..."
                    sh "ssh -o StrictHostKeyChecking=no -p ${REMOTE_PORT} ${REMOTE_USER}@${REMOTE_HOST} 'cd ${REMOTE_DEPLOY_PATH} && docker compose -f ${REMOTE_COMPOSE_FILENAME} pull && docker compose -f ${REMOTE_COMPOSE_FILENAME} up -d'"
                }
            }
        }
    } // Fin stages

    post {
        always {
            echo '🧹 Cleaning workspace...'
            cleanWs()
        }
        success {
            echo "✅ [${env.BRANCH_NAME}] Pipeline SUCCESS!"
        }
        failure {
            echo "❌ [${env.BRANCH_NAME}] Pipeline FAILED!"
        }
    } // Fin post
} // Fin pipeline