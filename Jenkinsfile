#!/usr/bin/env groovy
pipeline {
    agent {
        label "maven"
    }
    options {
        timestamps()
    }
    stages {
        stage('UTs') {
            steps {
                sh 'mvn -B clean test'
            }
            post {
                always {
                    junit(keepLongStdio: true, testResults: 'target/surefire-reports/TEST-*.xml')
                }
            }
        }
        stage('ITs') {
            steps {
                sh 'mvn -B verify'
            }
            post {
                always {
                    junit(keepLongStdio: true, testResults: 'target/failsafe-reports/TEST-*.xml')
                }
            }
        }
    }
}
