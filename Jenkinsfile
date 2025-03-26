pipeline {
    agent any
    environment {
        DOCKERHUB_USERNAME = "mootezbourguiba73"
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
                sh 'cd frontend && npm run build' // Ou votre commande de build
            }
        }
                stage('Deploy') {
            steps {
                echo "Deploy Stage"
                //sshPublisher(publishers: [sshPublisherDesc(configName: 'VotreServeurDistant', //Connexion SSH configurée dans Jenkins
                    //transfers: [sshTransfer(cleanRemote: false, excludes: '', remoteDirectory: '/chemin/vers/deploy', removePrefix: '', sourceFiles: 'docker/docker-compose.yml')]
                    //)] )
                //sshCommand remoteCommand: 'docker-compose -f /chemin/vers/deploy/docker-compose.yml up -d', //Exécution de la commande distante
            }
        }
    }
}