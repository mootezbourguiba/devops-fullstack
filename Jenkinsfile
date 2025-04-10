// Jenkinsfile FINAL v7 (Agent Docker dédié avec installation outils)
pipeline {
    // --- Utilise un agent Docker ---
    agent {
        docker {
            // Utilise une image contenant JDK 17 et Maven. Git y est souvent aussi.
            image 'maven:3.9-eclipse-temurin-17' // Image à jour avec Maven et JDK 17
            // Monte le socket Docker de l'hôte pour pouvoir exécuter des commandes docker DANS le conteneur
            // et monte l'exécutable docker client de l'hôte (adapter chemin si nécessaire sous Windows/Mac)
            // Exécute l'agent en tant que root pour simplifier les permissions sur le socket monté.
            args '-v /var/run/docker.sock:/var/run/docker.sock -v /usr/bin/docker:/usr/bin/docker -u root'
            // ATTENTION: --user root et le montage du socket Docker ont des implications de sécurité.
            // En production, utiliser des solutions plus sécurisées (agents dédiés, rootless Docker).
            reuseNode true // Réutilise le conteneur pour les étapes si possible
        }
    }

    // --- Outils gérés par Jenkins pour Nodejs (car pas dans l'image maven) ---
    tools {
        nodejs 'node-20' // Assure-toi que ce nom correspond à ta config Jenkins
        // Git et JDK/Maven sont fournis par l'agent Docker
        // On peut garder Git ici au cas où celui de l'image pose problème
        git 'Default'    // Assure-toi que ce nom correspond
    }

    // Variables d'environnement pour le pipeline
    environment {
        // --- Credentials Jenkins ---
        DOCKERHUB_CREDENTIALS_ID = 'dockerhub-credentials'
        SSH_CREDENTIALS_ID       = 'ssh-credentials-mon-serveur'

        // --- Configuration Docker Hub ---
        DOCKERHUB_USERNAME       = "mootezbourguiba73"     // TON username Docker Hub
        IMAGE_NAME_BACKEND       = "devops-backend"
        IMAGE_NAME_FRONTEND      = "devops-frontend"
        IMAGE_BACKEND            = "${env.DOCKERHUB_USERNAME}/${env.IMAGE_NAME_BACKEND}:latest"
        IMAGE_FRONTEND           = "${env.DOCKERHUB_USERNAME}/${env.IMAGE_NAME_FRONTEND}:latest"

        // --- Configuration Déploiement SSH ---
        REMOTE_USER              = "mootez"                              // Username sur la VM Ubuntu
        REMOTE_HOST              = "localhost"                           // Connexion via redirection de port VBox
        REMOTE_PORT              = "2222"                                // Port hôte redirigé
        REMOTE_DEPLOY_PATH       = "/home/${env.REMOTE_USER}/devops-app" // Chemin sur la VM
        PROD_COMPOSE_FILE        = "docker-compose.prod.yml"             // Fichier à la racine Git
        REMOTE_COMPOSE_FILENAME  = "docker-compose.yml"                  // Nom sur le serveur distant

        // --- PATH pour Nodejs et Git (JDK/Maven via agent) ---
        NODEJS_HOME = tool 'node-20'
        // Préfère ajouter explicitement au PATH plutôt que de surcharger complètement
        PATH = "${tool 'Default'}/bin:${NODEJS_HOME}/bin:${env.PATH}"
    }

    stages {
        stage('1. Preparation Agent & Verification Outils') {
            steps {
                echo "🔧 Préparation de l'agent Docker et vérification des outils..."
                // Met à jour apt et installe les outils manquants dans l'agent
                // openssh-client pour scp/ssh
                // docker-ce-cli au cas où le montage ne suffirait pas
                sh '''
                    apt-get update && apt-get install -y --no-install-recommends openssh-client docker-ce-cli || echo "[WARN] Installation tools échouée (peut-être déjà présents ?)"
                    echo "--- Versions des outils DANS L'AGENT ---"
                    echo "[INFO] Git:" && (git --version || echo " Non trouvé")
                    echo "[INFO] Java:" && (java -version || echo " Non trouvé")
                    echo "[INFO] Maven:" && (mvn -v || echo " Non trouvé")
                    echo "[INFO] Node:" && (node -v || echo " Non trouvé")
                    echo "[INFO] NPM:" && (npm -v || echo " Non trouvé")
                    echo "[INFO] Docker Client:" && (docker --version || echo " Non trouvé")
                    echo "[INFO] Docker Compose:" && (docker compose version || echo " Non trouvé")
                    echo "[INFO] scp:" && (which scp || echo " Non trouvé")
                    echo "[INFO] ssh:" && (which ssh || echo " Non trouvé")
                    echo "-----------------------------------------"
                '''
            }
        }

        stage('2. Checkout') { // Renuméroté
            steps {
                echo "📥 [${env.BRANCH_NAME}] Récupération du code depuis GitHub..."
                checkout scm
                echo '>>> Workspace après checkout:'
                sh 'ls -la'
            }
        }

        stage('3. Build et Test Backend') { // Renuméroté
            steps {
                echo "⚙️ [${env.BRANCH_NAME}] Build/Test backend..."
                dir('devops-fullstack/backend/backendDevops') {
                    // Utilise Maven/JDK de l'agent Docker 'maven:3.9-eclipse-temurin-17'
                    sh "mvn clean package" // Compile, teste, package
                }
            }
            post {
                success {
                    archiveArtifacts artifacts: 'devops-fullstack/backend/backendDevops/target/*.jar', fingerprint: true
                }
            }
        }

        stage('4. Build et Test Frontend') { // Renuméroté
            steps {
                echo "🌐 [${env.BRANCH_NAME}] Build/Test frontend..."
                dir('devops-fullstack/frontend/frontenddevops') {
                     // Utilise Node/NPM installé par Jenkins Tool et ajouté au PATH
                    sh "npm install"
                    sh "npm test -- --watchAll=false"
                    sh "npm run build"
                }
            }
            post {
                success {
                    archiveArtifacts artifacts: 'devops-fullstack/frontend/frontenddevops/build/**', fingerprint: true
                }
            }
        }

        stage('5. Build et Push Docker Images') { // Renuméroté
            steps {
                withCredentials([usernamePassword(credentialsId: env.DOCKERHUB_CREDENTIALS_ID,
                                               passwordVariable: 'DOCKERHUB_PASSWORD',
                                               usernameVariable: 'DOCKERHUB_USER')]) {

                    echo "🐳 [${env.BRANCH_NAME}] Login Docker Hub (${env.DOCKERHUB_USERNAME})..."
                    sh "docker login -u '${env.DOCKERHUB_USERNAME}' -p '${DOCKERHUB_PASSWORD}'"

                    echo "🔨 [${env.BRANCH_NAME}] Build backend image: ${IMAGE_BACKEND}"
                    dir('devops-fullstack/backend/backendDevops') {
                        // Le démon Docker de l'hôte exécute le build
                        sh "docker build -t ${IMAGE_BACKEND} ."
                    }
                    echo "🚀 [${env.BRANCH_NAME}] Push backend image..."
                    sh "docker push ${IMAGE_BACKEND}"

                    echo "🔨 [${env.BRANCH_NAME}] Build frontend image: ${IMAGE_FRONTEND}"
                    dir('devops-fullstack/frontend/frontenddevops') {
                         // Le démon Docker de l'hôte exécute le build
                        sh "docker build -t ${IMAGE_FRONTEND} ."
                    }
                    echo "🚀 [${env.BRANCH_NAME}] Push frontend image..."
                    sh "docker push ${IMAGE_FRONTEND}"

                    echo "🚪 [${env.BRANCH_NAME}] Logout Docker Hub..."
                    sh 'docker logout'
                }
            }
        }

        stage('6. Deploy to VM via SSH') { // Renuméroté
             // Condition : S'exécute seulement pour la branche 'main'
             when { branch 'main' }
             steps {
                echo "🛰️ [${env.BRANCH_NAME}] Deploying to VM (${REMOTE_USER}@${REMOTE_HOST}:${REMOTE_PORT})..."
                // Utilise le plugin SSH Agent avec les credentials configurés
                sshagent(credentials: [env.SSH_CREDENTIALS_ID]) { // ID: ssh-credentials-mon-serveur

                    echo "📄 Copying ${PROD_COMPOSE_FILE}..."
                    // Utilise scp (installé dans l'agent) avec le port spécifié (-P majuscule)
                    sh "scp -o StrictHostKeyChecking=no -P ${REMOTE_PORT} ${PROD_COMPOSE_FILE} ${REMOTE_USER}@${REMOTE_HOST}:${REMOTE_DEPLOY_PATH}/${REMOTE_COMPOSE_FILENAME}"

                    echo "🚀 Running Docker Compose on VM..."
                    // Utilise ssh (installé dans l'agent) avec le port spécifié (-p minuscule)
                    sh "ssh -o StrictHostKeyChecking=no -p ${REMOTE_PORT} ${REMOTE_USER}@${REMOTE_HOST} 'cd ${REMOTE_DEPLOY_PATH} && docker compose -f ${REMOTE_COMPOSE_FILENAME} pull && docker compose -f ${REMOTE_COMPOSE_FILENAME} up -d'"
                }
            }
        }
    } // Fin stages

    // Actions post-build
    post {
        always {
            echo '🧹 Cleaning workspace...'
            cleanWs() // Nettoie le workspace Jenkins
        }
        success {
            echo "✅ [${env.BRANCH_NAME}] Pipeline SUCCESS!"
        }
        failure {
            echo "❌ [${env.BRANCH_NAME}] Pipeline FAILED!"
        }
    } // Fin post
} // Fin pipeline