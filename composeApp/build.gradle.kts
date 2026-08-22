import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.gradle.api.tasks.JavaExec
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.composeHotReload)
    alias(libs.plugins.kotlin.serialization)
}

kotlin {
    androidTarget {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }

    jvm()

    sourceSets {
        androidMain.dependencies {
            implementation(compose.preview)
            implementation(libs.androidx.activity.compose)

        }
        commonMain.dependencies {
            implementation("androidx.compose.material3:material3-window-size-class:1.2.1")
            implementation(compose.materialIconsExtended)
            implementation("androidx.compose.material3:material3-adaptive-navigation-suite:1.3.2")
            implementation("io.github.compose-fluent:fluent:v0.1.0")
            implementation("io.github.compose-fluent:fluent-icons-extended:v0.1.0")
            implementation("com.attafitamim.krop:ui:0.2.0")
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)


            implementation(libs.jetbrains.navigation3.ui)
            implementation(libs.jetbrains.lifecycle.viewmodel.nav3)
            implementation(libs.jetbrains.lifecycle.viewmodel)
            implementation(libs.kotlinx.serialization.json)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
        jvmMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutinesSwing)
            implementation("net.java.dev.jna:jna-platform:5.14.0")
        }
    }
}

android {
    namespace = "app.lantr.lantr"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "app.lantr.lantr"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
    }
    lint {
        disable.add("NullSafeMutableLiveData")
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    debugImplementation(compose.uiTooling)
}

compose.desktop {
    application {
        mainClass = "app.lantr.lantr.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "Lantr"
            packageVersion = "1.0.0"
            fileAssociation(
                mimeType = "application/x-lantr-presentation",
                extension = "ltrn",
                description = "Lantr Presentation",
                windowsIconFile = project.file("media/AppIcon.ico")
            )
            windows {
                iconFile.set(project.file("media/AppIcon.ico"))
            }
        }
    }
}

compose.resources {
    packageOfResClass = "app.lantr.lantr.generated.resources"
    publicResClass = true
    generateResClass = auto
}

tasks.withType<JavaExec>()
    .matching { it.name == "run" || it.name == "runRelease" }
    .configureEach {
        val ltrnFileProvider = providers.gradleProperty("ltrnFile")

        doFirst {
            ltrnFileProvider.orNull?.takeIf { it.isNotBlank() }?.let { ltrnFile ->
                args(ltrnFile)
            }
        }
    }
