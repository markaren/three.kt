import com.jfrog.bintray.gradle.BintrayExtension

plugins {
    kotlin("multiplatform")
    id("com.jfrog.bintray") version "1.8.4"
    `maven-publish`
}

kotlin {
    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "1.8"
        }
    }

    sourceSets {
        commonMain {
            dependencies {
                implementation(kotlin("stdlib"))
                implementation(kotlin("reflect"))
            }
        }

        val jvmMain by getting {
            dependencies {
                implementation(kotlin("stdlib-jdk8"))

                val slf4jVersion = "1.7.27"
                implementation("org.slf4j:slf4j-api:$slf4jVersion")
                runtimeOnly("org.slf4j:slf4j-log4j12:$slf4jVersion")
            }
        }

    }
}

publishing {
    publications.withType<MavenPublication>().apply {
        val jvm by getting { }
        val metadata by getting { }
    }
}

val bintrayUser: String? by project
val bintrayKey: String? by project

if (bintrayUser != null && bintrayKey != null) {

    bintray {
        user = bintrayUser
        key = bintrayKey
        publish = true
        setPublications("jvm")
        pkg(delegateClosureOf<BintrayExtension.PackageConfig> {
            repo = "mvn"
            name = "threekt"
            websiteUrl = "https://github.com/markaren/three.kt"
            vcsUrl = "https://github.com/markaren/three.kt"
            setLabels("kotlin")
            setLicenses("MIT")
        })
    }

}
