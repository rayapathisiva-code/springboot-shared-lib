def call(Map config = [:]) {
    def imageName = config.get('imageName', env.IMAGE_NAME ?: 'springboot-demo')

    stage('Build and Test') {
        buildAndTest()
    }

    stage('Quality Scan') {
        qualityScan()
    }

    stage('Security Scan') {
        securityScan()
    }

    stage('Docker Build') {
        buildAndPushImage(
            imageName: imageName,
            imageTag: env.GIT_COMMIT
        )
    }

    stage('Helm Validation') {
        deployWithHelm(
            validationOnly: true,
            imageName: imageName,
            imageTag: env.GIT_COMMIT
        )
    }

    stage('Archive Artifact') {
        publishArtifact()
    }
}
