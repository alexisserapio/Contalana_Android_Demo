import java.util.Properties

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

        //Leemos el archivo local.properties
        val localProperties = Properties()
        val localPropertiesFile = rootProject.file("local.properties")
        if(localPropertiesFile.exists()){
            localProperties.load(localPropertiesFile.inputStream())
        }

        //Leemos la propiedad
        val webClientId = localProperties.getProperty("WEB_CLIENT_ID")

        //Creamos el campo BuildConfig
        buildConfigField("String", "WEB_CLIENT_ID", "\"$webClientId\"")
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
        buildConfig = true
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
    //Glide
    implementation(libs.github.glide)
    //datastore preferences
    implementation(libs.androidx.datastore.preferences)
    //lifecycle
    implementation (libs.androidx.lifecycle.viewmodel.ktx)
    //SplashScreen
    implementation(libs.androidx.core.splashscreen)
    //Room
    implementation(libs.androidx.room.ktx)
    //Firebase Auth
    implementation(libs.firebase.auth)
    //Bibliotecas para Credential Manager
    implementation(libs.androidx.credentials)
    implementation(libs.androidx.credentials.play.services.auth)
    implementation(libs.googleid)
    //Barras
    implementation(libs.mpandroidchart)
    implementation(libs.androidx.lifecycle.runtime.ktx)

    ksp(libs.androidx.room.compiler)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

}