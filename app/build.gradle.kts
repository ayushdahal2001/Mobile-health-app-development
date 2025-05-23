plugins {
    alias(libs.plugins.android.application)

    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.healthapp"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.healthapp"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(libs.material.v160)
    implementation(libs.appcompat.v161)
    implementation(libs.constraintlayout.v214)
    implementation(libs.activity.ktx)
    implementation(libs.firebase.database)

    testImplementation(libs.junit)
    androidTestImplementation(libs.junit.v115)
    androidTestImplementation(libs.espresso.core.v351)

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation (libs.material.v1100)
    implementation (libs.google.firebase.auth)
    implementation (libs.play.services.auth)
    implementation (libs.google.firebase.auth)
    implementation (libs.firebase.firestore)
    implementation (libs.recyclerview)
    implementation (libs.firebase.database.v2030)
    implementation (libs.firebase.core)
    implementation (libs.itext7.core)
    implementation (libs.core.ktx) // For file operations




}

