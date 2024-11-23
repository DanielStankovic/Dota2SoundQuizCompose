plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.android.hilt)
    kotlin("kapt")
    alias(libs.plugins.google.gms.google.services)
    alias(libs.plugins.google.firebase.crashlytics)
}

android {
    namespace = "com.dsapps2018.dota2guessthesound"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.dsapps2018.dota2guessthesound"
        minSdk = 29
        targetSdk = 35
        versionCode = 10
        versionName = "2.0.2"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        android.buildFeatures.buildConfig = true

        getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }

        getByName("debug") {
            isDebuggable = true
        }

    }

    flavorDimensions += "version"

    productFlavors {
        create("prod") {
            resValue("string", "app_name", "Dota 2 Sound Quiz")
            dimension = "version"
            buildConfigField("String", "BASE_URL", "\"https://tzmsnmqbajpgwcvnokgf.supabase.co\"")
            buildConfigField(
                "String",
                "ANON_KEY",
                "\"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InR6bXNubXFiYWpwZ3djdm5va2dmIiwicm9sZSI6ImFub24iLCJpYXQiOjE3Mjg5ODU0MDQsImV4cCI6MjA0NDU2MTQwNH0.9tEc6usKR6k1HmSnoq4CHNcVLKMMDXjBYuFC7fHTRTk\""
            )

            resValue("string", "AdMob_App_Id", "ca-app-pub-2701841893001237~2192936151")
            resValue("string", "banner_id", "ca-app-pub-2701841893001237/7964137584")
            resValue("string", "interstitial_ad_id", "ca-app-pub-2701841893001237/6035623027")
            resValue("string", "rewarded_video_ad_id", "ca-app-pub-2701841893001237/7830731058")

            manifestPlaceholders["appIcon"] =  "@mipmap/ic_launcher"
            manifestPlaceholders["appIconRound"] =  "@mipmap/ic_launcher_round"
        }

        create("staging") {
            resValue("string", "app_name", "Dota 2 Sound Quiz - DEV")
            dimension = "version"
            applicationIdSuffix = ".dev"
            buildConfigField("String", "BASE_URL", "\"https://ljuylgejhmmtxulyfojd.supabase.co\"")
            buildConfigField(
                "String",
                "ANON_KEY",
                "\"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImxqdXlsZ2VqaG1tdHh1bHlmb2pkIiwicm9sZSI6ImFub24iLCJpYXQiOjE3MzAyMTY2NTMsImV4cCI6MjA0NTc5MjY1M30.OmSctnEaZE0oedK90iWz3sJdSZ4UJhjJOZxIPwyhBko\""
            )

            resValue("string", "AdMob_App_Id", "ca-app-pub-3940256099942544~3347511713")
            resValue("string", "banner_id", "ca-app-pub-3940256099942544/9214589741")
            resValue("string", "interstitial_ad_id", "ca-app-pub-3940256099942544/1033173712")
            resValue("string", "rewarded_video_ad_id", "ca-app-pub-3940256099942544/5224354917")
            
            manifestPlaceholders["appIcon"] =  "@mipmap/ic_launcher_dev"
            manifestPlaceholders["appIconRound"] =  "@mipmap/ic_launcher_dev_round"
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

    //Hilt
    implementation(libs.hilt.android)
    implementation(libs.hilt.compose)
    kapt(libs.hilt.compiler)

    //Room
    implementation(libs.room)
    implementation(libs.room.ktx)
    implementation(libs.room.paging)
    annotationProcessor(libs.room.compailer)
    kapt(libs.room.compailer)

    //Kotlinx Serialization
    implementation(libs.kotlinx.serialization)

    //Ktor
    implementation(platform(libs.ktor.bom))
    implementation(libs.ktor.client.android)
    implementation(libs.ktor.client.logging)

    //Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.crashlytics)
    implementation(libs.firebase.messaging)
    implementation(libs.firebase.analytics)


    //Supabase
    implementation(platform(libs.supabase.bom))
    implementation(libs.supabase.postgrest)
    implementation(libs.supabase.storage)

    //Navigation
    implementation(libs.androidx.navigation)

    //Admob
    implementation (libs.play.services.ads)

    //Permissions
    implementation (libs.accompanist.permissions)

    //Play Review
    implementation (libs.play.review)
    implementation (libs.play.review.ktx)

}

kapt {
    correctErrorTypes = true
}