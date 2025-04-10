// Jenkinsfile FINAL (avec déploiement SSH vers VM via NAT+PortForwarding)
pipeline {
    agent any // Utilise n'importe quel agent Jenkins disponible

    // Définitions des outils à utiliser (configurés dans Jenkins -> Global Tool Configuration)
    tools {
        jdk 'jdk17'                // Assure-toi que ce nom correspond à ta config Jenkins
        maven 'apache-maven-3.8.6' // Assure-toi que ce nom correspond
        nodejs 'node-20'           // Assure-toi que ce nom correspond
    }

    // Variables d'environnement pour le pipeline
    environment {
        // --- Credentials Jenkins ---
        DOCKERHUB_CREDENTIALS_ID = 'dockerhub-credentials'       // ID pour Docker Hub
        SSH_CREDENTIALS_ID       = 'ssh-credentials-mon-serveur' // ID pour la clé privée SSH de la VM

        // --- Configuration Docker Hub ---
        DOCKERHUB_USERNAME       = "mootezbourguiba73"     // TON username Docker Hub
        IMAGE_NAME_BACKEND       = "devops-backend"        // Nom de l'image backend sur Docker Hub
        IMAGE_NAME_FRONTEND      = "devops-frontend"       // Nom de l'image frontend sur Docker Hub
        IMAGE_BACKEND            = "${env.DOCKERHUB_USERNAME}/${env.IMAGE_NAME_BACKEND}:latest" // Nom complet de l'image backend
        IMAGE_FRONTEND           = "${env.DOCKERHUB_USERNAME}/${env.IMAGE_NAME_FRONTEND}:latest" // Nom complet de l'image frontend

        // --- Configuration Déploiement SSH ---
        REMOTE_USER              = "mootez"                              // Username sur la VM Ubuntu
        REMOTE_HOST              = "localhost"                           // On se connecte à localhost (redirigé par VirtualBox)
        REMOTE_PORT              = "2222"                                // Port Hôte redirigé vers le port 22 de la VM
        REMOTE_DEPLOY_PATH       = "/home/${env.REMOTE_USER}/devops-app" // Chemin sur la VM (utilise le REMOTE_USER)
        PROD_COMPOSE_FILE        = "docker-compose.prod.yml"             // Nom du fichier compose pour la prod (à la racine du projet Git)
        REMOTE_COMPOSE_FILENAME  = "docker-compose.yml"                  // Nom du fichier sur le serveur distant après copie
    }

    stages {
        stage('1. Checkout') {
            steps {
                echo "📥 [${env.BRANCH_NAME}] Récupération du code depuis GitHub..."
                checkout scm
                echo '>>> Contenu du workspace après checkout:'
                sh 'ls -la'
            }
        }

        stage('2. Build et Test Backend') {
            steps {
                echo "⚙️ [${env.BRANCH_NAME}] Construction et test du backend..."
                dir('devops-fullstack/backend/backendDevops') {
                    sh 'echo ">>> Dans $(pwd)"'
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
                echo "🌐 [${env.BRANCH_NAME}] Construction et test du frontend..."
                dir('devops-fullstack/frontend/frontenddevops') {
                    sh 'echo ">>> Dans $(pwd)"'
                    sh "npm install"
                    // Exécute les tests SANS le mode watch
                    sh "npm test -- --watchAll=false"
                    // Construit les fichiers statiques pour la production
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

                    echo "🐳 [${env.BRANCH_NAME}] Connexion à Docker Hub (${env.DOCKERHUB_USERNAME})..."
                    sh "docker login -u '${env.DOCKERHUB_USERNAME}' -p '${DOCKERHUB_PASSWORD}'"

                    echo "🔨 [${env.BRANCH_NAME}] Build image backend: ${IMAGE_BACKEND}"
                    dir('devops-fullstack/backend/backendDevops') {
                        // Construit l'image en utilisant le Dockerfile local
                        sh "docker build -t ${IMAGE_BACKEND} ."
                    }
                    echo "🚀 [${env.BRANCH_NAME}] Push image backend..."
                    sh "docker push ${IMAGE_BACKEND}"

                    echo "🔨 [${env.BRANCH_NAME}] Build image frontend: ${IMAGE_FRONTEND}"
                    dir('devops-fullstack/frontend/frontenddevops') {
                        // Construit l'image en utilisant le Dockerfile local
                        sh "docker build -t ${IMAGE_FRONTEND} ."
                    }
                    echo "🚀 [${env.BRANCH_NAME}] Push image frontend..."
                    sh "docker push ${IMAGE_FRONTEND}"

                    echo "🚪 [${env.BRANCH_NAME}] Déconnexion de Docker Hub..."
                    sh 'docker logout'
                }
            }
        }

        // --- STAGE DE DÉPLOIEMENT MODIFIÉ ---
        stage('5. Deploy to VM via SSH') {
             // Condition : S'exécute seulement pour la branche 'main'
             when { branch 'main' }
             steps {
                echo "🛰️ [${env.BRANCH_NAME}] Déploiement sur VM Ubuntu via SSH (${REMOTE_USER}@${REMOTE_HOST}:${REMOTE_PORT})..."
                // Utilise le plugin SSH Agent avec les credentials configurés
                sshagent(credentials: [env.SSH_CREDENTIALS_ID]) { // ID: ssh-credentials-mon-serveur

                    echo "📄 Copie de ${PROD_COMPOSE_FILE} vers ${REMOTE_DEPLOY_PATH}/${REMOTE_COMPOSE_FILENAME} sur la VM..."
                    // Utilise scp avec le port spécifié (-P majuscule) et la destination localhost
                    sh "scp -o StrictHostKeyChecking=no -P ${REMOTE_PORT} ${PROD_COMPOSE_FILE} ${REMOTE_USER}@${REMOTE_HOST}:${REMOTE_DEPLOY_PATH}/${REMOTE_COMPOSE_FILENAME}"

                    echo "🚀 Exécution de Docker Compose sur la VM..."
                    // Utilise ssh avec le port spécifié (-p minuscule) et la destination localhost
                    // Commande: va dans le dossier, pull les dernières images (backend/frontend) depuis Docker Hub,
                    // puis démarre/met à jour les services en arrière-plan en utilisant le fichier compose copié.
                    sh "ssh -o StrictHostKeyChecking=no -p ${REMOTE_PORT} ${REMOTE_USER}@${REMOTE_HOST} 'cd ${REMOTE_DEPLOY_PATH} && docker compose -f ${REMOTE_COMPOSE_FILENAME} pull && docker compose -f ${REMOTE_COMPOSE_FILENAME} up -d'"
                }
            }
        }
        // --- FIN STAGE DE DÉPLOIEMENT ---
    } // Fin stages

    // Actions post-build
    post {
        always {
            echo '🧹 Nettoyage du workspace Jenkins...'
            cleanWs() // Supprime les fichiers du workspace pour le prochain build
        }
        success {
            echo "✅ [${env.BRANCH_NAME}] Pipeline terminé avec SUCCÈS !"
            // Ajouter des notifications ici (Email, Slack, etc.)
        }
        failure {
            echo "❌ [${env.BRANCH_NAME}] Pipeline en ÉCHEC !"
             // Ajouter des notifications d'échec ici
        }
    }
} // Fin pipeline