// Jenkinsfile (Version améliorée)
pipeline {
    agent any // Exécute sur n'importe quel agent disponible

    // Définit les outils à utiliser. Les noms DOIVENT correspondre à ceux
    // configurés dans "Manage Jenkins" -> "Global Tool Configuration"
    tools {
        // Assurez-vous d'avoir configuré un JDK 17 avec le nom 'jdk17'
        jdk 'jdk17'
        // Assurez-vous d'avoir configuré Maven avec ce nom
        maven 'apache-maven-3.8.6' // Ou le nom que vous avez choisi
        // Assurez-vous d'avoir configuré NodeJS avec ce nom
        nodejs 'node-20'           // Ou le nom/version que vous avez choisi
    }

    environment {
        // Variables pour Docker Hub
        DOCKERHUB_CREDENTIALS_ID = 'dockerhub-credentials' // ID des credentials Docker Hub dans Jenkins
        DOCKERHUB_USERNAME       = "mootezbourguiba365"    // Votre nom d'utilisateur Docker Hub
        IMAGE_FRONTEND           = "${DOCKERHUB_USERNAME}/devops-frontend:latest"
        IMAGE_BACKEND            = "${DOCKERHUB_USERNAME}/devops-backend:latest"
        // Variables pour le déploiement SSH
        SSH_CREDENTIALS_ID       = 'ssh-credentials-mon-serveur' // ID des credentials SSH dans Jenkins
        SSH_SERVER_NAME          = 'ssh-mon-serveur'             // Nom du serveur SSH configuré dans Jenkins
        REMOTE_DEPLOY_PATH       = '/home/user/devops-app'       // !! REMPLACEZ PAR LE VRAI CHEMIN SUR VOTRE SERVEUR !!
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
                // Change de répertoire vers 'backend'
                dir('backend') {
                    // Utilise Maven configuré dans 'tools'
                    // 'mvn clean package' compile, teste et crée le JAR
                    sh "'${tool('apache-maven-3.8.6')}/bin/mvn' clean package"
                }
            }
            post {
                success {
                    // Archive le JAR produit (optionnel mais utile)
                    archiveArtifacts artifacts: 'backend/target/*.jar', fingerprint: true
                }
            }
        }

        stage('Build Frontend') {
            steps {
                echo 'Construction du frontend React...'
                // Change de répertoire vers 'frontend'
                dir('frontend') {
                    // Utilise Node/NPM configuré dans 'tools'
                    sh "'${tool('node-16')}/bin/npm' install"
                    // sh "'${tool('node-16')}/bin/npm' run test" // Décommentez si vous avez des tests frontend
                    sh "'${tool('node-16')}/bin/npm' run build"
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
                                               usernameVariable: 'DOCKERHUB_USER')]) { // Note: usernameVariable est redondant si déjà dans env

                    // Connexion Docker
                    sh "docker login -u '${env.DOCKERHUB_USERNAME}' -p '${DOCKERHUB_PASSWORD}'"

                    echo "Construction de l'image backend: ${IMAGE_BACKEND}"
                    dir('backend') {
                        // Assurez-vous qu'un Dockerfile existe dans ./backend/
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
                 // Assurez-vous d'avoir configuré les credentials SSH (avec clé privée) dans Jenkins (ID: env.SSH_CREDENTIALS_ID)
                 sshagent(credentials: [env.SSH_CREDENTIALS_ID]) {
                     // Copie le fichier docker-compose.yml vers le serveur distant
                     // Assurez-vous que docker-compose.yml est à la racine du projet Git
                     // Le plugin SSH Publisher doit être installé et configuré si vous préférez cette méthode
                     // Sinon, utiliser sh avec scp:
                     // sh "scp docker-compose.yml user@your_server_ip:${REMOTE_DEPLOY_PATH}/docker-compose.yml"

                     echo "Copie de docker-compose.yml vers ${REMOTE_DEPLOY_PATH} sur le serveur distant..."
                     // Exemple avec scp (nécessite que la clé publique soit sur le serveur distant)
                     // Assurez-vous que l'utilisateur Jenkins a accès à la commande ssh/scp et que l'hôte est connu (ou utiliser -o StrictHostKeyChecking=no)
                     // REMPLACEZ user@your_server_ip par les vraies informations
                     // sh "scp -o StrictHostKeyChecking=no docker-compose.yml user@your_server_ip:${REMOTE_DEPLOY_PATH}/"

                     // NOTE IMPORTANTE: La copie de fichier via sshagent/scp peut être complexe.
                     // Le plugin "SSH Publisher" configuré avec le nom 'ssh-mon-serveur' (comme dans votre exemple original)
                     // est souvent plus simple pour le transfert de fichiers. Vérifions cette approche:

                     // Assurez-vous que le plugin "SSH Publisher" est installé.
                     // Allez dans "Manage Jenkins" -> "Configure System" -> "SSH Servers" pour ajouter votre serveur
                     // avec le nom 'ssh-mon-serveur' (env.SSH_SERVER_NAME) et les credentials SSH corrects.
                     echo "Transfert de docker-compose.yml via SSH Publisher..."
                     sshPublisher(publishers: [
                         sshPublisherDesc(
                             configName: env.SSH_SERVER_NAME, // Nom du serveur configuré dans Jenkins
                             transfers: [
                                 sshTransfer(
                                     sourceFiles: 'docker-compose.yml', // Fichier à la racine du workspace Jenkins
                                     removePrefix: '',
                                     remoteDirectory: env.REMOTE_DEPLOY_PATH, // Dossier cible sur le serveur
                                     execCommand: '', // Pas de commande exécutée après le transfert ici
                                     cleanRemote: false
                                 )
                             ],
                             verbose: true // Pour voir plus de logs
                         )
                     ])

                     echo "Exécution de docker-compose sur le serveur distant..."
                     // Exécute docker-compose sur le serveur distant
                     // Assurez-vous que Docker et Docker Compose sont installés sur le serveur distant
                     // Utilisation de sshCommand (du plugin SSH Pipeline Steps) ou execCommand de sshPublisher
                     // Ici, on utilise execCommand dans un second transfert (bidouille courante) ou sshCommand séparément.
                     // Utilisons sshCommand pour la clarté (nécessite le plugin "SSH Pipeline Steps")

                     // Vous pouvez aussi utiliser la section `execCommand` du sshPublisherDesc précédent après le transfert.
                     // Exemple avec sshCommand (nécessite le plugin SSH Pipeline Steps, plus récent que SSH Publisher pour les commandes) :
                      sh "ssh -o StrictHostKeyChecking=no user@your_server_ip 'cd ${REMOTE_DEPLOY_PATH} && docker-compose pull && docker-compose up -d'"

                     // Ou si vous préférez utiliser execCommand de SSH Publisher (configuré ci-dessus):
                     // Modifiez le sshPublisherDesc ci-dessus pour inclure execCommand:
                     // execCommand: "cd ${REMOTE_DEPLOY_PATH} && docker-compose pull && docker-compose up -d"
                     // (et supprimez le bloc sshCommand ci-dessus)


                 } // Fin sshagent (si utilisé pour scp/ssh)
            }
        }
    } // Fin stages

    post {
        // Actions à exécuter à la fin du pipeline
        always {
            echo 'Nettoyage du workspace...'
            // Supprime les fichiers pour économiser de l'espace
            cleanWs()
        }
        success {
             echo 'Pipeline terminé avec succès !'
             // Envoyer une notification ?
        }
        failure {
             echo 'Le Pipeline a échoué !'
             // Envoyer une notification d'échec ?
        }
    } // Fin post
} // Fin pipeline