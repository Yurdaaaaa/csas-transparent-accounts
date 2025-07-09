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
//    id("com.github.ben-manes.versions") version libs.versions.gradle.versions.plugin.ben.manes apply false
//    id("com.android.application") version libs.versions.agp apply false
//    id("com.android.library") version libs.versions.comAndroidLibrary apply false
//    id("org.jetbrains.kotlin.android") version libs.versions.kotlin apply false
//    id("org.jetbrains.kotlin.kapt") version libs.versions.kotlin apply false
//    id("com.google.firebase.crashlytics") version libs.versions.crashlyticsGradle apply false
//    id("org.jetbrains.kotlin.plugin.parcelize") version libs.versions.kotlin apply false
//    id("app.cash.sqldelight") version libs.versions.sqlDelight apply false

//    alias(libs.plugins.gradle.versions) apply false
//    id("com.github.ben-manes.versions") version libs.versions.gradle.versions.plugin.ben.manes apply false
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