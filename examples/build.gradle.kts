import org.jetbrains.kotlin.gradle.dsl.KotlinJvmCompile

plugins {
    kotlin("jvm")
}

repositories {
    maven(url="http://maven.cuchazinteractive.com")
    maven(url="https://repo.eclipse.org/content/groups/efxclipse")
}

val kotlinIOVersion = "0.1.16"

val os = org.gradle.internal.os.OperatingSystem.current()
val lwjglNatives = when {
    os.isLinux -> "natives-linux"
    os.isUnix -> "natives-macos"
    os.isWindows -> "natives-windows"
    else -> TODO("OS $os not supported")
}

dependencies {

    api(project(":core"))
    implementation(kotlin("stdlib-jdk8"))
    implementation("org.jetbrains.kotlinx:kotlinx-io:$kotlinIOVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-io-jvm:$kotlinIOVersion")

    // JFXGL
    implementation("cuchaz:jfxgl:0.4")
    implementation("cuchaz:jfxgl-jfxrt:0.4")

    // DriftFX
    val lwjglVersion = "3.2.3"
    implementation("org.lwjgl:lwjgl:$lwjglVersion")
    implementation("org.lwjgl:lwjgl-glfw:$lwjglVersion")
    implementation("org.lwjgl:lwjgl-opengl:$lwjglVersion")
    runtimeOnly("org.lwjgl:lwjgl:$lwjglVersion:$lwjglNatives")
    runtimeOnly("org.lwjgl:lwjgl-glfw:$lwjglVersion:$lwjglNatives")
    runtimeOnly("org.lwjgl:lwjgl-opengl:$lwjglVersion:$lwjglNatives")

    api("org.eclipse.fx:org.eclipse.fx.drift:999.0.0-SNAPSHOT")
}

tasks {
    named<KotlinJvmCompile>("compileKotlin") {
        kotlinOptions.jvmTarget = "1.8"
    }
    named<KotlinJvmCompile>("compileTestKotlin") {
        kotlinOptions.jvmTarget = "1.8"
    }

    val jar by getting(Jar::class)

    val mainSourceSet = sourceSets["main"]

    val basePath = projectDir.resolve("src/main/kotlin")
    for (sourceFile in mainSourceSet.allSource) {
        if (sourceFile.extension != "kt") continue

        val relativePath = sourceFile.relativeTo(basePath)
        val mainClass = relativePath.toPath().joinToString(".")
            .removeSuffix(".kt")

        register<JavaExec>("run${sourceFile.nameWithoutExtension}") {
            dependsOn(jar)

            main = mainClass
            classpath = mainSourceSet.runtimeClasspath
        }
    }
}
