// Jenkinsfile corrigé
pipeline {
    agent any
    tools { /* ... */ }
    environment { /* ... */ }

    stages {
        stage('Checkout') {
            steps {
                echo '📥 Récupération du code depuis GitHub...'
                checkout scm
                // Gardons le ls pour confirmer la structure
                echo '>>> Contenu de la racine du workspace après checkout:'
                sh 'ls -la'
            }
        }

        stage('Build et Test Backend') {
            steps {
                echo '⚙️ Construction et test du backend Spring Boot...'
                 // CORRECTION: Ajouter le préfixe devops-fullstack/
                dir('devops-fullstack/backend/backendDevops') {
                    sh 'echo ">>> Vérification du contenu du répertoire $(pwd):"' // Utiliser sh pwd
                    sh 'ls -la'
                    sh "mvn clean package"
                }
            }
            post {
                success {
                    // CORRECTION: Ajouter le préfixe devops-fullstack/
                    archiveArtifacts artifacts: 'devops-fullstack/backend/backendDevops/target/*.jar', fingerprint: true
                }
            }
        }

        stage('Build Frontend') {
            steps {
                echo '🌐 Construction du frontend React...'
                // CORRECTION: Ajouter le préfixe devops-fullstack/
                dir('devops-fullstack/frontend/frontenddevops') {
                    sh 'echo ">>> Vérification du contenu du répertoire $(pwd):"' // Utiliser sh pwd
                    sh 'ls -la'
                    sh "npm install"
                    sh "npm run build"
                }
            }
            post {
                success {
                     // CORRECTION: Ajouter le préfixe devops-fullstack/
                    archiveArtifacts artifacts: 'devops-fullstack/frontend/frontenddevops/build/**', fingerprint: true
                }
            }
        }

        stage('Build et Push Docker Images') {
            steps {
                echo "🐳 Connexion à Docker Hub (${DOCKERHUB_USERNAME})..."
                withCredentials(/*...*/) {
                    sh "docker login -u '${env.DOCKERHUB_USERNAME}' -p '${DOCKERHUB_PASSWORD}'"

                    echo "🔨 Construction de l'image backend: ${IMAGE_BACKEND}"
                     // CORRECTION: Ajouter le préfixe devops-fullstack/
                    dir('devops-fullstack/backend/backendDevops') {
                        sh "docker build -t ${IMAGE_BACKEND} ."
                    }
                    echo "🚀 Push de l'image backend: ${IMAGE_BACKEND}"
                    sh "docker push ${IMAGE_BACKEND}"

                    echo "🔨 Construction de l'image frontend: ${IMAGE_FRONTEND}"
                     // CORRECTION: Ajouter le préfixe devops-fullstack/
                    dir('devops-fullstack/frontend/frontenddevops') {
                        sh "docker build -t ${IMAGE_FRONTEND} ."
                    }
                    echo "🚀 Push de l'image frontend: ${IMAGE_FRONTEND}"
                    sh "docker push ${IMAGE_FRONTEND}"

                    sh 'docker logout'
                }
            }
        }

        stage('Deploy to Remote Server via SSH') {
             // NOTE IMPORTANTE pour le déploiement :
             // La commande scp copie le docker-compose.yml depuis la racine du workspace Jenkins.
             // Assurez-vous que votre docker-compose.yml est bien à la racine de votre dépôt Git
             // (comme le montre le premier ls -la). Si oui, cette partie n'a pas besoin du préfixe.
            steps {
                echo "🛰️ Déploiement sur le serveur distant via SSH..."
                sshagent(credentials: [env.SSH_CREDENTIALS_ID]) {
                    echo "📄 Copie de docker-compose.yml vers ${REMOTE_DEPLOY_PATH} sur le serveur distant..."
                     // !! REMPLACEZ user@your_server_ip !!
                    sh "scp -o StrictHostKeyChecking=no docker-compose.yml user@your_server_ip:${REMOTE_DEPLOY_PATH}/docker-compose.yml"

                    echo "🚀 Exécution de docker-compose sur le serveur distant..."
                     // !! REMPLACEZ user@your_server_ip !!
                    sh "ssh -o StrictHostKeyChecking=no user@your_server_ip 'cd ${REMOTE_DEPLOY_PATH} && docker-compose pull && docker-compose up -d'"
                }
            }
        }
    } // Fin stages

    post { /* ... */ }
} // Fin pipeline
