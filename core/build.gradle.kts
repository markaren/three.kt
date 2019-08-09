import org.gradle.internal.os.OperatingSystem

plugins {
    kotlin("multiplatform")
    `maven-publish`
}

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

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["kotlin"])
        }
    }
}
