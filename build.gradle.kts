plugins {
    `java-library`
    kotlin("jvm") version "1.3.72" apply false
}

configure<JavaPluginExtension> {
    sourceCompatibility = JavaVersion.VERSION_1_8
}

tasks.named<Wrapper>("wrapper") {
    gradleVersion = "6.5.1"
    distributionType = Wrapper.DistributionType.ALL
}

println ("Gradle version is ${gradle.getGradleVersion()}")

subprojects {
    repositories {
        mavenCentral()
    }
}
