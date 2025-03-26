pipeline {
    agent { docker { image 'maven:3.8.4-openjdk-17' } } // ou une image personnalisée avec Java, Maven, Docker
    environment {
        DOCKERHUB_USERNAME = "mootezbourguiba" // **Remplacez par VOTRE nom d'utilisateur Docker Hub**
        IMAGE_FRONTEND = "${DOCKERHUB_USERNAME}/devops-frontend:latest" // Nom de l'image Docker Hub du frontend
        IMAGE_BACKEND = "${DOCKERHUB_USERNAME}/devops-backend:latest" // Nom de l'image Docker Hub du backend
    }

    stages {
        stage('Checkout') {
            steps {
                git 'https://github.com/mootezbourguiba/devops-fullstack.git' // URL de votre dépôt Git
            }
        }
        stage('Build et test Backend') {
            steps {
                sh 'cd backendDevops && mvn clean install -DskipTests'
                sh 'cd backendDevops && mvn test'
            }
        }
        stage('Build Frontend') {
            steps {
                sh 'cd frontend && npm install'
                sh 'cd frontend && npm run build' // Ou votre commande de build
            }
        }
        stage('Docker Build') {
           steps {
                sh 'docker-compose -f docker/docker-compose.yml build'
            }
        }
        stage('Docker Push') {
            steps {
                withCredentials([usernamePassword(credentialsId: 'dockerhub-credentials', passwordVariable: 'DOCKERHUB_PASSWORD', usernameVariable: 'DOCKERHUB_USERNAME')]) {
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
                // Déploiement via SSH sur un serveur distant (Exemple !)
                sshPublisher(publishers: [sshPublisherDesc(configName: 'VotreServeurDistant', //Connexion SSH configurée dans Jenkins
                    transfers: [sshTransfer(cleanRemote: false, excludes: '', remoteDirectory: '/chemin/vers/deploy', removePrefix: '', sourceFiles: 'docker/docker-compose.yml')]
                    )] )
                sshCommand remoteCommand: 'docker-compose -f /chemin/vers/deploy/docker-compose.yml up -d', //Exécution de la commande distante
            }
        }
    }
}