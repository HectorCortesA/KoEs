import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidMultiplatformLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    id("app.cash.sqldelight") version "2.3.2"
}

kotlin {
    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "Shared"
            isStatic = true
            linkerOpts("-lsqlite3")
            binaryOption("bundleId", "com.hector.koes.shared")
        }
    }
    
    android {
       namespace = "com.hector.koes.shared"
       compileSdk = libs.versions.android.compileSdk.get().toInt()
       minSdk = libs.versions.android.minSdk.get().toInt()
    
       compilerOptions {
           jvmTarget = JvmTarget.JVM_11
       }
       androidResources {
           enable = true
       }
       withHostTest {
           isIncludeAndroidResources = true
       }
       withDeviceTestBuilder {
           sourceSetTreeName = "test"
       }.configure {
           instrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
       }
    }
    
    sourceSets {
        androidMain.dependencies {
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.compose.uiTooling)
            implementation("app.cash.sqldelight:android-driver:2.3.2")
        }

        iosMain.dependencies {
            implementation("app.cash.sqldelight:native-driver:2.3.2")
        }

        commonMain.dependencies {
            implementation("io.coil-kt.coil3:coil-compose:3.0.4")
            implementation("io.coil-kt.coil3:coil-svg:3.0.4")
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material3)
            implementation(libs.compose.ui)
            implementation(libs.compose.components.resources)
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)
            implementation("app.cash.sqldelight:runtime:2.3.2")
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}
compose.resources {
    packageOfResClass = "com.hector.koes.resources"
}
sqldelight {
    databases {
        create("KoEsDatabase") {
            packageName.set("com.hector.koes.database")
        }
    }
}

dependencies {
    androidRuntimeClasspath(libs.compose.uiTooling)
}