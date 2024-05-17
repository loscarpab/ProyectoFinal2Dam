plugins {
    id("org.jetbrains.kotlin.android")
    //Figma
    id("com.google.relay") version "0.3.02"
    id("com.android.application")
    //Firebase
    id("com.google.gms.google-services")
    //Dagger Hilt
    kotlin("kapt")
    id("com.google.dagger.hilt.android")
}

android {
    namespace = "com.ccormor392.pruebaproyectofinal"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.ccormor392.pruebaproyectofinal"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.3"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.activity:activity-compose:1.8.2")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3:1.2.1")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2023.03.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
    //figma
    implementation("androidx.compose.material:material:1.4.0")
    //navigation
    implementation("androidx.navigation:navigation-compose:2.7.5")
    implementation(platform("com.google.firebase:firebase-bom:33.0.0"))
    implementation("com.google.firebase:firebase-analytics")
    //Servicio de Autenticación
    implementation("com.google.firebase:firebase-auth-ktx:23.0.0")
    //Base de datos Firestore
    implementation("com.google.firebase:firebase-firestore-ktx:25.0.0")
    //Base de datos Storage(guardar imagenes)
    implementation("com.google.firebase:firebase-storage-ktx:21.0.0")
    //Cargar imagenes asincronas
    implementation("io.coil-kt:coil-compose:2.5.0")
    //Dagger Hilt
    implementation("com.google.dagger:hilt-android:2.44")
    kapt("com.google.dagger:hilt-android-compiler:2.44")



}
//Dagger Hilt - Allow references to generated code
kapt {
    correctErrorTypes = true
}