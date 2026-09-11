def call(Map config = [:]) {
    def chart = config.get('chart', 'helm/springboot-poc')
    def releaseName = config.get('releaseName', env.IMAGE_NAME ?: 'springboot-poc')
    def namespace = config.get('namespace', env.ENVIRONMENT ?: 'development')
    def imageName = config.get('imageName', env.IMAGE_NAME ?: 'springboot-poc')
    def imageTag = config.get('imageTag', env.IMAGE_TAG ?: env.GIT_COMMIT)
    def validationOnly = config.get('validationOnly', false)

    if (!fileExists(chart)) {
        error "Helm chart not found: ${chart}"
    }

    echo "Helm lint: ${chart}"
    bat "helm lint ${chart}"

    bat "helm template ${releaseName} ${chart} --set image.repository=${imageName} --set image.tag=${imageTag} > rendered-manifest.yaml"

    if (validationOnly) {
        echo 'Helm validation completed. Deployment is disabled in validation-only mode.'
        return
    }

    echo "Deploying ${releaseName}:${imageTag} to namespace ${namespace}"

    bat "helm upgrade --install ${releaseName} ${chart} --namespace ${namespace} --create-namespace --set image.repository=${imageName} --set image.tag=${imageTag}"
}
