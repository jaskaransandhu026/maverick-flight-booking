pipeline {

    agent any

    options {
        timestamps()
        disableConcurrentBuilds()
    }

    environment {
        REGISTRY = 'host.docker.internal:8081'
        NEXUS_REPOSITORY = 'flight-admin-onprem'
        APPLICATION_NAME = 'flight-admin-service'
    }

    stages {

        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Prepare Gradle') {
            steps {
                sh 'chmod +x gradlew'
            }
        }

        stage('Unit Tests') {
            steps {
                sh './gradlew clean test'
            }

            post {
                always {
                    junit allowEmptyResults: true,
                    testResults: 'build/test-results/test/*.xml'
                }
            }
        }

        stage('Integration Tests') {
            steps {
                sh './gradlew integrationTest'
            }

            post {
                always {
                    junit allowEmptyResults: true,
                    testResults: 'build/test-results/integrationTest/*.xml'
                }
            }
        }

        stage('Build Artifact') {
            steps {
                sh './gradlew bootJar'
            }

            post {
                success {
                    archiveArtifacts(
                        artifacts: 'build/libs/flight-admin-service.jar',
                        fingerprint: true
                    )
                }
            }
        }

        stage('Generate Docker Tag') {
            steps {
                script {

                    def gitSha = sh(
                        returnStdout: true,
                        script: 'git rev-parse --short=8 HEAD'
                    ).trim()

                    def safeBranch = env.BRANCH_NAME
                    .replaceAll('[^A-Za-z0-9_.-]', '-')
                    .toLowerCase()

                    env.IMAGE_TAG = "${safeBranch}-${gitSha}"

                    env.IMAGE_NAME =
                    "${env.REGISTRY}/" +
                    "${env.NEXUS_REPOSITORY}/" +
                    "${env.APPLICATION_NAME}:" +
                    "${env.IMAGE_TAG}"

                    echo "Docker image: ${env.IMAGE_NAME}"
                }
            }
        }

        stage('Build Docker Image') {
            steps {
                sh 'docker build -t "$IMAGE_NAME" .'
            }
        }

        stage('Push Docker Image') {
            steps {

                withCredentials([
                        usernamePassword(
                            credentialsId: 'nexus-docker',
                            usernameVariable: 'NEXUS_CI_USERNAME',
                            passwordVariable: 'NEXUS_CI_PASSWORD'
                        )
                    ]) {

                    sh '''
                        echo "$NEXUS_CI_PASSWORD" | \
                          docker login "$REGISTRY" \
                          --username "$NEXUS_CI_USERNAME" \
                          --password-stdin

                        docker push "$IMAGE_NAME"
                    '''
                }
            }

            post {
                always {
                    sh 'docker logout "$REGISTRY" || true'
                }
            }
        }
    }

    post {

        success {
            echo 'Build successful.'
            echo "Published image: ${env.IMAGE_NAME}"
        }

        failure {
            echo 'Pipeline failed.'
        }
    }
}