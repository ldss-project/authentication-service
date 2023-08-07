// ### Project Information #############################################################################################
private class ProjectInfo {
    val longName: String = "Chess Authentication service"
    val description: String = "A service which handles registration and authentication in an application."

    val repositoryOwner: String = "ldss-project"
    val repositoryName: String = "authentication-service"

    val artifactGroup: String = "io.github.jahrim.chess"
    val artifactId: String = project.name
    val implementationClass: String = "io.github.jahrim.chess.authentication.service.main.main"

    val license = "The MIT License"
    val licenseUrl = "https://opensource.org/licenses/MIT"

    val website = "https://github.com/$repositoryOwner/$repositoryName"
    val tags = listOf("scala3", "chess", "authentication")
}
private val projectInfo: ProjectInfo = ProjectInfo()

// ### Build Configuration #############################################################################################
plugins {
    with(libs.plugins){
        `java-library`
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
    implementation(libs.scallop)
    implementation(libs.hexarc)
    implementation(libs.vertx.web)
    implementation(libs.bcrypt)
    testImplementation(libs.scalatest)
    testImplementation(libs.scalatestplusjunit)
}

application {
    mainClass.set(projectInfo.implementationClass)

    tasks.withType(JavaExec::class.java){
        properties["httpHost"]?.apply { args("--http-host", this) }
        properties["httpPort"]?.apply { args("--http-port", this) }
        properties["allowedOrigins"]?.apply { args("--allowed-origins", this) }
        properties["mongoDBConnection"]?.apply { args("--mongodb-connection", this) }
        properties["mongoDBDatabase"]?.apply { args("--mongodb-database", this) }
        properties["mongoDBCollection"]?.apply { args("--mongodb-collection", this) }
    }
}

spotless {
    isEnforceCheck = false
    scala { scalafmt(libs.versions.scalafmt.version.get()).configFile(".scalafmt.conf") }
    tasks.compileScala.get().dependsOn(tasks.spotlessApply)
}

// ### Publishing ######################################################################################################
group = projectInfo.artifactGroup
gitSemVer {
    buildMetadataSeparator.set("-")
    assignGitSemanticVersion()
}

tasks.jar {
    dependsOn(configurations.runtimeClasspath)
    from(sourceSets.main.get().output)
    from(configurations.runtimeClasspath.get().filter { it.name.endsWith("jar") }.map { zipTree(it) })

    manifest.attributes["Main-Class"] = projectInfo.implementationClass
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

tasks.javadocJar {
    dependsOn(tasks.scaladoc)
    from(tasks.scaladoc.get().destinationDir)
}

publishOnCentral {
    configureMavenCentral.set(true)
    projectDescription.set(projectInfo.description)
    projectLongName.set(projectInfo.longName)
    licenseName.set(projectInfo.license)
    licenseUrl.set(projectInfo.licenseUrl)
    repoOwner.set(projectInfo.repositoryOwner)
    projectUrl.set(projectInfo.website)
    scmConnection.set("scm:git:$projectUrl")
}

publishing {
    publications {
        withType<MavenPublication> {
            pom {
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
