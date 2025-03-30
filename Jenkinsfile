pipeline {
    agent any
    environment {
        DOCKERHUB_USERNAME = "mootezbourguiba365"  // REMPLACEZ PAR VOTRE NOM D'UTILISATEUR
        IMAGE_FRONTEND = "${DOCKERHUB_USERNAME}/devops-frontend:latest"
        IMAGE_BACKEND = "${DOCKERHUB_USERNAME}/devops-backend:latest"
    }

    stages {
        stage('Checkout') {
            steps {
                git credentialsId: 'github-credentials', url: 'https://github.com/mootezbourguiba/devops-fullstack.git', branch: 'main' // OU 'master'
            }
        }
        stage('Build et test Backend') {
            steps {
                sh "cd ${WORKSPACE}/backendDevops && mvn clean install -DskipTests"
                sh "cd ${WORKSPACE}/backendDevops && mvn test"
            }
        }
        stage('Build Frontend') {
            steps {
                sh "cd ${WORKSPACE}/frontend/frontenddevops && npm install"
                sh "cd ${WORKSPACE}/frontend/frontenddevops && npm run build"
            }
        }
        stage('Docker Build and Push') {
            steps {
                // Authentification Docker Hub
                withCredentials([usernamePassword(credentialsId: 'dockerhub-credentials', 
                                               passwordVariable: 'DOCKERHUB_PASSWORD', 
                                               usernameVariable: 'DOCKERHUB_USERNAME')]) {
                    sh 'docker login -u "$DOCKERHUB_USERNAME" -p "$DOCKERHUB_PASSWORD"'
                    // Construction et push de l'image backend
                    sh "cd ${WORKSPACE}/backendDevops && docker build -t ${IMAGE_BACKEND} ."
                    sh "docker push ${IMAGE_BACKEND}"
                    // Construction et push de l'image frontend
                    sh "cd ${WORKSPACE}/frontend/frontenddevops && docker build -t ${IMAGE_FRONTEND} ."
                    sh "docker push ${IMAGE_FRONTEND}"
                    sh 'docker logout'
                }
            }
        }
        stage('Deploy') {
            steps {
                echo "Deploy Stage"
                sshPublisher(publishers: [sshPublisherDesc(configName: 'ssh-mon-serveur', 
                    transfers: [sshTransfer(cleanRemote: false, excludes: '', remoteDirectory: '/path/to/your/app', removePrefix: '', sourceFiles: 'docker-compose.yml')]
                    )] )
                sshCommand remoteCommand: 'docker-compose -f /path/to/your/app/docker-compose.yml up -d'
            }
        }
    }
}