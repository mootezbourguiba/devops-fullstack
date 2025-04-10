// Jenkinsfile FINAL v5 (avec agent Docker et installation d'outils)
pipeline {
    // --- Utilise un agent Docker ---
    agent {
        docker {
            // Utilise une image contenant JDK 17 et Maven. Git y est souvent aussi.
            image 'maven:3.9-eclipse-temurin-17' // Image à jour avec Maven et JDK 17
            // Monte le socket Docker de l'hôte pour pouvoir exécuter des commandes docker DANS le conteneur
            // et monte l'exécutable docker client de l'hôte (adapter chemin si nécessaire)
            args '-v /var/run/docker.sock:/var/run/docker.sock -v /usr/bin/docker:/usr/bin/docker'
            // ATTENTION: Le montage du socket Docker a des implications de sécurité.
        }
    }

    // --- Outils gérés par Jenkins (pour Nodejs et Git au cas où) ---
    tools {
        nodejs 'node-20' // Garde Nodejs car pas dans l'image Maven par défaut
        git 'Default'    // Garde Git au cas où celui de l'image pose problème
    }

    // Variables d'environnement pour le pipeline
    environment {
        // --- Credentials Jenkins ---
        DOCKERHUB_CREDENTIALS_ID = 'dockerhub-credentials'
        SSH_CREDENTIALS_ID       = 'ssh-credentials-mon-serveur'

        // --- Configuration Docker Hub ---
        DOCKERHUB_USERNAME       = "mootezbourguiba73"     // TON username Docker Hub
        IMAGE_NAME_BACKEND       = "devops-backend"
        IMAGE_NAME_FRONTEND      = "devops-frontend"
        IMAGE_BACKEND            = "${env.DOCKERHUB_USERNAME}/${env.IMAGE_NAME_BACKEND}:latest"
        IMAGE_FRONTEND           = "${env.DOCKERHUB_USERNAME}/${env.IMAGE_NAME_FRONTEND}:latest"

        // --- Configuration Déploiement SSH ---
        REMOTE_USER              = "mootez"
        REMOTE_HOST              = "localhost" // Connexion via redirection de port VBox
        REMOTE_PORT              = "2222"    // Port hôte redirigé
        REMOTE_DEPLOY_PATH       = "/home/${env.REMOTE_USER}/devops-app"
        PROD_COMPOSE_FILE        = "docker-compose.prod.yml" // Fichier à la racine Git
        REMOTE_COMPOSE_FILENAME  = "docker-compose.yml"      // Nom sur le serveur distant

        // --- Configuration du PATH pour Nodejs (Git/JDK/Maven via agent) ---
        NODEJS_HOME = tool 'node-20'
        PATH = "${NODEJS_HOME}/bin:${tool 'Default'}/bin:${env.PATH}" // Ajoute Node et Git au PATH
    }

    stages {
        stage('1. Checkout') {
            steps {
                echo "📥 [${env.BRANCH_NAME}] Récupération du code depuis GitHub..."
                // Utilise le Git de l'agent ou celui spécifié dans PATH
                sh 'git --version' // Vérifie quel git est utilisé
                checkout scm
                echo '>>> Workspace après checkout:'
                sh 'ls -la'
            }
        }

        stage('2. Build et Test Backend') {
            steps {
                echo "⚙️ [${env.BRANCH_NAME}] Build/Test backend..."
                dir('devops-fullstack/backend/backendDevops') {
                    // Utilise Maven/JDK de l'agent Docker 'maven:3.9-eclipse-temurin-17'
                    sh 'echo ">>> Java Version:"'
                    sh 'java -version'
                    sh 'echo ">>> Maven Version:"'
                    sh 'mvn -v'
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
                    // Utilise Node/NPM installé par Jenkins Tool et ajouté au PATH
                    sh 'echo ">>> Node Version:"'
                    sh 'node -v'
                    sh 'echo ">>> NPM Version:"'
                    sh 'npm -v'
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
                // Vérifie que la commande docker est accessible via le socket monté
                sh 'docker --version'
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
                    // Tenter d'installer openssh-client si nécessaire dans l'agent
                    try {
                        sh 'which scp || (apt-get update && apt-get install -y openssh-client)'
                        sh 'which ssh || (apt-get update && apt-get install -y openssh-client)'
                    } catch (err) {
                        echo "Avertissement : Impossible d'installer/vérifier openssh-client : ${err}"
                        // On continue quand même, peut-être qu'ils sont déjà là
                    }
                }
                echo "🛰️ [${env.BRANCH_NAME}] Deploying to VM (${REMOTE_USER}@${REMOTE_HOST}:${REMOTE_PORT})..."
                sshagent(credentials: [env.SSH_CREDENTIALS_ID]) { // ID: ssh-credentials-mon-serveur

                    echo "📄 Copying ${PROD_COMPOSE_FILE}..."
                    // Utilise scp avec le port spécifié (-P majuscule)
                    sh "scp -o StrictHostKeyChecking=no -P ${REMOTE_PORT} ${PROD_COMPOSE_FILE} ${REMOTE_USER}@${REMOTE_HOST}:${REMOTE_DEPLOY_PATH}/${REMOTE_COMPOSE_FILENAME}"

                    echo "🚀 Running Docker Compose on VM..."
                    // Utilise ssh avec le port spécifié (-p minuscule)
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