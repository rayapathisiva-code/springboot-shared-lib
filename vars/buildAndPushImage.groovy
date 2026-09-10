def call(Map config = [:]) {
    def imageName = config.get('imageName', env.IMAGE_NAME ?: 'springboot-demo')
    def imageTag = config.get('imageTag', env.GIT_COMMIT)

    if (!imageTag) {
        error 'GIT_COMMIT is not available. Checkout the application repository before building the image.'
    }

    def image = "${imageName}:${imageTag}"

    echo "Building Docker image: ${image}"
    bat "docker build -t ${image} ."

    // Push is intentionally disabled by default for this public POC.
    // Use Jenkins Credentials for registry authentication in an enterprise setup.
    //
    // bat "docker push ${image}"

    env.BUILT_IMAGE = image
    env.IMAGE_TAG = imageTag

    echo "Docker image created: ${image}"
}
