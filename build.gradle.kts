plugins {
    `java-library`
    kotlin("jvm") version "1.4.0" apply false
    id("com.jfrog.bintray") version "1.8.4" apply false
}

configure<JavaPluginExtension> {
    sourceCompatibility = JavaVersion.VERSION_1_8
}

tasks.named<Wrapper>("wrapper") {
    gradleVersion = "6.6.1"
    distributionType = Wrapper.DistributionType.ALL
}

println("Gradle version is ${gradle.gradleVersion}")

group = "info.laht.threekt"
version = "r1-ALPHA-17"

println("Building three.kt $version")

subprojects {

    group = rootProject.group
    version = rootProject.version

    repositories {
        mavenCentral()
    }

}
