import java.net.URI

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.lurobaca.jarvis"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.lurobaca.jarvis"
        minSdk = 26
        targetSdk = 35
        versionCode = 3
        versionName = "0.2.1"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions { jvmTarget = "17" }
}

dependencies {
    implementation("androidx.core:core-ktx:1.15.0")
    implementation("com.alphacephei:vosk-android:0.3.47")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
}

val voskModelDirectory = layout.projectDirectory.dir("src/main/assets/model-en-us")
val downloadVoskModel by tasks.registering {
    description = "Downloads the offline English Vosk model bundled in the APK"
    outputs.dir(voskModelDirectory)

    doLast {
        val destination = voskModelDirectory.asFile
        if (destination.resolve("am/final.mdl").exists()) return@doLast

        val archive = layout.buildDirectory.file("downloads/vosk-model-small-en-us-0.15.zip").get().asFile
        archive.parentFile.mkdirs()
        URI("https://alphacephei.com/vosk/models/vosk-model-small-en-us-0.15.zip")
            .toURL()
            .openStream()
            .use { input -> archive.outputStream().use(input::copyTo) }

        copy {
            from(zipTree(archive))
            into(destination)
            includeEmptyDirs = false
            eachFile { path = path.substringAfter('/') }
        }
        destination.resolve("uuid").writeText("jarvis-vosk-en-us-0.15")
    }
}

tasks.named("preBuild").configure { dependsOn(downloadVoskModel) }
