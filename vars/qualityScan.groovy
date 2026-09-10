def call() {
    if (env.SONAR_ENABLED?.toBoolean()) {
        echo 'Running SonarQube quality scan'
        withSonarQubeEnv('SonarQube') {
            bat 'mvn -B verify sonar:sonar'
        }
    } else {
        echo 'SonarQube scan is disabled for this POC'
    }
}
