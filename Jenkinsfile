// Jenkinsfile avec étape de test frontend
pipeline {
    agent any // Utilise n'importe quel agent disponible

    // Définitions des outils à utiliser (configurés dans Jenkins -> Global Tool Configuration)
    tools {
        jdk 'jdk17'                // Nom de la config JDK 17 dans Jenkins
        maven 'apache-maven-3.8.6' // Nom de la config Maven dans Jenkins
        nodejs 'node-20'           // Nom de la config NodeJS dans Jenkins
    }

    // Variables d'environnement pour le pipeline
    environment {
        DOCKERHUB_CREDENTIALS_ID = 'dockerhub-credentials' // ID des credentials Docker Hub dans Jenkins
        DOCKERHUB_USERNAME       = "mootezbourguiba73"     // TON username Docker Hub
        IMAGE_FRONTEND           = "${env.DOCKERHUB_USERNAME}/devops-frontend:latest" // Nom de l'image frontend
        IMAGE_BACKEND            = "${env.DOCKERHUB_USERNAME}/devops-backend:latest"  // Nom de l'image backend
        SSH_CREDENTIALS_ID       = 'ssh-credentials-mon-serveur' // ID des credentials SSH dans Jenkins
        REMOTE_DEPLOY_PATH       = '/home/user/devops-app'       // !! METTRE LE VRAI CHEMIN SUR LE SERVEUR DISTANT !!
    }

    stages {
        stage('Checkout') {
            steps {
                echo '📥 Récupération du code depuis GitHub...'
                checkout scm // Récupère le code depuis le SCM configuré dans le job Jenkins
                echo '>>> Contenu de la racine du workspace après checkout:'
                sh 'ls -la' // Affiche le contenu pour vérifier (utile pour le debug)
            }
        }

        stage('Build et Test Backend') {
            steps {
                echo '⚙️ Construction et test du backend Spring Boot...'
                // Exécute les commandes dans le sous-dossier du backend
                dir('devops-fullstack/backend/backendDevops') {
                    sh 'echo ">>> Vérification du contenu du répertoire $(pwd):"'
                    sh 'ls -la' // Affiche le contenu pour vérifier
                    // Utilise l'outil Maven configuré
                    // 'package' compile, teste, et crée le JAR
                    sh "mvn clean package"
                }
            }
            post {
                success {
                    // Archive le JAR créé en cas de succès
                    archiveArtifacts artifacts: 'devops-fullstack/backend/backendDevops/target/*.jar', fingerprint: true
                }
            }
        }

        // *** STAGE MODIFIÉ ***
        stage('Build et Test Frontend') { // Nom du stage mis à jour
            steps {
                echo '🌐 Construction et test du frontend React...' // Message mis à jour
                 // Exécute les commandes dans le sous-dossier du frontend
                dir('devops-fullstack/frontend/frontenddevops') {
                    sh 'echo ">>> Vérification du contenu du répertoire $(pwd):"'
                    sh 'ls -la' // Affiche le contenu pour vérifier
                    // Utilise l'outil NodeJS configuré
                    sh "npm install"      // Installe les dépendances
                    sh "npm test -- --watchAll=false" // *** LIGNE AJOUTEE / MODIFIEE ***
                    sh "npm run build"   // Construit l'application React pour la production
                }
            }
            post {
                success {
                     // Archive le build frontend créé en cas de succès
                    archiveArtifacts artifacts: 'devops-fullstack/frontend/frontenddevops/build/**', fingerprint: true
                }
            }
        }
        // *** FIN STAGE MODIFIÉ ***

        stage('Build et Push Docker Images') {
            steps {
                echo "🐳 Connexion à Docker Hub (${DOCKERHUB_USERNAME})..."
                // Utilise les credentials Jenkins pour se connecter à Docker Hub
                withCredentials([usernamePassword(credentialsId: env.DOCKERHUB_CREDENTIALS_ID,
                                               passwordVariable: 'DOCKERHUB_PASSWORD', // La variable contiendra le mot de passe
                                               usernameVariable: 'DOCKERHUB_USER')]) { // La variable contiendra le username (peut être différent de DOCKERHUB_USERNAME)

                    // Connexion à Docker Hub en utilisant les variables injectées
                    sh "docker login -u '${env.DOCKERHUB_USERNAME}' -p '${DOCKERHUB_PASSWORD}'"

                    echo "🔨 Construction de l'image backend: ${IMAGE_BACKEND}"
                    // Construit l'image depuis le sous-dossier backend
                    dir('devops-fullstack/backend/backendDevops') {
                        sh "docker build -t ${IMAGE_BACKEND} ."
                    }
                    echo "🚀 Push de l'image backend: ${IMAGE_BACKEND}"
                    sh "docker push ${IMAGE_BACKEND}"

                    echo "🔨 Construction de l'image frontend: ${IMAGE_FRONTEND}"
                    // Construit l'image depuis le sous-dossier frontend
                    dir('devops-fullstack/frontend/frontenddevops') {
                        sh "docker build -t ${IMAGE_FRONTEND} ."
                    }
                    echo "🚀 Push de l'image frontend: ${IMAGE_FRONTEND}"
                    sh "docker push ${IMAGE_FRONTEND}"

                    // Déconnexion de Docker Hub (bonne pratique)
                    sh 'docker logout'
                }
            }
        }

        stage('Deploy to Remote Server via SSH') {
             // Condition : Exécute seulement si on est sur la branche 'main'
             when { branch 'main' }
             steps {
                echo "🛰️ Déploiement sur le serveur distant via SSH..."
                // Utilise les credentials SSH Jenkins
                sshagent(credentials: [env.SSH_CREDENTIALS_ID]) {
                    echo "📄 Copie de docker-compose.yml vers ${REMOTE_DEPLOY_PATH} sur le serveur distant..."
                     // !! REMPLACE user@your_server_ip PAR LES VRAIES VALEURS !!
                     // Suppose que le docker-compose pour le déploiement est à la racine du projet Git
                    sh "scp -o StrictHostKeyChecking=no docker-compose.yml user@your_server_ip:${REMOTE_DEPLOY_PATH}/docker-compose.yml"

                    echo "🚀 Exécution de docker-compose sur le serveur distant..."
                     // !! REMPLACE user@your_server_ip PAR LES VRAIES VALEURS !!
                    sh "ssh -o StrictHostKeyChecking=no user@your_server_ip 'cd ${REMOTE_DEPLOY_PATH} && docker-compose pull && docker-compose up -d'"
                }
            }
        }
    } // Fin stages

    // Actions à exécuter après la fin du pipeline
    post {
        always {
            echo '🧹 Nettoyage du workspace...'
            cleanWs() // Nettoie le workspace Jenkins
        }
        success {
            echo '✅ Pipeline terminé avec succès !'
        }
        failure {
            echo '❌ Le Pipeline a échoué !'
        }
    }
} // Fin pipeline