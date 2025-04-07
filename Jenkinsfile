// Jenkinsfile corrigé (Version Complète)
pipeline {
<<<<<<< HEAD
    agent any

    // DÉFINITIONS DES OUTILS (pas de /*...*/ !)
    tools {
        jdk 'jdk17'                // Vérifiez le nom exact dans Jenkins -> Outils
        maven 'apache-maven-3.8.6' // Vérifiez le nom exact dans Jenkins -> Outils
        nodejs 'node-20'           // Vérifiez le nom exact dans Jenkins -> Outils
    }

    // DÉFINITIONS DE L'ENVIRONNEMENT (pas de /*...*/ !)
    environment {
        DOCKERHUB_CREDENTIALS_ID = 'dockerhub-credentials' // ID dans Jenkins
        DOCKERHUB_USERNAME       = "mootezbourguiba365"    // Votre user Docker Hub
        IMAGE_FRONTEND           = "${DOCKERHUB_USERNAME}/devops-frontend:latest"
        IMAGE_BACKEND            = "${DOCKERHUB_USERNAME}/devops-backend:latest"
        SSH_CREDENTIALS_ID       = 'ssh-credentials-mon-serveur' // ID dans Jenkins
        REMOTE_DEPLOY_PATH       = '/home/user/devops-app'       // !! METTEZ VOTRE VRAI CHEMIN SERVEUR !!
=======
    agent { docker { image 'maven:3.8.4-openjdk-17' } }
    environment {
        DOCKERHUB_USERNAME = "mootezbourguiba" // REMPLACEZ PAR VOTRE NOM D'UTILISATEUR
        IMAGE_FRONTEND = "${DOCKERHUB_USERNAME}/devops-frontend:latest"
        IMAGE_BACKEND = "${DOCKERHUB_USERNAME}/devops-backend:latest"
>>>>>>> 5fc7a19 (Ajout des nouveaux fichiers et mises à jour le 07/04)
    }
    stages {
        stage('Checkout') {
            steps {
<<<<<<< HEAD
                echo '📥 Récupération du code depuis GitHub...'
                checkout scm
                echo '>>> Contenu de la racine du workspace après checkout:'
                sh 'ls -la'
            }
        }

        stage('Build et Test Backend') {
            steps {
                echo '⚙️ Construction et test du backend Spring Boot...'
                dir('devops-fullstack/backend/backendDevops') { // Avec préfixe
                    sh 'echo ">>> Vérification du contenu du répertoire $(pwd):"'
                    sh 'ls -la'
                    sh "mvn clean package"
                }
            }
            post {
                success {
                    archiveArtifacts artifacts: 'devops-fullstack/backend/backendDevops/target/*.jar', fingerprint: true // Avec préfixe
                }
=======
                git 'https://github.com/mootezbourguiba/devops-fullstack.git'
            }
        }
        stage('Build et test Backend') {
            steps {
                sh 'cd backendDevops && mvn clean install -DskipTests'
                sh 'cd backendDevops && mvn test'
>>>>>>> 5fc7a19 (Ajout des nouveaux fichiers et mises à jour le 07/04)
            }
        }
        stage('Build Frontend') {
            steps {
<<<<<<< HEAD
                echo '🌐 Construction du frontend React...'
                dir('devops-fullstack/frontend/frontenddevops') { // Avec préfixe
                    sh 'echo ">>> Vérification du contenu du répertoire $(pwd):"'
                    sh 'ls -la'
                    sh "npm install"
                    sh "npm run build"
                }
            }
            post {
                success {
                    archiveArtifacts artifacts: 'devops-fullstack/frontend/frontenddevops/build/**', fingerprint: true // Avec préfixe
                }
            }
        }

        stage('Build et Push Docker Images') {
            steps {
                echo "🐳 Connexion à Docker Hub (${DOCKERHUB_USERNAME})..."
                // DÉFINITION DES CREDENTIALS (pas de /*...*/ !)
                withCredentials([usernamePassword(credentialsId: env.DOCKERHUB_CREDENTIALS_ID,
                                               passwordVariable: 'DOCKERHUB_PASSWORD',
                                               usernameVariable: 'DOCKERHUB_USER')]) {
                    sh "docker login -u '${env.DOCKERHUB_USERNAME}' -p '${DOCKERHUB_PASSWORD}'"

                    echo "🔨 Construction de l'image backend: ${IMAGE_BACKEND}"
                    dir('devops-fullstack/backend/backendDevops') { // Avec préfixe
                        sh "docker build -t ${IMAGE_BACKEND} ."
                    }
                    echo "🚀 Push de l'image backend: ${IMAGE_BACKEND}"
                    sh "docker push ${IMAGE_BACKEND}"

                    echo "🔨 Construction de l'image frontend: ${IMAGE_FRONTEND}"
                    dir('devops-fullstack/frontend/frontenddevops') { // Avec préfixe
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
                     // !! REMPLACEZ user@your_server_ip !!
                    sh "scp -o StrictHostKeyChecking=no docker-compose.yml user@your_server_ip:${REMOTE_DEPLOY_PATH}/docker-compose.yml"

                    echo "🚀 Exécution de docker-compose sur le serveur distant..."
                     // !! REMPLACEZ user@your_server_ip !!
                    sh "ssh -o StrictHostKeyChecking=no user@your_server_ip 'cd ${REMOTE_DEPLOY_PATH} && docker-compose pull && docker-compose up -d'"
                }
            }
        }
    } // Fin stages

    // DÉFINITION DU BLOC POST (pas de /*...*/ !)
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
} // Fin pipeline
=======
                sh 'cd frontend && npm install'
                sh 'cd frontend && npm run build'
            }
        }
        stage('Docker Build') {
            steps {
                sh 'docker-compose -f docker/docker-compose.yml build'
            }
        }
        stage('Docker Push') {
            steps {
                withCredentials([usernamePassword(credentialsId: 'dockerhub-credentials', 
                                               passwordVariable: 'DOCKERHUB_PASSWORD', 
                                               usernameVariable: 'DOCKERHUB_USERNAME')]) {
                    sh "docker login -u ${DOCKERHUB_USERNAME} -p ${DOCKERHUB_PASSWORD}"
                    sh "docker tag docker-frontend ${IMAGE_FRONTEND}"
                    sh "docker push ${IMAGE_FRONTEND}"
                    sh "docker tag docker-backend ${IMAGE_BACKEND}"
                    sh "docker push ${IMAGE_BACKEND}"
                    sh "docker logout"
                }
            }
        }
        stage('Deploy') {
            steps {
                sshPublisher(publishers: [sshPublisherDesc(configName: 'VotreServeurDistant',
                                                      transfers: [sshTransfer(cleanRemote: false, 
                                                                            excludes: '', 
                                                                            remoteDirectory: '/chemin/vers/deploy', 
                                                                            removePrefix: '', 
                                                                            sourceFiles: 'docker/docker-compose.yml')]
                )])
                sshCommand remoteCommand: 'docker-compose -f /chemin/vers/deploy/docker-compose.yml up -d'
            }
        }
    }
}
>>>>>>> 5fc7a19 (Ajout des nouveaux fichiers et mises à jour le 07/04)
