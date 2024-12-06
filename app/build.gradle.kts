plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.devtools.ksp") version "1.9.0-1.0.13"
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "com.example.keepnote"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.keepnote"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        testInstrumentationRunnerArguments["clearPackageData"] = "true"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures {
        viewBinding = true
        dataBinding = true
    }
}

dependencies {
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.1")
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    // Activity KTX
    implementation("androidx.activity:activity-ktx:1.7.2")

    // Fragment KTX (optional)
    implementation("androidx.fragment:fragment-ktx:1.6.0")

    // LiveData KTX (optional)
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.6.1")

    // Coroutine (optional)
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    implementation(libs.firebase.database)

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")

    val room_version = "2.6.1"
    implementation("androidx.room:room-runtime:$room_version")
    ksp("androidx.room:room-compiler:$room_version")

    // Room Kotlin Extensions and Coroutines support
    implementation("androidx.room:room-ktx:$room_version")

    testImplementation("androidx.room:room-testing:$room_version")

    androidTestImplementation("androidx.room:room-testing:$room_version")
    androidTestImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.9.0-RC.2")
}