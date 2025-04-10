// Jenkinsfile FINAL v3 (avec stage de debug pour les outils)
pipeline {
    agent any

    tools {
        jdk 'jdk17'
        maven 'apache-maven-3.8.6'
        nodejs 'node-20'
        git 'Default' // Assure-toi que 'Default' est le nom de ton outil Git
    }

    environment {
        // --- Credentials Jenkins ---
        DOCKERHUB_CREDENTIALS_ID = 'dockerhub-credentials'
        SSH_CREDENTIALS_ID       = 'ssh-credentials-mon-serveur'

        // --- Configuration Docker Hub ---
        DOCKERHUB_USERNAME       = "mootezbourguiba73"
        IMAGE_NAME_BACKEND       = "devops-backend"
        IMAGE_NAME_FRONTEND      = "devops-frontend"
        IMAGE_BACKEND            = "${env.DOCKERHUB_USERNAME}/${env.IMAGE_NAME_BACKEND}:latest"
        IMAGE_FRONTEND           = "${env.DOCKERHUB_USERNAME}/${env.IMAGE_NAME_FRONTEND}:latest"

        // --- Configuration Déploiement SSH ---
        REMOTE_USER              = "mootez"
        REMOTE_HOST              = "localhost"
        REMOTE_PORT              = "2222"
        REMOTE_DEPLOY_PATH       = "/home/${env.REMOTE_USER}/devops-app"
        PROD_COMPOSE_FILE        = "docker-compose.prod.yml"
        REMOTE_COMPOSE_FILENAME  = "docker-compose.yml"

        // --- Modification du PATH ---
        // Tente d'ajouter les chemins des outils au PATH
        // Note: L'efficacité peut dépendre de l'agent et du moment où c'est évalué
        // Version corrigée :
        PATH = "${tool 'Default'}/bin:${tool 'jdk17'}/bin:${tool 'apache-maven-3.8.6'}/bin:${tool 'node-20'}/bin:${env.PATH}"
    }

    stages {
        // *** NOUVEAU STAGE DE DEBUG ***
        stage('0. Debug Tools and PATH') {
            steps {
                echo "--- Vérification Environnement ---"
                echo "PATH complet:"
                sh 'printenv PATH' // Affiche la variable PATH telle que vue par le shell

                echo "--- Vérification Outils via 'tool' ---"
                echo "Chemin Git Tool: ${tool 'Default'}"
                echo "Chemin JDK Tool: ${tool 'jdk17'}"
                echo "Chemin Maven Tool: ${tool 'apache-maven-3.8.6'}"
                echo "Chemin NodeJS Tool: ${tool 'node-20'}"

                echo "--- Tentative d'exécution directe ---"
                // Essaie d'exécuter 'git --version' en utilisant le PATH modifié
                sh 'echo ">>> git --version via PATH modifié:"'
                sh 'git --version'

                 // Essaie d'exécuter 'git --version' en utilisant le chemin absolu fourni par 'tool'
                 sh 'echo ">>> git --version via chemin absolu:"'
                 sh "${tool 'Default'}/bin/git --version"

                 // Idem pour Java pour confirmer que les autres outils fonctionnent
                 sh 'echo ">>> java -version via chemin absolu:"'
                 sh "${tool 'jdk17'}/bin/java -version"
            }
        }
        // *** FIN STAGE DE DEBUG ***

        stage('1. Checkout') {
            steps {
                echo "📥 [${env.BRANCH_NAME}] Récupération du code depuis GitHub..."
                // Cette étape implicite utilise 'git'. Si l'étape 0 montre que git n'est pas trouvée,
                // le problème est dans la configuration de l'outil/agent Jenkins.
                checkout scm
                echo '>>> Contenu du workspace après checkout:'
                sh 'ls -la'
            }
        }

        // ... reste des stages (inchangés) ...

        stage('2. Build et Test Backend') {
             // ... (conserver le withEnv ici par sécurité) ...
            steps {
                echo "⚙️ [${env.BRANCH_NAME}] Construction et test du backend..."
                dir('devops-fullstack/backend/backendDevops') {
                     withEnv(["JAVA_HOME=${tool 'jdk17'}", "PATH+MAVEN=${tool 'apache-maven-3.8.6'}/bin", "PATH+JDK=${tool 'jdk17'}/bin"]) {
                        sh "mvn clean package"
                    }
                }
            }
            post { // ... }
        }

        stage('3. Build et Test Frontend') {
             // ... (conserver le withEnv ici par sécurité) ...
            steps {
                echo "🌐 [${env.BRANCH_NAME}] Construction et test du frontend..."
                dir('devops-fullstack/frontend/frontenddevops') {
                    withEnv(["NODEJS_HOME=${tool 'node-20'}", "PATH+NODE=${tool 'node-20'}/bin"]) {
                        sh "npm install"
                        sh "npm test -- --watchAll=false"
                        sh "npm run build"
                    }
                }
            }
            post { // ... }
        }

        stage('4. Build et Push Docker Images') { // ... (inchangé) ... }

        stage('5. Deploy to VM via SSH') { // ... (inchangé) ... }

    } // Fin stages

    post { // ... (inchangé) ... }
} // Fin pipeline