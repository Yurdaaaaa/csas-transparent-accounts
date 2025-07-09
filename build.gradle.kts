import com.google.wireless.android.sdk.stats.GradleBuildVariant.KotlinOptions
import org.jetbrains.kotlin.gradle.dsl.KotlinCompile

buildscript {
    repositories {
        google()
        mavenCentral()
        maven(url = "https://plugins.gradle.org/m2/")
        maven(url = "https://oss.sonatype.org/content/repositories/snapshots")
    }
    dependencies {
        classpath(libs.sqldelight.gradle.plugin)
    }
}

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.comAndroidLibrary) apply false
    alias(libs.plugins.jetbrains.kotlin.android) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.parcelize) apply false
    alias(libs.plugins.sqldelight) apply false
}

allprojects {
    tasks.withType<Test> {
        testLogging {
            events("passed", "skipped", "failed")
        }
    }
}

subprojects {
}


fun testArgs(vararg testNames: String): String {
    var args = ""
    testNames.forEach { testName ->
        args += "--tests $testName "
    }
    return args
}

fun ordered(vararg dependencyPaths: String): List<Task> {
    val dependencies = dependencyPaths.map { tasks.getByPath(it) }
    for (i in 0 until dependencies.size - 1) {
        dependencies[i + 1].mustRunAfter(dependencies[i])
    }
    return dependencies
}

fun allSubprojectsTasks(subprojects: Set<Project>, name: String): List<Task> {
    val allUnitTestTasks = mutableListOf<Task>()
    subprojects.forEach { project ->
        project.tasks.findByName(name)?.let { allUnitTestTasks.add(it) }
    }
    return allUnitTestTasks
}