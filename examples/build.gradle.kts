import org.jetbrains.kotlin.gradle.dsl.KotlinJvmCompile

plugins {
    kotlin("jvm")
}

repositories {
    maven(url="http://maven.cuchazinteractive.com")
}

val kotlinIOVersion = "0.1.16"

dependencies {

    api(project(":core"))
    implementation(kotlin("stdlib-jdk8"))
    implementation("org.jetbrains.kotlinx:kotlinx-io:$kotlinIOVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-io-jvm:$kotlinIOVersion")

    implementation("cuchaz:jfxgl:0.4")
    implementation("cuchaz:jfxgl-jfxrt:0.4")

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
