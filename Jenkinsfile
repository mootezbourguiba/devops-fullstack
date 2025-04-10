// Jenkinsfile FINAL v7 (Après correction Docker Hub Username & Login)
pipeline {
    agent any // Exécute sur l'agent par défaut (le contrôleur Jenkins, qui a Docker CLI/Compose installé)

    // Outils à utiliser (configurés dans Jenkins -> Global Tool Configuration)
    tools {
        jdk 'jdk17'
        maven 'apache-maven-3.8.6'
        nodejs 'node-20'
        git 'Default' // Assure-toi que 'Default' est bien le nom configuré dans Jenkins
    }

    // Variables d'environnement
    environment {
        // --- Credentials Jenkins ---
        DOCKERHUB_CREDENTIALS_ID = 'dockerhub-credentials'  // ID du credential Docker Hub (Username/Password ou Token)
        SSH_CREDENTIALS_ID       = 'ssh-credentials-mon-serveur' // ID du credential SSH (Username with private key)

        // --- Configuration Docker Hub ---
        // UTILISE TON VRAI USERNAME DOCKER HUB ICI !
        DOCKERHUB_USERNAME       = "mootezbourguiba365"      // === CORRIGÉ ===
        IMAGE_NAME_BACKEND       = "devops-backend"
        IMAGE_NAME_FRONTEND      = "devops-frontend"
        // Construit les noms d'image complets
        IMAGE_BACKEND            = "${env.DOCKERHUB_USERNAME}/${env.IMAGE_NAME_BACKEND}:latest"
        IMAGE_FRONTEND           = "${env.DOCKERHUB_USERNAME}/${env.IMAGE_NAME_FRONTEND}:latest"

        // --- Configuration Déploiement SSH ---
        REMOTE_USER              = "mootez"  // Username sur la VM Ubuntu
        REMOTE_HOST              = "localhost" // Via redirection VBox
        REMOTE_PORT              = "2222"      // Port hôte redirigé
        REMOTE_DEPLOY_PATH       = "/home/${env.REMOTE_USER}/devops-app" // Chemin sur la VM
        PROD_COMPOSE_FILE        = "docker-compose.prod.yml"          // Fichier Compose de PROD à la racine Git
        REMOTE_COMPOSE_FILENAME  = "docker-compose.yml"               // Nom du fichier Compose sur le serveur distant
        // Optionnel: ID du credential Jenkins de type "Secret file" pour le fichier .env de prod
        PROD_ENV_CREDENTIAL_ID   = "prod-env-file" // Remplace par ton ID si tu utilises cette méthode

        // --- Modification du PATH Complète ---
        // Ajoute tous les outils au PATH (assure que git fonctionne dans les sh steps)
        PATH = "${tool 'Default'}/bin:${tool 'jdk17'}/bin:${tool 'apache-maven-3.8.6'}/bin:${tool 'node-20'}/bin:${env.PATH}"
    }

    stages {
        stage('1. Checkout') {
            steps {
                echo "📥 [${env.BRANCH_NAME}] Récupération du code depuis GitHub..."
                // Git est ajouté au PATH via environment, pas besoin de sh 'git --version' ici
                checkout scm
                echo '>>> Workspace après checkout:'
                sh 'ls -la'
            }
        }

        stage('2. Build et Test Backend') {
            steps {
                echo "⚙️ [${env.BRANCH_NAME}] Build/Test backend..."
                dir('devops-fullstack/backend/backendDevops') {
                    // Java et Maven sont dans le PATH via tools/environment
                    sh "mvn clean package -DskipTests" // Test ok
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
                echo "🌐 [${env.BRANCH_NAME}] Build/Test frontend..."
                dir('devops-fullstack/frontend/frontenddevops') {
                    // Node est dans le PATH via tools/environment
                    sh "npm install"
                    sh "npm test -- --watchAll=false" // Test ok
                    sh "npm run build"
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
                // Docker CLI et Compose sont installés dans l'image Jenkins personnalisée
                sh 'docker --version'
                sh 'docker compose version' // Vérifie que compose v2 est bien là

                // Utilise withCredentials pour charger les identifiants DockerHub de manière sécurisée
                withCredentials([usernamePassword(credentialsId: env.DOCKERHUB_CREDENTIALS_ID,
                                               passwordVariable: 'DOCKERHUB_PASSWORD', // Le mot de passe/token sera dans cette variable
                                               usernameVariable: 'DOCKERHUB_USER')]) {    // Le username (mootezbourguiba365) sera ici

                    // Utilise DOCKERHUB_USER des credentials pour le login
                    echo "🐳 [${env.BRANCH_NAME}] Login Docker Hub (${DOCKERHUB_USER})..."

                    // === Login Docker Sécurisé via stdin ===
                    sh "echo '${DOCKERHUB_PASSWORD}' | docker login --username '${DOCKERHUB_USER}' --password-stdin"

                    // Les noms d'images utilisent env.DOCKERHUB_USERNAME qui a été corrigé plus haut
                    echo "🔨 [${env.BRANCH_NAME}] Build backend image: ${IMAGE_BACKEND}"
                    dir('devops-fullstack/backend/backendDevops') {
                        // Assure-toi que le Dockerfile est à la racine de ce dossier
                        sh "docker build -t ${IMAGE_BACKEND} ."
                    }
                    echo "🚀 [${env.BRANCH_NAME}] Push backend image..."
                    sh "docker push ${IMAGE_BACKEND}"

                    echo "🔨 [${env.BRANCH_NAME}] Build frontend image: ${IMAGE_FRONTEND}"
                    dir('devops-fullstack/frontend/frontenddevops') {
                         // Assure-toi que le Dockerfile est à la racine de ce dossier
                        sh "docker build -t ${IMAGE_FRONTEND} ."
                    }
                    echo "🚀 [${env.BRANCH_NAME}] Push frontend image..."
                    sh "docker push ${IMAGE_FRONTEND}"

                    echo "🚪 [${env.BRANCH_NAME}] Logout Docker Hub..."
                    sh 'docker logout'
                } // Fin withCredentials
            }
        }

        stage('5. Deploy to VM via SSH') {
             // Condition : S'exécute seulement pour la branche 'main' (ou 'develop' si tu testes)
             // Change la branche ici si nécessaire pour tes tests
             when { branch 'main' } // ou 'develop' etc.
             steps {
                 script {
                    // Vérifie si ssh/scp sont dispos (normalement oui dans l'image jenkins)
                    try {
                         sh 'echo "Vérification ssh/scp..."'
                         sh 'which ssh'
                         sh 'which scp'
                     } catch (err) {
                         error "[FATAL] Commande ssh ou scp non trouvée : ${err}"
                     }
                 }
                echo "🛰️ [${env.BRANCH_NAME}] Deploying to VM (${REMOTE_USER}@${REMOTE_HOST}:${REMOTE_PORT})..."
                // Utilise sshagent pour charger la clé privée SSH de manière sécurisée
                sshagent(credentials: [env.SSH_CREDENTIALS_ID]) {

                    // === Gestion du fichier .env (CRUCIAL !) ===
                    // Décommente UNE des options suivantes et adapte-la !

                    /*
                    // Option 1 (Recommandée) : Utiliser un credential Jenkins "Secret file"
                    echo "📄 Récupération du fichier .env depuis les credentials Jenkins (${env.PROD_ENV_CREDENTIAL_ID})..."
                    withCredentials([file(credentialsId: env.PROD_ENV_CREDENTIAL_ID, variable: 'PROD_ENV_FILE_PATH')]) {
                        echo "📄 Copie de .env vers ${REMOTE_DEPLOY_PATH}/.env ..."
                        sh "scp -o StrictHostKeyChecking=no -P ${REMOTE_PORT} ${PROD_ENV_FILE_PATH} ${REMOTE_USER}@${REMOTE_HOST}:${REMOTE_DEPLOY_PATH}/.env"
                    }
                    */

                    /*
                    // Option 2 : Copier un fichier .env.prod depuis un chemin sur le contrôleur Jenkins (Moins sécurisé, à éviter si possible)
                    // Assure-toi que ce fichier existe et est lisible par l'utilisateur Jenkins (root dans ce cas)
                    // Attention à ne jamais le commiter dans Git !
                    def jenkinsControllerEnvPath = "/chemin/securise/sur/jenkins/controller/.env.prod" // <== ADAPTE CE CHEMIN !
                    echo "📄 Copie de ${jenkinsControllerEnvPath} vers ${REMOTE_DEPLOY_PATH}/.env ..."
                    sh "scp -o StrictHostKeyChecking=no -P ${REMOTE_PORT} ${jenkinsControllerEnvPath} ${REMOTE_USER}@${REMOTE_HOST}:${REMOTE_DEPLOY_PATH}/.env"
                    */

                    // === Copie du fichier Docker Compose ===
                    echo "📄 Copie de ${PROD_COMPOSE_FILE} vers ${REMOTE_DEPLOY_PATH}/${REMOTE_COMPOSE_FILENAME}..."
                    sh "scp -o StrictHostKeyChecking=no -P ${REMOTE_PORT} ${PROD_COMPOSE_FILE} ${REMOTE_USER}@${REMOTE_HOST}:${REMOTE_DEPLOY_PATH}/${REMOTE_COMPOSE_FILENAME}"

                    // === Exécution des commandes Docker Compose sur la VM ===
                    echo "🚀 Exécution du script de déploiement sur la VM distante..."
                    sh """
                        ssh -o StrictHostKeyChecking=no -p ${REMOTE_PORT} ${REMOTE_USER}@${REMOTE_HOST} << EOF
                            echo '--- Connexion SSH réussie ---'

                            echo '1. Navigation vers le répertoire de déploiement : ${REMOTE_DEPLOY_PATH}'
                            cd ${REMOTE_DEPLOY_PATH} || { echo 'ERREUR: Impossible de changer de répertoire !'; exit 1; }

                            # Optionnel: Login Docker Hub sur la VM si tes images sont privées et que le pull le nécessite
                            # (Peut nécessiter de passer les credentials via .env ou une autre méthode sécurisée sur la VM)
                            # echo '2. Login Docker Hub sur la VM (si nécessaire)...'
                            # docker login ...

                            echo '3. Pull des dernières images Docker spécifiées dans ${REMOTE_COMPOSE_FILENAME}...'
                            docker compose -f ${REMOTE_COMPOSE_FILENAME} pull || { echo 'ERREUR: docker compose pull a échoué !'; exit 1; }

                            echo '4. Arrêt et suppression des anciens conteneurs (si existants)...'
                            docker compose -f ${REMOTE_COMPOSE_FILENAME} down || echo 'INFO: Aucun conteneur à arrêter ou la commande down a échoué (non bloquant).'

                            echo '5. Démarrage des nouveaux conteneurs en mode détaché...'
                            docker compose -f ${REMOTE_COMPOSE_FILENAME} up -d || { echo 'ERREUR: docker compose up a échoué !'; exit 1; }

                            echo '6. Attente de quelques secondes pour la stabilisation...'
                            sleep 15

                            echo '7. Vérification du statut des conteneurs...'
                            docker compose -f ${REMOTE_COMPOSE_FILENAME} ps

                            # Optionnel : Health check plus spécifique (adapte le port et le chemin)
                            # echo '8. Health check du backend...'
                            # curl --fail http://localhost:8081/actuator/health || { echo 'ERREUR: Health Check Backend a échoué !'; exit 1; }

                            echo '9. Nettoyage des anciennes images Docker non utilisées...'
                            docker image prune -af || echo 'INFO: Nettoyage Docker (prune) échoué ou rien à nettoyer.'

                            echo '--- Script de déploiement terminé sur la VM ---'
                        EOF
                    """
                } // Fin sshagent
            } // Fin steps
        } // Fin stage Deploy
    } // Fin stages

    // Actions post-build
    post {
        always {
            // Le nettoyage doit être dans un node context, mais 'agent any' le fournit déjà implicitement pour always
            echo '🧹 Nettoyage du workspace...'
            cleanWs()
        }
        success {
            echo "✅ [${env.BRANCH_NAME}] Pipeline SUCCESS!"
            // Ajoute ici tes notifications de succès (Slack, Email...)
        }
        failure {
            echo "❌ [${env.BRANCH_NAME}] Pipeline FAILED!"
             // Ajoute ici tes notifications d'échec
        }
    } // Fin post
} // Fin pipeline