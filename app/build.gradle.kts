

plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.myapplication"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.myapplication"
        minSdk = 21
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    buildFeatures {
        viewBinding = true
        buildConfig = true
    }
}

dependencies {

    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")
    implementation("androidx.navigation:navigation-fragment:2.7.7")
    implementation("androidx.navigation:navigation-ui:2.7.7")
    implementation("androidx.activity:activity:1.8.2")
    implementation("org.chromium.net:cronet-embedded:119.6045.31")
    implementation ("com.jakewharton.threetenabp:threetenabp:1.3.1")
    implementation ("androidx.core:core:1.6.0")
    implementation ("androidx.work:work-runtime-ktx:2.9.0")

    //google map & place
    implementation("com.google.android.gms:play-services-maps:18.2.0")
    implementation("com.google.android.gms:play-services-location:21.2.0")
    implementation("com.google.maps.android:android-maps-utils:1.3.1")

    implementation("com.google.android.libraries.places:places:3.4.0")
    implementation(platform("org.jetbrains.kotlin:kotlin-bom:1.9.20"))

    //Retrofit
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")

    // OkHttpClient
    implementation ("com.squareup.okhttp3:okhttp:4.9.0")
    implementation ("com.squareup.okhttp3:logging-interceptor:4.9.0")

    //Gson
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    //firebase
    implementation(platform("com.google.firebase:firebase-bom:32.8.0"))
    implementation("com.google.firebase:firebase-auth:22.3.1")
    implementation("com.google.firebase:firebase-core:21.1.1")
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.android.gms:play-services-auth:21.2.0")
    implementation("com.google.firebase:firebase-firestore:25.0.0")
    implementation("com.google.firebase:firebase-messaging:24.0.0")

    //Facebook
    implementation("com.facebook.android:facebook-android-sdk:16.0.0")

    //Glide
    implementation("com.github.bumptech.glide:glide:4.13.0")

    //Picasso
    implementation ("com.squareup.picasso:picasso:2.8")



    //Test

    // Test
    // JUnit 4
    testImplementation("junit:junit:4.13.2")

    // Mockito
    testImplementation("org.mockito:mockito-core:4.0.0")
    androidTestImplementation("org.mockito:mockito-android:4.0.0")

    // AndroidX Test
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation("androidx.test.espresso:espresso-intents:3.5.1")
    androidTestImplementation("androidx.test.espresso:espresso-contrib:3.5.1")
    testImplementation("androidx.arch.core:core-testing:2.2.0")
    androidTestImplementation("androidx.test:rules:1.5.0")
    androidTestImplementation("androidx.test:runner:1.5.2")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:1.2.0")
    androidTestImplementation("androidx.test.uiautomator:uiautomator:2.2.0")

    // Robolectric
    testImplementation("org.robolectric:robolectric:4.7.3")

    // MockWebServer pour simuler les serveurs HTTP dans les tests
    testImplementation("com.squareup.okhttp3:mockwebserver:4.9.1")

    // Firebase Firestore et Auth pour les tests
    testImplementation("com.google.firebase:firebase-firestore:25.0.0")
    testImplementation("com.google.firebase:firebase-auth:21.0.0")

    // AndroidX Test Core pour les tests unitaires Android
    testImplementation("androidx.test:core:1.5.0")

    // ThreeTenBP pour les tests de date/heure
    testImplementation("org.threeten:threetenbp:1.3.2")

    debugImplementation("androidx.fragment:fragment-testing:1.8.2")

    androidTestImplementation ("androidx.test.uiautomator:uiautomator:2.3.0")
}
