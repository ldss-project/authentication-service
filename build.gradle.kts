// ### Project Information #############################################################################################
private class ProjectInfo { // TODO change project info
    companion object {
        const val longName: String = "Chess Authentication service"
        const val description: String = "The service which handles the authentication to the application."

        const val repositoryOwner: String = "madina"
        const val repositoryName: String = "authentication-service"

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
    implementation(libs.vertx.web)
    implementation(libs.hexarc)
    implementation(libs.bcrypt)
    implementation(libs.jwt)
    testImplementation(libs.scalatest)
    testImplementation(libs.scalatestplusjunit)
}

application {
    mainClass.set(ProjectInfo.implementationClass)
    val mongoDBConnection: String? by project
    tasks.withType(JavaExec::class.java) {
        mongoDBConnection?.apply { args(this) }
    }
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
                        name.set("Madina Kentpayeva")
                        email.set("madina.kentpayeva@studio.unibo.it")
                        url.set("https://madina9229.github.io")
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