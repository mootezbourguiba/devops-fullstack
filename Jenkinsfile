pipeline {
    agent { docker { image 'maven:3.8.4-openjdk-17' } }
    environment {
        DOCKERHUB_USERNAME = "mootezbourguiba" // REMPLACEZ PAR VOTRE NOM D'UTILISATEUR
        IMAGE_FRONTEND = "${DOCKERHUB_USERNAME}/devops-frontend:latest"
        IMAGE_BACKEND = "${DOCKERHUB_USERNAME}/devops-backend:latest"
    }
    stages {
        stage('Checkout') {
            steps {
                git 'https://github.com/mootezbourguiba/devops-fullstack.git'
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
                sh 'cd frontend && npm run build'
            }
        }/*
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
        }*/
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