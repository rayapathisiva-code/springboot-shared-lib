def call() {
    echo 'Running Maven build and unit tests'
    bat 'mvn -B clean install'
}
