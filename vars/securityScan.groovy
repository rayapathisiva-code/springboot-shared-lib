def call() {
    echo 'Security scan stage is present, but Trivy is intentionally disabled for this POC.'

    // Enable after Trivy is installed and approved in Jenkins:
    //
    // bat 'trivy image --severity HIGH,CRITICAL --ignore-unfixed --exit-code 1 %IMAGE_NAME%:%GIT_COMMIT%'
}
