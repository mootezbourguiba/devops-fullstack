// Jenkinsfile (Version corrigée)
pipeline {
    agent any // Exécute sur n'importe quel agent disponible

    // Définit les outils à utiliser. Les noms DOIVENT correspondre à ceux
    // configurés dans "Manage Jenkins" -> "Global Tool Configuration"
    tools {
        jdk 'jdk17' // Assurez-vous d'avoir configuré un JDK 17 avec ce nom
        maven 'apache-maven-3.8.6' // Assurez-vous d'avoir configuré Maven avec ce nom
        nodejs 'node-20' // Utilisez la version Node.js configurée dans Jenkins
    }

    environment {
        // Variables pour Docker Hub
        DOCKERHUB_CREDENTIALS_ID = 'dockerhub-credentials' // ID des credentials Docker Hub dans Jenkins
        DOCKERHUB_USERNAME       = "mootezbourguiba365" // Votre nom d'utilisateur Docker Hub
        IMAGE_FRONTEND           = "${DOCKERHUB_USERNAME}/devops-frontend:latest"
        IMAGE_BACKEND            = "${DOCKERHUB_USERNAME}/devops-backend:latest"

        // Variables pour le déploiement SSH
        SSH_CREDENTIALS_ID       = 'ssh-credentials-mon-serveur' // ID des credentials SSH dans Jenkins
        SSH_SERVER_NAME          = 'ssh-mon-serveur' // Nom du serveur SSH configuré dans Jenkins
        REMOTE_DEPLOY_PATH       = '/home/user/devops-app' // !! REMPLACEZ PAR LE VRAI CHEMIN SUR VOTRE SERVEUR !!
    }

    stages {
        stage('Checkout') {
            steps {
                echo 'Récupération du code depuis GitHub...'
                // Utilise la configuration SCM du job Jenkins ("Pipeline script from SCM")
                checkout scm
            }
        }

        stage('Build et Test Backend') {
            steps {
                echo 'Construction et test du backend Java (Spring Boot)...'
                // Change de répertoire vers 'backend/backendDevops'
                dir('devops-fullstack/backend/backendDevops/pom.xml') {
                    // Utilise Maven configuré dans 'tools'
                    sh "'${tool('apache-maven-3.8.6')}/bin/mvn' clean package"
                }
            }
            post {
                success {
                    // Archive le JAR produit (optionnel mais utile)
                    archiveArtifacts artifacts: 'backend/backendDevops/target/*.jar', fingerprint: true
                }
            }
        }

        stage('Build Frontend') {
            steps {
                echo 'Construction du frontend React...'
                // Change de répertoire vers 'frontend'
                dir('frontend') {
                    // Utilise Node/NPM configuré dans 'tools'
                    sh "'${tool('node-20')}/bin/npm' install"
                    sh "'${tool('node-20')}/bin/npm' run build"
                }
            }
            post {
                success {
                    // Archive le build statique (optionnel)
                    archiveArtifacts artifacts: 'frontend/build/**', fingerprint: true
                }
            }
        }

        stage('Build et Push Docker Images') {
            steps {
                echo "Connexion à Docker Hub (${DOCKERHUB_USERNAME})..."
                // Utilise les credentials Docker Hub configurés dans Jenkins
                withCredentials([usernamePassword(credentialsId: env.DOCKERHUB_CREDENTIALS_ID,
                                               passwordVariable: 'DOCKERHUB_PASSWORD',
                                               usernameVariable: 'DOCKERHUB_USER')]) {
                    // Connexion Docker
                    sh "docker login -u '${env.DOCKERHUB_USERNAME}' -p '${DOCKERHUB_PASSWORD}'"

                    echo "Construction de l'image backend: ${IMAGE_BACKEND}"
                    dir('backend/backendDevops') {
                        // Assurez-vous qu'un Dockerfile existe dans ./backend/backendDevops/
                        sh "docker build -t ${IMAGE_BACKEND} ."
                    }
                    echo "Push de l'image backend: ${IMAGE_BACKEND}"
                    sh "docker push ${IMAGE_BACKEND}"

                    echo "Construction de l'image frontend: ${IMAGE_FRONTEND}"
                    dir('frontend') {
                        // Assurez-vous qu'un Dockerfile existe dans ./frontend/
                        sh "docker build -t ${IMAGE_FRONTEND} ."
                    }
                    echo "Push de l'image frontend: ${IMAGE_FRONTEND}"
                    sh "docker push ${IMAGE_FRONTEND}"

                    // Déconnexion Docker (bonne pratique)
                    sh 'docker logout'
                } // Fin withCredentials
            }
        }

        stage('Deploy to Remote Server via SSH') {
            steps {
                echo "Déploiement sur le serveur distant via SSH..."
                // Utilise le plugin SSH Agent pour gérer les clés SSH de manière sécurisée
                sshagent(credentials: [env.SSH_CREDENTIALS_ID]) {
                    // Copie le fichier docker-compose.yml vers le serveur distant
                    echo "Copie de docker-compose.yml vers ${REMOTE_DEPLOY_PATH} sur le serveur distant..."
                    sh "scp -o StrictHostKeyChecking=no docker-compose.yml user@your_server_ip:${REMOTE_DEPLOY_PATH}/docker-compose.yml"

                    echo "Exécution de docker-compose sur le serveur distant..."
                    sh "ssh -o StrictHostKeyChecking=no user@your_server_ip 'cd ${REMOTE_DEPLOY_PATH} && docker-compose pull && docker-compose up -d'"
                }
            }
        }
    }

    post {
        // Actions à exécuter à la fin du pipeline
        always {
            echo 'Nettoyage du workspace...'
            cleanWs()
        }
        success {
            echo 'Pipeline terminé avec succès !'
        }
        failure {
            echo 'Le Pipeline a échoué !'
        }
    }
}
