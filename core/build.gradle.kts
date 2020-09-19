import com.jfrog.bintray.gradle.BintrayExtension
import org.gradle.internal.os.OperatingSystem

plugins {
    kotlin("multiplatform")
    id("com.jfrog.bintray")
    `maven-publish`
}

val os = OperatingSystem.current()
val lwjglNatives = when {
    os.isLinux -> "natives-linux"
    os.isUnix -> "natives-macos"
    os.isWindows -> "natives-windows"
    else -> TODO("OS $os not supported")
}

val kotlinIOVersion = "0.1.16"

kotlin {
    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "1.8"
        }
    }

    sourceSets {
        commonMain {
            dependencies {
                api(project(":math"))

                implementation(kotlin("stdlib"))
                implementation("org.jetbrains.kotlinx:kotlinx-io:$kotlinIOVersion")
            }
        }
        commonTest {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }

        val jvmMain by getting {
            dependencies {
                implementation(kotlin("stdlib-jdk8"))

                val lwjglVersion = "3.2.3"
                implementation("org.lwjgl:lwjgl:$lwjglVersion")
                implementation("org.lwjgl:lwjgl-glfw:$lwjglVersion")
                implementation("org.lwjgl:lwjgl-opengl:$lwjglVersion")
                runtimeOnly("org.lwjgl:lwjgl:$lwjglVersion:$lwjglNatives")
                runtimeOnly("org.lwjgl:lwjgl-glfw:$lwjglVersion:$lwjglNatives")
                runtimeOnly("org.lwjgl:lwjgl-opengl:$lwjglVersion:$lwjglNatives")

                implementation("org.jetbrains.kotlinx:kotlinx-io-jvm:$kotlinIOVersion")
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
