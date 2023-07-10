// ### Project Information #############################################################################################
private class ProjectInfo { // TODO change project info
    companion object {
        const val longName: String = "Scala 3 Project Template"
        const val description: String = "A template for configuring Scala 3 projects."

        const val repositoryOwner: String = "jahrim"
        const val repositoryName: String = "scala3-project-template"

        const val artifactGroup: String = "io.github.jahrim"
        const val artifactId: String = "scala3-project-template"
        const val implementationClass: String = "main.MainClass"

        const val license = "The MIT License"
        const val licenseUrl = "https://opensource.org/licenses/MIT"

        val website = "https://github.com/$repositoryOwner/$repositoryName"
        val tags = listOf("scala3", "project template")
    }
}

// ### Build Configuration #############################################################################################
plugins {
    with(libs.plugins){
        scala
        application
        alias(spotless)
        alias(wartremover)
        alias(git.semantic.versioning)
        alias(publish.on.central)
    }
}

repositories { mavenCentral() }

dependencies {
    compileOnly(libs.bundles.scalafmt)
    implementation(libs.scala)
    testImplementation(libs.scalatest)
    testImplementation(libs.scalatestplusjunit)
}

application {
    mainClass.set(ProjectInfo.implementationClass)
}

spotless {
    isEnforceCheck = false
    scala { scalafmt(libs.versions.scalafmt.version.get()).configFile(".scalafmt.conf") }
    tasks.compileScala.get().dependsOn(tasks.spotlessApply)
}

// ### Publishing ######################################################################################################
group = ProjectInfo.artifactGroup
gitSemVer {
    buildMetadataSeparator.set("-")
    assignGitSemanticVersion()
}

tasks.javadocJar {
    dependsOn(tasks.scaladoc)
    from(tasks.scaladoc.get().destinationDir)
}

publishOnCentral {
    configureMavenCentral.set(true)
    projectDescription.set(ProjectInfo.description)
    projectLongName.set(ProjectInfo.longName)
    licenseName.set(ProjectInfo.license)
    licenseUrl.set(ProjectInfo.licenseUrl)
    repoOwner.set(ProjectInfo.repositoryOwner)
    projectUrl.set(ProjectInfo.website)
    scmConnection.set("scm:git:$projectUrl")
}

publishing {
    publications {
        withType<MavenPublication> {
            pom {
                // TODO change developers
                developers {
                    developer {
                        name.set("Jahrim Gabriele Cesario")
                        email.set("jahrim.cesario2@studio.unibo.it")
                        url.set("https://jahrim.github.io")
                    }
                }
            }
        }
    }
}

signing {
    val signingKey: String? by project
    val signingPassword: String? by project
    useInMemoryPgpKeys(signingKey, signingPassword)
}