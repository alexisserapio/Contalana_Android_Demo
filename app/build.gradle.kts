plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ksp.contalana)
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "com.alexisserapio.contalana_prototipe"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.alexisserapio.contalana_prototipe"
        minSdk = 24
        targetSdk = 36
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
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures{
        viewBinding = true
    }


}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.material3)

    //Mis Implementaciones
    //PageController
    implementation(libs.androidx.viewpager2)
    //PageIndicator
    implementation(libs.material.v190)
    //Tink
    implementation(libs.tink.android)
    //datastore preferences
    implementation(libs.androidx.datastore.preferences)
    //lifecycle
    implementation (libs.androidx.lifecycle.viewmodel.ktx)
    //SplashScreen
    implementation(libs.androidx.core.splashscreen)
    //Room
    implementation(libs.androidx.room.ktx)

    implementation(libs.firebase.auth)

    ksp(libs.androidx.room.compiler)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

}