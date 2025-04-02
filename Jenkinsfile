// Jenkinsfile (Recommandé pour votre configuration)
pipeline {
    agent any

    tools {
        jdk 'jdk17'
        maven 'apache-maven-3.8.6'
        nodejs 'node-20'
    }

    environment {
        DOCKERHUB_CREDENTIALS_ID = 'dockerhub-credentials'
        DOCKERHUB_USERNAME       = "mootezbourguiba365"
        IMAGE_FRONTEND           = "${DOCKERHUB_USERNAME}/devops-frontend:latest"
        IMAGE_BACKEND            = "${DOCKERHUB_USERNAME}/devops-backend:latest"
        SSH_CREDENTIALS_ID       = 'ssh-credentials-mon-serveur'
        REMOTE_DEPLOY_PATH       = '/home/user/devops-app'
    }

    stages {
        stage('Checkout') {
            steps {
                echo '📥 Récupération du code depuis GitHub...'
                checkout scm
                echo '>>> Contenu de la racine du workspace après checkout:'
            sh 'ls -la' // <-- AJOUTER ICI pour voir la racine
            }
        }

        stage('Build et Test Backend') {
            steps {
                echo '⚙️ Construction et test du backend Spring Boot...'
                dir('backend/backendDevops') {
                    echo ">>> Vérification du contenu du répertoire ($PWD):"
                    sh 'ls -la'
                    sh "mvn clean package"
                }
            }
            post {
                success {
                    archiveArtifacts artifacts: 'backend/backendDevops/target/*.jar', fingerprint: true
                }
            }
        }

        stage('Build Frontend') {
            steps {
                echo '🌐 Construction du frontend React...'
                dir('frontend/frontenddevops') {
                    echo ">>> Vérification du contenu du répertoire ($PWD):"
                    sh 'ls -la'
                    sh "npm install"
                    sh "npm run build"
                }
            }
            post {
                success {
                    archiveArtifacts artifacts: 'frontend/frontenddevops/build/**', fingerprint: true
                }
            }
        }

        stage('Build et Push Docker Images') {
            steps {
                echo "🐳 Connexion à Docker Hub (${DOCKERHUB_USERNAME})..."
                withCredentials([usernamePassword(credentialsId: env.DOCKERHUB_CREDENTIALS_ID,
                                               passwordVariable: 'DOCKERHUB_PASSWORD',
                                               usernameVariable: 'DOCKERHUB_USER')]) {
                    sh "docker login -u '${env.DOCKERHUB_USERNAME}' -p '${DOCKERHUB_PASSWORD}'"
                    
                    echo "🔨 Construction de l'image backend: ${IMAGE_BACKEND}"
                    dir('backend/backendDevops') {
                        sh "docker build -t ${IMAGE_BACKEND} ."
                    }
                    echo "🚀 Push de l'image backend: ${IMAGE_BACKEND}"
                    sh "docker push ${IMAGE_BACKEND}"
                    
                    echo "🔨 Construction de l'image frontend: ${IMAGE_FRONTEND}"
                    dir('frontend/frontenddevops') {
                        sh "docker build -t ${IMAGE_FRONTEND} ."
                    }
                    echo "🚀 Push de l'image frontend: ${IMAGE_FRONTEND}"
                    sh "docker push ${IMAGE_FRONTEND}"
                    
                    sh 'docker logout'
                }
            }
        }

        stage('Deploy to Remote Server via SSH') {
            steps {
                echo "🛰️ Déploiement sur le serveur distant via SSH..."
                sshagent(credentials: [env.SSH_CREDENTIALS_ID]) {
                    echo "📄 Copie de docker-compose.yml vers ${REMOTE_DEPLOY_PATH} sur le serveur distant..."
                    sh "scp -o StrictHostKeyChecking=no docker-compose.yml user@your_server_ip:${REMOTE_DEPLOY_PATH}/docker-compose.yml"
                    
                    echo "🚀 Exécution de docker-compose sur le serveur distant..."
                    sh "ssh -o StrictHostKeyChecking=no user@your_server_ip 'cd ${REMOTE_DEPLOY_PATH} && docker-compose pull && docker-compose up -d'"
                }
            }
        }
    }

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
    }
}
