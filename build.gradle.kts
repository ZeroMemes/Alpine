import net.ltgt.gradle.errorprone.errorprone

plugins {
    id("java-library")
    id("maven-publish")
    id("jacoco")
    id("net.ltgt.errorprone") version "3.1.0"
}

val typetoolsVersion by extra { "0.6.3" }
val fastutilVersion by extra { "8.5.12" }
val annotationsVersion by extra { "24.0.1" }
val mockitoVersion by extra { "5.3.1" }
val junitVersion by extra { "5.9.3" }

// 2.11+ requires at least JDK 11. This could be done by cross-compiling to Java 8 bytecode using JDK 11, but I
// couldn't fix the issue with "sun.misc.Unsafe" being unrecognized when doing so with "--release 8". Maybe it's worth
// removing usage of Unsafe all together in favor of a supported way of accessing private fields/methods.
val errorproneVersion by extra { "2.10.0" }

group = "com.github.ZeroMemes"
version = "3.1.0-SNAPSHOT"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(8))
    }
    withJavadocJar()
    withSourcesJar()
}

tasks.jar {
    into("META-INF") {
        from("LICENSE")
    }
    manifest {
        attributes(mapOf(
            "Automatic-Module-Name" to "me.zero.alpine"
        ), "Alpine")
    }
}

tasks.withType<AbstractArchiveTask>().configureEach {
    isPreserveFileTimestamps = false
    isReproducibleFileOrder = true
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("net.jodah:typetools:$typetoolsVersion")
    api("it.unimi.dsi:fastutil:$fastutilVersion")
    implementation("org.jetbrains:annotations:$annotationsVersion")

    errorprone("com.google.errorprone:error_prone_core:$errorproneVersion")

    testImplementation("org.mockito:mockito-core:$mockitoVersion")
    testImplementation("org.junit.jupiter:junit-jupiter-api:$junitVersion")
    testImplementation("org.junit.jupiter:junit-jupiter-params:$junitVersion")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$junitVersion")
}

tasks {
    // Use Java 17 for running tests (Latest LTS version)
    // This allows @BeforeAll usage in @Nested test classes (Added in 16)
    test {
        useJUnitPlatform()
        javaLauncher.set(project.javaToolchains.launcherFor {
            languageVersion.set(JavaLanguageVersion.of(17))
        })
    }

    compileTestJava {
        javaCompiler.set(project.javaToolchains.compilerFor {
            languageVersion.set(JavaLanguageVersion.of(17))
        })
        options.errorprone.isEnabled.set(false)
    }

    // Setup report for test coverage
    jacocoTestReport {
        reports {
            xml.required.set(true)
            html.required.set(true)
        }
    }

    check {
        dependsOn(jacocoTestReport)
    }
}

fun configureJavaPublication(publication: MavenPublication) = publication.apply {
    from(components["java"])
    pom {
        name.set("Alpine")
        description.set("A lightweight, pub/sub event system for Java 8+")
        url.set("https://github.com/ZeroMemes/Alpine")
        licenses {
            license {
                name.set("MIT")
                url.set("https://github.com/ZeroMemes/Alpine/blob/master/LICENSE")
                distribution.set("repo")
            }
        }
        developers {
            developer {
                id.set("ZeroMemes")
                name.set("Brady Hahn")
                email.set("zeromemesdev@gmail.com")
            }
        }
        scm {
            connection.set("scm:git:https://github.com/ZeroMemes/Alpine.git")
            url.set("https://github.com/ZeroMemes/Alpine")
        }
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            configureJavaPublication(this)
        }
        create<MavenPublication>("gpr") {
            groupId = "com.github.zeromemes"
            artifactId = "alpine" // GitHub Packages requires a lowercase artifact id
            configureJavaPublication(this)
        }
    }

    repositories {
        maven {
            name = "FileSystem"
            url = if (System.getenv("MAVEN_DIR") != null) {
                uri("file://" + System.getenv("MAVEN_DIR"))
            } else {
                uri(layout.buildDirectory.dir("repo"))
            }
        }
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/ZeroMemes/Alpine")
            credentials {
                username = System.getenv("GITHUB_ACTOR")
                password = System.getenv("GITHUB_TOKEN")
            }
        }
    }
}

tasks.withType<PublishToMavenRepository>().configureEach {
    onlyIf {
        repository == publishing.repositories["FileSystem"] && publication == publishing.publications["maven"]
                || repository == publishing.repositories["GitHubPackages"] && publication == publishing.publications["gpr"]
    }
}

tasks.withType<PublishToMavenLocal>().configureEach {
    onlyIf {
        publication == publishing.publications["maven"]
    }
}
