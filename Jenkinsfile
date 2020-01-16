pipeline {
    agent { docker 'maven:3-alpine' } 
    stages {
        stage('Clone') {
            steps {
                git branch: '${BRANCH_NAME}', url: 'https://github.com/CMSgov/qpp-conversion-tool.git'
            }
        }
		stage('Maven Build') {
			steps {
				sh 'ls'
				sh 'mvn --batch-mode verify'
			}
		}
		stage('Integration Tests') {
			steps {
				sh 'mvn --batch-mode test -P integration'
			}
		}
    }
}
