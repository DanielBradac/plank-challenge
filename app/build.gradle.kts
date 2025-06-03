plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
}

android {
    namespace = "cz.bradacd.plankchallenge"
    compileSdk = 34

    defaultConfig {
        applicationId = "cz.bradacd.plankchallenge"
        minSdk = 29
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1,INDEX.LIST,DEPENDENCIES}"
        }
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    //ViewModel
    implementation ("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.1")

    // Extended Icons
    implementation("androidx.compose.material:material-icons-extended:1.6.7")

    // Navigation
    implementation("androidx.navigation:navigation-compose:2.7.7")

    // GSON
    //implementation("com.google.code.gson:gson:2.9.0")

    // Google APIs
    //implementation("com.google.api-client:google-api-client-android:1.34.0")
    //implementation("com.google.api-client:google-api-client-gson:1.34.0")
    //implementation("com.google.auth:google-auth-library-oauth2-http:1.19.0")
    implementation(libs.google.api.services.sheets)
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.1")
}