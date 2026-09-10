def call(Map config = [:]) {
    def artifactPattern = config.get(
        'artifactPattern',
        'target/*.jar,rendered-manifest.yaml'
    )

    echo "Archiving build artifacts: ${artifactPattern}"

    archiveArtifacts(
        artifacts: artifactPattern,
        fingerprint: true,
        allowEmptyArchive: true
    )
}
