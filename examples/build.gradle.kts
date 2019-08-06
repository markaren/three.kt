import org.jetbrains.kotlin.gradle.dsl.KotlinJvmCompile

plugins {
    kotlin("jvm")
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))

    compile(project(":core"))
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
