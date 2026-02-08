import com.android.build.gradle.internal.api.BaseVariantOutputImpl

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.devtools.ksp)
    alias(libs.plugins.hilt.android)
}

android {
    namespace = "com.kgzn.gamecenter"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.kgzn.gamecenter"
        minSdk = 29
        targetSdk = 36
        val tag = "git tag".execute().text.trim().lineSequence().lastOrNull()?.takeIf { it.isNotBlank() } ?: "1.0.0"
        val hash = "git rev-parse --short HEAD".execute().text.trim().takeIf { it.isNotBlank() } ?: "unknown"
        //noinspection WrongGradleMethod
        versionCode = tag.split(".").map { it.toInt() }.reduce { acc, i -> acc.shl(8).or(i) }
        versionName = "$tag-$hash"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        register("MT9660") {
            storeFile = file("../keystore/MT9660.jks")
            keyAlias = "platform"
            keyPassword = "android"
            storePassword = "android"
        }
        register("MT9633") {
            storeFile = file("../keystore/MT9633.jks")
            keyAlias = "kgzn"
            keyPassword = "ktc123"
            storePassword = "ktc123"
        }
    }

    buildTypes {
        debug {
            signingConfig = signingConfigs.getByName("MT9633")
        }
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            signingConfig = signingConfigs.getByName("MT9633")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
        freeCompilerArgs = listOf("-XXLanguage:+PropertyParamAnnotationDefaultTargetMode")
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    applicationVariants.all {
        outputs.all {
            val outputImpl = this as BaseVariantOutputImpl
            outputImpl.outputFileName = "GameCenter_${versionName.substringBefore("-")}_${name}.apk"
        }
    }
    packaging {
        dex {
            useLegacyPackaging = true
        }
    }
}

fun String.execute(): ProcessResult {
    val process = ProcessBuilder(*split(" ").toTypedArray())
        .redirectOutput(ProcessBuilder.Redirect.PIPE)
        .redirectError(ProcessBuilder.Redirect.PIPE)
        .start()

    val output = process.inputStream.bufferedReader().readText()
    val error = process.errorStream.bufferedReader().readText()
    process.waitFor()

    return ProcessResult(output, error, process.exitValue())
}

data class ProcessResult(val text: String, val error: String, val exitCode: Int)

dependencies {

    // https://mvnrepository.com/artifact/com.squareup.retrofit2/retrofit
    implementation(libs.retrofit)
    // https://mvnrepository.com/artifact/com.squareup.retrofit2/converter-gson
    implementation(libs.converter.gson)
    // https://mvnrepository.com/artifact/com.squareup.retrofit2/converter-kotlinx-serialization
    implementation(libs.retrofit.kotlinx.serialization)
    // https://mvnrepository.com/artifact/io.coil-kt.coil3/coil
    implementation(libs.coil)
    // https://mvnrepository.com/artifact/io.coil-kt.coil3/coil-compose
    implementation(libs.coil.compose)
    // https://mvnrepository.com/artifact/io.coil-kt.coil3/coil-network-okhttp
    implementation(libs.coil.network.okhttp)
    // https://mvnrepository.com/artifact/io.coil-kt.coil3/coil-svg
    implementation(libs.coil.svg)
    // https://mvnrepository.com/artifact/com.google.code.gson/gson
    implementation(libs.gson)
    // https://mvnrepository.com/artifact/org.jetbrains.kotlinx/kotlinx-datetime
    implementation(libs.kotlinx.datetime)
    // https://mvnrepository.com/artifact/org.jetbrains.kotlinx/kotlinx-serialization-json-jvm
    implementation(libs.kotlinx.serialization.json.jvm)
    // https://mvnrepository.com/artifact/io.arrow-kt/arrow-core-jvm
    implementation(libs.arrow.core.jvm)
    // https://mvnrepository.com/artifact/com.squareup.okhttp3/okhttp-coroutines
    implementation(libs.okhttp.coroutines)
    // https://mvnrepository.com/artifact/com.squareup.okhttp3/okhttp
    implementation(libs.okhttp)
    // https://mvnrepository.com/artifact/androidx.room/room-runtime
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.ui.tooling)
    // https://mvnrepository.com/artifact/androidx.room/room-compiler
    ksp(libs.androidx.room.compiler)
    // https://mvnrepository.com/artifact/androidx.room/room-ktx
    implementation(libs.androidx.room.ktx)
    // https://mvnrepository.com/artifact/androidx.navigation/navigation-compose
    implementation(libs.androidx.navigation.compose)
    // https://mvnrepository.com/artifact/androidx.compose.ui/ui-tooling-preview
    implementation(libs.androidx.ui.tooling.preview)
    // https://mvnrepository.com/artifact/androidx.tv/tv-material
    implementation(libs.androidx.tv.material)
    // https://mvnrepository.com/artifact/androidx.compose.material3/material3
    implementation(libs.androidx.material3)
    // https://mvnrepository.com/artifact/androidx.activity/activity-compose
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    debugImplementation(libs.androidx.ui.tooling)

    // https://mvnrepository.com/artifact/androidx.palette/palette
    implementation(libs.androidx.palette)
    // https://mvnrepository.com/artifact/androidx.palette/palette-ktx
    implementation(libs.androidx.palette.ktx)
    // https://mvnrepository.com/artifact/jakarta.inject/jakarta.inject-api
    implementation(libs.jakarta.inject.api)
    // https://mvnrepository.com/artifact/com.google.dagger/hilt-android
    implementation(libs.hilt.android)
    // https://mvnrepository.com/artifact/com.google.dagger/hilt-android-compiler
    ksp(libs.hilt.android.compiler)
    // https://mvnrepository.com/artifact/androidx.hilt/hilt-navigation-compose
    implementation(libs.hilt.navigation.compose)
    // https://mvnrepository.com/artifact/androidx.tracing/tracing
    implementation(libs.androidx.tracing)

    // https://mvnrepository.com/artifact/androidx.datastore/datastore-core
    implementation(libs.androidx.datastore.core)
    // https://mvnrepository.com/artifact/androidx.datastore/datastore-preferences
    implementation(libs.androidx.datastore.preferences)

    // For EventReportSdk
    implementation(files("libs/EventReportSdk-debug.aar"))
    implementation("io.reactivex.rxjava3:rxjava:3.1.12")
    implementation("io.reactivex.rxjava3:rxandroid:3.0.2")
    implementation("io.reactivex.rxjava2:rxjava:2.2.21")
    implementation("io.reactivex.rxjava2:rxandroid:2.1.1")
    implementation("androidx.room:room-rxjava3:2.8.3")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
    implementation("com.squareup.retrofit2:adapter-rxjava2:3.0.0")

    implementation("me.jessyan:autosize:1.2.1")
}