pipeline {
    agent none // Désactivé pour une gestion plus fine par stage

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
            agent any
            steps {
                echo '📥 Récupération du code depuis GitHub...'
                checkout scm
            }
        }

        stage('Build et Test Backend') {
            agent {
                docker {
                    image 'maven:3.8.6-openjdk-17' // Image avec JDK 17
                    args '-v $HOME/.m2:/root/.m2'
                }
            }
            steps {
                echo '⚙️ Construction et test du backend Spring Boot...'
                dir('devops-fullstack/backend/backendDevops') {
                    sh """
                        set -e
                        echo "📂 Chemin actuel : \$(pwd)"
                        mvn clean package
                    """
                }
            }
            post {
                success {
                    archiveArtifacts artifacts: 'devops-fullstack/backend/backendDevops/target/*.jar', fingerprint: true
                }
            }
        }

        stage('Build Frontend') {
            agent {
                docker {
                    image 'node:20-alpine' // Image Node.js 20
                    args '-u root'
                }
            }
            steps {
                echo '🌐 Construction du frontend React...'
                dir('frontend') {
                    sh """
                        set -e
                        npm install
                        npm run build
                    """
                }
            }
            post {
                success {
                    archiveArtifacts artifacts: 'frontend/build/**', fingerprint: true
                }
            }
        }

        // Les autres stages (Docker et déploiement) restent inchangés
    }

    // Configuration post-build inchangée
}
