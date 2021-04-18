plugins {
    kotlin("multiplatform")
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

    val repsyUser: String? by project
    val repsyPw: String? by project

    if (repsyUser != null && repsyPw != null) {
        repositories {
            maven {
                credentials {
                    username = repsyUser
                    password = repsyPw
                }
                url = uri("https://repo.repsy.io/mvn/laht/threekt")
            }
        }
    }
}
