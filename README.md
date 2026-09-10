# springboot-shared-library

Jenkins Shared Library for the Spring Boot Jenkins-to-GitHub Actions migration POC.

This repository contains only the reusable Jenkins Shared Library. The application repository keeps the Jenkinsfile, Spring Boot source, Dockerfile, Helm chart, and GitHub Actions workflows.

## Structure

```text
springboot-shared-library/
├── README.md
└── vars/
    ├── buildAndTest.groovy
    ├── qualityScan.groovy
    ├── securityScan.groovy
    ├── buildAndPushImage.groovy
    ├── publishArtifact.groovy
    ├── deployWithHelm.groovy
    └── ciPipeline.groovy
```

## Jenkins configuration

Configure this repository under:

`Manage Jenkins -> System -> Global Trusted Pipeline Libraries`

Use:

- Name: `springboot-shared-library`
- Default version: `main`
- Retrieval: Modern SCM
- SCM: Git
- Repository: your GitHub repository URL
- Credentials: None if the repository is public

## Example Jenkinsfile

```groovy
@Library('springboot-shared-library') _

pipeline {
    agent any

    environment {
        IMAGE_NAME = 'springboot-demo'
        SONAR_ENABLED = 'false'
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build and Test') {
            steps {
                buildAndTest()
            }
        }

        stage('Quality') {
            steps {
                qualityScan()
            }
        }

        stage('Security') {
            steps {
                securityScan()
            }
        }

        stage('Docker Build') {
            steps {
                buildAndPushImage()
            }
        }

        stage('Helm Validation') {
            steps {
                deployWithHelm(validationOnly: true)
            }
        }

        stage('Archive Artifact') {
            steps {
                publishArtifact()
            }
        }
    }
}
```

## Complete orchestration option

```groovy
@Library('springboot-shared-library') _

pipeline {
    agent any

    environment {
        IMAGE_NAME = 'springboot-demo'
        SONAR_ENABLED = 'false'
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('CI Pipeline') {
            steps {
                ciPipeline()
            }
        }
    }
}
```

`ciPipeline()` runs:

```text
Maven Build + Unit Test
        |
Quality Scan (optional)
        |
Security Scan placeholder
        |
Docker Build with Git SHA
        |
Helm Validation
        |
Artifact Archive
```

## Windows Jenkins agent

This POC is designed for the Windows Jenkins installation used for the assessment. Maven, Docker and Helm commands therefore use Jenkins `bat`.

Required tools:

```bat
java -version
mvn -version
docker --version
helm version
```

## Git SHA tagging

The Docker function uses Jenkins `GIT_COMMIT`:

```text
springboot-demo:<GIT_COMMIT>
```

This is the Jenkins equivalent of:

```text
springboot-demo:${GITHUB_SHA}
```

Build once and promote the same immutable image between environments.

## Security

No credentials are hardcoded.

Use Jenkins Credentials for registry/Kubernetes credentials. For AWS deployments, prefer short-lived/OIDC-based authentication where supported.

## Trivy

Trivy is intentionally commented out at this stage. The security stage remains in the library so the migration architecture demonstrates where the security check belongs.

Enable the commented Trivy command after Trivy is installed and approved in the Jenkins environment.

## Jenkins to GitHub Actions mapping

| Jenkins | GitHub Actions |
|---|---|
| Jenkinsfile | Workflow |
| Shared Library | Composite Action / Reusable Workflow |
| `agent any` | GitHub runner |
| Jenkins Credentials | GitHub Secrets / OIDC |
| `stash` / `unstash` | upload/download artifact |
| `input` | GitHub Environment approval |
| `GIT_COMMIT` | `GITHUB_SHA` |
| Maven cache | `actions/setup-java` Maven cache |
| Pipeline stages | Workflow jobs/steps |
