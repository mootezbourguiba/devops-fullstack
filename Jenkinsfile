// Jenkinsfile FINAL v2 (avec outil Git explicite et modif PATH)
pipeline {
    agent any // Utilise n'importe quel agent Jenkins disponible

    // Définitions des outils à utiliser (configurés dans Jenkins -> Global Tool Configuration)
    tools {
        jdk 'jdk17'                // Assure-toi que ce nom correspond à ta config Jenkins
        maven 'apache-maven-3.8.6' // Assure-toi que ce nom correspond
        nodejs 'node-20'           // Assure-toi que ce nom correspond
        git 'Default'              // *** AJOUTÉ: Nom de l'outil Git configuré dans Jenkins ***
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

        // --- Modification du PATH ---
        // Ajoute le répertoire 'bin' de l'outil Git au début du PATH
        PATH = "${tool 'Default'}/bin:${env.PATH}" // *** AJOUTÉ: Utilise le nom de l'outil Git ***
    }

    stages {
        stage('1. Checkout') {
            steps {
                echo "📥 [${env.BRANCH_NAME}] Récupération du code depuis GitHub..."
                // Jenkins devrait maintenant utiliser le Git configuré car il est dans le PATH
                checkout scm
                echo '>>> Contenu du workspace après checkout:'
                sh 'ls -la'
                echo '>>> Vérification de la version de Git utilisée:'
                sh 'git --version' // *** AJOUTÉ pour DEBUG ***
            }
        }

        stage('2. Build et Test Backend') {
            steps {
                echo "⚙️ [${env.BRANCH_NAME}] Construction et test du backend..."
                dir('devops-fullstack/backend/backendDevops') {
                    sh 'echo ">>> Dans $(pwd)"'
                     // Utilise withEnv pour s'assurer que Maven utilise le bon JDK
                     // (Même si on l'a mis dans PATH global, redondance pour assurer)
                    withEnv(["JAVA_HOME=${tool 'jdk17'}", "PATH+MAVEN=${tool 'apache-maven-3.8.6'}/bin", "PATH+JDK=${tool 'jdk17'}/bin"]) {
                        sh 'echo ">>> JAVA_HOME utilisé: $JAVA_HOME"'
                        sh 'echo ">>> java -version"'
                        sh 'java -version' // Vérifie la version Java
                        sh 'echo ">>> mvn -version"'
                        sh 'mvn -version'  // Vérifie la version Maven et son JDK
                        sh "mvn clean package" // Compile, teste, package
                    }
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
                    // Utilise withEnv pour s'assurer que npm utilise le bon NodeJS
                    withEnv(["NODEJS_HOME=${tool 'node-20'}", "PATH+NODE=${tool 'node-20'}/bin"]) {
                        sh 'echo ">>> node -v"'
                        sh 'node -v' // Vérifie la version node
                        sh 'echo ">>> npm -v"'
                        sh 'npm -v' // Vérifie la version npm
                        sh "npm install"
                        sh "npm test -- --watchAll=false"
                        sh "npm run build"
                    }
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
                        sh "docker build -t ${IMAGE_BACKEND} ."
                    }
                    echo "🚀 [${env.BRANCH_NAME}] Push image backend..."
                    sh "docker push ${IMAGE_BACKEND}"

                    echo "🔨 [${env.BRANCH_NAME}] Build image frontend: ${IMAGE_FRONTEND}"
                    dir('devops-fullstack/frontend/frontenddevops') {
                        sh "docker build -t ${IMAGE_FRONTEND} ."
                    }
                    echo "🚀 [${env.BRANCH_NAME}] Push image frontend..."
                    sh "docker push ${IMAGE_FRONTEND}"

                    echo "🚪 [${env.BRANCH_NAME}] Déconnexion de Docker Hub..."
                    sh 'docker logout'
                }
            }
        }

        stage('5. Deploy to VM via SSH') {
             when { branch 'main' }
             steps {
                echo "🛰️ [${env.BRANCH_NAME}] Déploiement sur VM Ubuntu via SSH (${REMOTE_USER}@${REMOTE_HOST}:${REMOTE_PORT})..."
                sshagent(credentials: [env.SSH_CREDENTIALS_ID]) {

                    echo "📄 Copie de ${PROD_COMPOSE_FILE} vers ${REMOTE_DEPLOY_PATH}/${REMOTE_COMPOSE_FILENAME} sur la VM..."
                    sh "scp -o StrictHostKeyChecking=no -P ${REMOTE_PORT} ${PROD_COMPOSE_FILE} ${REMOTE_USER}@${REMOTE_HOST}:${REMOTE_DEPLOY_PATH}/${REMOTE_COMPOSE_FILENAME}"

                    echo "🚀 Exécution de Docker Compose sur la VM..."
                    sh "ssh -o StrictHostKeyChecking=no -p ${REMOTE_PORT} ${REMOTE_USER}@${REMOTE_HOST} 'cd ${REMOTE_DEPLOY_PATH} && docker compose -f ${REMOTE_COMPOSE_FILENAME} pull && docker compose -f ${REMOTE_COMPOSE_FILENAME} up -d'"
                }
            }
        }
    } // Fin stages

    post {
        always {
            echo '🧹 Nettoyage du workspace Jenkins...'
            cleanWs()
        }
        success {
            echo "✅ [${env.BRANCH_NAME}] Pipeline terminé avec SUCCÈS !"
        }
        failure {
            echo "❌ [${env.BRANCH_NAME}] Pipeline en ÉCHEC !"
        }
    }
} // Fin pipeline