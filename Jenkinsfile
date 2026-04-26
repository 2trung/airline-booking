pipeline {
    agent any

    options {
        timestamps()
        disableConcurrentBuilds()
        buildDiscarder(logRotator(numToKeepStr: '20', artifactNumToKeepStr: '20'))
        skipDefaultCheckout(true)
    }

    parameters {
        string(name: 'DEPLOY_AGENT_LABEL', defaultValue: 'deploy-agent', description: 'Label of the Jenkins agent attached to the deployment server')
        string(name: 'DEPLOY_ROOT', defaultValue: '/opt/airline-booking', description: 'Root directory on the deployment server')
        booleanParam(name: 'SKIP_DEPLOY', defaultValue: false, description: 'Build and test only; do not deploy')
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
                sh 'ls -la'
            }
        }

        stage('Build & Test') {
            agent {
                docker {
                    image 'maven:3.9.9-eclipse-temurin-17'
                    args '-v $HOME/.m2:/root/.m2' // cache dependencies
                }
            }
            steps {
                sh 'mvn -B -ntp clean verify'
                stash name: 'deployable-jars',
                      includes: 'cloud/**/target/*.jar,services/**/target/*.jar',
                      excludes: '**/*-sources.jar,**/*-javadoc.jar,**/*.original'
            }
        }

        stage('Archive Artifacts') {
            steps {
                archiveArtifacts artifacts: 'common-lib/target/*.jar,cloud/**/target/*.jar,services/**/target/*.jar',
                                  fingerprint: true,
                                  onlyIfSuccessful: true
            }
        }

        stage('Deploy') {
            when {
                expression { !params.SKIP_DEPLOY }
            }
            agent {
                label "${params.DEPLOY_AGENT_LABEL}"
            }
            steps {
                checkout scm
                unstash 'deployable-jars'
                sh '''
                    set -euo pipefail
                    export DEPLOY_ROOT="${DEPLOY_ROOT}"
                    export RELEASE_NAME="build-${BUILD_NUMBER}"
                    ./deploy/deploy.sh
                '''
            }
        }
    }

    post {
        always {
            junit testResults: '**/target/surefire-reports/*.xml', allowEmptyResults: true
        }
    }
}