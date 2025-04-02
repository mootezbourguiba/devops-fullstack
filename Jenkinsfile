// Jenkinsfile (Recommandé pour votre configuration)
pipeline {
    // Utiliser UN agent pour tout le pipeline
    agent any

    // Définir les outils qui seront installés et mis dans le PATH par Jenkins
    tools {
        jdk 'jdk17'                // Doit correspondre au nom dans Jenkins -> Outils
        maven 'apache-maven-3.8.6' // Doit correspondre au nom dans Jenkins -> Outils
        nodejs 'node-20'           // Doit correspondre au nom dans Jenkins -> Outils
    }

    environment {
        DOCKERHUB_CREDENTIALS_ID = 'dockerhub-credentials'
        DOCKERHUB_USERNAME       = "mootezbourguiba365"
        IMAGE_FRONTEND           = "${DOCKERHUB_USERNAME}/devops-frontend:latest"
        IMAGE_BACKEND            = "${DOCKERHUB_USERNAME}/devops-backend:latest"
        SSH_CREDENTIALS_ID       = 'ssh-credentials-mon-serveur'
        REMOTE_DEPLOY_PATH       = '/home/user/devops-app' // !! METTEZ VOTRE VRAI CHEMIN !!
    }

    stages {
        stage('Checkout') {
            // Pas besoin de agent ici, car défini au niveau pipeline
            steps {
                echo '📥 Récupération du code depuis GitHub...'
                checkout scm
            }
        }

        stage('Build et Test Backend') {
            // Pas besoin de agent ici
            steps {
                echo '⚙️ Construction et test du backend Spring Boot...'
                // IMPORTANT: Vérifiez le chemin exact après le checkout.
                // Est-ce 'backend/backendDevops' ou 'devops-fullstack/backend/backendDevops'?
                // Regardez la sortie du 'ls -la' précédent si besoin. Supposons que c'est 'backend/backendDevops' pour l'instant.
                dir('backend/backendDevops') {
                    echo ">>> Vérification du contenu du répertoire ($(pwd)):"
                    sh 'ls -la'
                    // Utilise directement mvn car il est dans le PATH grâce à la directive 'tools'
                    sh "mvn clean package"
                }
            }
            post {
                success {
                    // Ajustez le chemin si nécessaire
                    archiveArtifacts artifacts: 'backend/backendDevops/target/*.jar', fingerprint: true
                }
            }
        }

        stage('Build Frontend') {
            // Pas besoin de agent ici
            steps {
                echo '🌐 Construction du frontend React...'
                // IMPORTANT: Vérifiez le chemin exact. Supposons 'frontend/frontenddevops'.
                dir('frontend/frontenddevops') {
                     echo ">>> Vérification du contenu du répertoire ($(pwd)):"
                     sh 'ls -la'
                    // Utilise directement npm car il est dans le PATH grâce à la directive 'tools'
                    sh "npm install"
                    sh "npm run build"
                }
            }
            post {
                success {
                    // Ajustez le chemin si nécessaire
                    archiveArtifacts artifacts: 'frontend/frontenddevops/build/**', fingerprint: true
                }
            }
        }

        stage('Build et Push Docker Images') {
            // Pas besoin de agent ici
            steps {
                echo "🐳 Connexion à Docker Hub (${DOCKERHUB_USERNAME})..."
                withCredentials([usernamePassword(credentialsId: env.DOCKERHUB_CREDENTIALS_ID,
                                               passwordVariable: 'DOCKERHUB_PASSWORD',
                                               usernameVariable: 'DOCKERHUB_USER')]) {
                    sh "docker login -u '${env.DOCKERHUB_USERNAME}' -p '${DOCKERHUB_PASSWORD}'"

                    echo "🔨 Construction de l'image backend: ${IMAGE_BACKEND}"
                    // Ajustez le chemin si nécessaire
                    dir('backend/backendDevops') {
                        // Vérifiez que Dockerfile est ici
                        sh "docker build -t ${IMAGE_BACKEND} ."
                    }
                    echo "🚀 Push de l'image backend: ${IMAGE_BACKEND}"
                    sh "docker push ${IMAGE_BACKEND}"

                    echo "🔨 Construction de l'image frontend: ${IMAGE_FRONTEND}"
                     // Ajustez le chemin si nécessaire
                    dir('frontend/frontenddevops') {
                         // Vérifiez que Dockerfile est ici
                        sh "docker build -t ${IMAGE_FRONTEND} ."
                    }
                    echo "🚀 Push de l'image frontend: ${IMAGE_FRONTEND}"
                    sh "docker push ${IMAGE_FRONTEND}"

                    sh 'docker logout'
                } // Fin withCredentials
            }
        }

        stage('Deploy to Remote Server via SSH') {
            // Pas besoin de agent ici
            steps {
                echo "🛰️ Déploiement sur le serveur distant via SSH..."
                sshagent(credentials: [env.SSH_CREDENTIALS_ID]) {
                    echo "📄 Copie de docker-compose.yml vers ${REMOTE_DEPLOY_PATH} sur le serveur distant..."
                    // !! REMPLACEZ user@your_server_ip par les vraies infos !!
                    sh "scp -o StrictHostKeyChecking=no docker-compose.yml user@your_server_ip:${REMOTE_DEPLOY_PATH}/docker-compose.yml"

                    echo "🚀 Exécution de docker-compose sur le serveur distant..."
                     // !! REMPLACEZ user@your_server_ip par les vraies infos !!
                    sh "ssh -o StrictHostKeyChecking=no user@your_server_ip 'cd ${REMOTE_DEPLOY_PATH} && docker-compose pull && docker-compose up -d'"
                }
            }
        }
    } // Fin stages

    post {
        always {
            echo '🧹 Nettoyage du workspace...'
            cleanWs()
        }
        success {
             echo '✅ Pipeline terminé avec succès !'
        }
        failure {
             echo '❌ Le Pipeline a échoué !'
        }
    } // Fin post
} // Fin pipeline
