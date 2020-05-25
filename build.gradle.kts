plugins {
    `java-library`
    kotlin("jvm") version "1.3.72" apply false
}

configure<JavaPluginExtension> {
    sourceCompatibility = JavaVersion.VERSION_1_8
}

tasks.named<Wrapper>("wrapper") {
    distributionType = Wrapper.DistributionType.ALL
}

subprojects {
    repositories {
        mavenCentral()
    }
}
