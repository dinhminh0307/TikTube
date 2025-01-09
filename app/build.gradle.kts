plugins {
    alias(libs.plugins.android.application)
    // Add the Google services Gradle plugin
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.tiktube"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.tiktube"
        minSdk = 24
        targetSdk = 34
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
    buildFeatures {
        viewBinding = true
    }

    packagingOptions {
        exclude("META-INF/DEPENDENCIES")
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.constraintlayout)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    implementation(libs.activity)
    implementation(libs.play.services.auth)
    implementation(libs.firebase.config)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    // Import the Firebase BoM
    implementation(platform("com.google.firebase:firebase-bom:33.6.0"))

    // When using the BoM, you don't specify versions in Firebase library dependencies

    // Add the dependency for the Firebase SDK for Google Analytics
    implementation("com.google.firebase:firebase-analytics")

    // TODO: Add the dependencies for any other Firebase products you want to use
    // See https://firebase.google.com/docs/android/setup#available-libraries
    // For example, add the dependencies for Firebase Authentication and Cloud Firestore
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-firestore")

    // Import the BoM for the Firebase platform
    implementation(platform("com.google.firebase:firebase-bom:33.6.0"))

    // Add the dependency for the Firebase Authentication library
    // When using the BoM, you don't specify versions in Firebase library dependencies
    implementation("com.google.firebase:firebase-auth")
    // Add YouTube Android Player API
//    implementation("com.google.android.youtube:youtube-android-player-api:1.2.2")
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    implementation("com.google.android.gms:play-services-auth:21.1.1")
    implementation("com.google.api-client:google-api-client-android:1.31.5")
    implementation("com.google.apis:google-api-services-drive:v3-rev197-1.25.0")
    implementation("com.google.http-client:google-http-client-gson:1.42.3")
    implementation ("com.github.bumptech.glide:glide:4.14.2")
    implementation ("com.google.auth:google-auth-library-oauth2-http:1.15.0")
    implementation ("com.github.bumptech.glide:glide:4.15.1")
    annotationProcessor ("com.github.bumptech.glide:compiler:4.15.1")

    // stripe api
    implementation ("com.stripe:stripe-android:20.24.1")

    //http method
    implementation ("com.squareup.okhttp3:okhttp:4.11.0" )// OkHttp for HTTP requests
    implementation ("org.json:json:20211205") // JSON parsing

    implementation ("io.github.cdimascio:java-dotenv:5.2.2")

}