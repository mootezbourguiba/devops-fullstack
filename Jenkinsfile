// Jenkinsfile FINAL v6 (agent any, Jenkins Controller tourne en root avec socket monté)
pipeline {
    agent any // Exécute sur l'agent par défaut (le contrôleur Jenkins qui tourne en root)

    // Outils à utiliser (configurés dans Jenkins -> Global Tool Configuration)
    tools {
        jdk 'jdk17'
        maven 'apache-maven-3.8.6'
        nodejs 'node-20'
        git 'Default' // Assure-toi que 'Default' est le nom de ton outil Git
    }

    // Variables d'environnement
    environment {
        // --- Credentials Jenkins ---
        DOCKERHUB_CREDENTIALS_ID = 'dockerhub-credentials'
        SSH_CREDENTIALS_ID       = 'ssh-credentials-mon-serveur'

        // --- Configuration Docker Hub ---
        DOCKERHUB_USERNAME       = "mootezbourguiba365" // TON username Docker Hub
        IMAGE_NAME_BACKEND       = "devops-backend"
        IMAGE_NAME_FRONTEND      = "devops-frontend"
        IMAGE_BACKEND            = "${env.DOCKERHUB_USERNAME}/${env.IMAGE_NAME_BACKEND}:latest"
        IMAGE_FRONTEND           = "${env.DOCKERHUB_USERNAME}/${env.IMAGE_NAME_FRONTEND}:latest"

        // --- Configuration Déploiement SSH ---
        REMOTE_USER              = "mootez"  // Username sur la VM Ubuntu
        REMOTE_HOST              = "localhost" // Via redirection VBox
        REMOTE_PORT              = "2222"      // Port hôte redirigé
        REMOTE_DEPLOY_PATH       = "/home/${env.REMOTE_USER}/devops-app" // Chemin sur la VM
        PROD_COMPOSE_FILE        = "docker-compose.prod.yml"          // Fichier à la racine Git
        REMOTE_COMPOSE_FILENAME  = "docker-compose.yml"               // Nom sur le serveur distant

        // --- Modification du PATH Complète ---
        // Ajoute tous les outils au PATH
        PATH = "${tool 'Default'}/bin:${tool 'jdk17'}/bin:${tool 'apache-maven-3.8.6'}/bin:${tool 'node-20'}/bin:${env.PATH}"
    }

    stages {
        stage('1. Checkout') {
            steps {
                echo "📥 [${env.BRANCH_NAME}] Récupération du code depuis GitHub..."
                sh 'git --version' // Vérifie que git est accessible
                checkout scm
                echo '>>> Workspace après checkout:'
                sh 'ls -la'
            }
        }

        stage('2. Build et Test Backend') {
            steps {
                echo "⚙️ [${env.BRANCH_NAME}] Build/Test backend..."
                dir('devops-fullstack/backend/backendDevops') {
                    sh 'java -version' // Vérifie
                    sh 'mvn -v'       // Vérifie
                    sh "mvn clean package" // Compile, teste, package
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
                    sh 'node -v' // Vérifie
                    sh 'npm -v'  // Vérifie
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
                // Le client Docker est installé DANS le conteneur Jenkins Controller
                // et accède au démon Docker de l'hôte via le socket monté.
                sh 'docker --version' // Vérifie l'accès à Docker
                sh 'docker compose version' // Vérifie l'accès à Docker Compose
                withCredentials([usernamePassword(credentialsId: env.DOCKERHUB_CREDENTIALS_ID,
                                               passwordVariable: 'DOCKERHUB_PASSWORD',
                                               usernameVariable: 'DOCKERHUB_USER')]) {

                    echo "🐳 [${env.BRANCH_NAME}] Login Docker Hub (${env.DOCKERHUB_USERNAME})..."
                    sh "docker login -u '${env.DOCKERHUB_USERNAME}' -p '${DOCKERHUB_PASSWORD}'"

                    echo "🔨 [${env.BRANCH_NAME}] Build backend image: ${IMAGE_BACKEND}"
                    dir('devops-fullstack/backend/backendDevops') {
                        sh "docker build -t ${IMAGE_BACKEND} ."
                    }
                    echo "🚀 [${env.BRANCH_NAME}] Push backend image..."
                    sh "docker push ${IMAGE_BACKEND}"

                    echo "🔨 [${env.BRANCH_NAME}] Build frontend image: ${IMAGE_FRONTEND}"
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
             // Condition : S'exécute seulement pour la branche 'main'
             when { branch 'main' }
             steps {
                 script {
                    // Vérifie si ssh/scp sont dispos (normalement oui dans l'image jenkins)
                    try {
                         sh 'echo "Vérification ssh/scp..."'
                         sh 'which ssh'
                         sh 'which scp'
                     } catch (err) {
                         echo "[WARN] Commande ssh ou scp non trouvée : ${err}"
                     }
                 }
                echo "🛰️ [${env.BRANCH_NAME}] Deploying to VM (${REMOTE_USER}@${REMOTE_HOST}:${REMOTE_PORT})..."
                sshagent(credentials: [env.SSH_CREDENTIALS_ID]) { // Utilise l'ID ssh-credentials-mon-serveur

                    echo "📄 Copying ${PROD_COMPOSE_FILE}..."
                    sh "scp -o StrictHostKeyChecking=no -P ${REMOTE_PORT} ${PROD_COMPOSE_FILE} ${REMOTE_USER}@${REMOTE_HOST}:${REMOTE_DEPLOY_PATH}/${REMOTE_COMPOSE_FILENAME}"

                    echo "🚀 Running Docker Compose on VM..."
                    sh "ssh -o StrictHostKeyChecking=no -p ${REMOTE_PORT} ${REMOTE_USER}@${REMOTE_HOST} 'cd ${REMOTE_DEPLOY_PATH} && docker compose -f ${REMOTE_COMPOSE_FILENAME} pull && docker compose -f ${REMOTE_COMPOSE_FILENAME} up -d'"
                }
            }
        }
    } // Fin stages

    // Actions post-build
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