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
                api(project(":common"))
                implementation(kotlin("stdlib"))
            }
        }
        commonTest {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }

        val jvmTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation(kotlin("test-junit"))

                val junitVersion = "5.3.2"
                implementation("org.junit.jupiter:junit-jupiter-api:$junitVersion")
                implementation("org.junit.jupiter:junit-jupiter-params:$junitVersion")
                runtimeOnly("org.junit.jupiter:junit-jupiter-engine:$junitVersion")
            }
        }
    }
}

tasks.named<Test>("jvmTest") {
    failFast = true
    useJUnitPlatform()
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
