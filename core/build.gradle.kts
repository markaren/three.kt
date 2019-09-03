import com.jfrog.bintray.gradle.BintrayExtension
import org.gradle.internal.os.OperatingSystem

plugins {
    kotlin("multiplatform")
    id("com.jfrog.bintray") version "1.8.4"
    `maven-publish`
}

group = "info.laht.threekt"
version = "r1-ALPHA-1"

repositories {
    mavenCentral()
}

val os = OperatingSystem.current()
val lwjglNatives = when {
    os.isLinux -> "natives-linux"
    os.isUnix -> "natives-macos"
    os.isWindows -> "natives-windows"
    else -> TODO("OS $os not supported")
}

val kotlinIOVersion = "0.1.13"

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

                implementation("org.jetbrains.kotlinx:kotlinx-io:$kotlinIOVersion")
            }
        }
        commonTest {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }

        named("jvmMain") {
            dependencies {
                implementation(kotlin("stdlib-jdk8"))

                val slf4jVersion = "1.7.27"
                implementation("org.slf4j:slf4j-api:$slf4jVersion")
                runtimeOnly("org.slf4j:slf4j-log4j12:$slf4jVersion")

                val lwjglVersion = "3.2.2"
                implementation("org.lwjgl:lwjgl:$lwjglVersion")
                implementation("org.lwjgl:lwjgl-glfw:$lwjglVersion")
                implementation("org.lwjgl:lwjgl-opengl:$lwjglVersion")
                runtimeOnly("org.lwjgl:lwjgl:$lwjglVersion:$lwjglNatives")
                runtimeOnly("org.lwjgl:lwjgl-glfw:$lwjglVersion:$lwjglNatives")
                runtimeOnly("org.lwjgl:lwjgl-opengl:$lwjglVersion:$lwjglNatives")

                implementation("org.jetbrains.kotlinx:kotlinx-io-jvm:$kotlinIOVersion")
            }
        }

        named("jvmTest") {
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

val publicationName = "mavenPublication"
publishing {
    publications {
        register(publicationName, MavenPublication::class) {
            from(components["kotlin"])
        }
    }
}

val bintrayUser: String? by project
val bintrayKey: String? by project

if (bintrayUser != null && bintrayKey != null) {

    bintray {
        user = bintrayUser
        key = bintrayKey
        publish = true
        setPublications(publicationName)
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
