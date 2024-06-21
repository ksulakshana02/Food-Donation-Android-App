plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.s22010213.wasteless"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.s22010213.wasteless"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    packaging {
        resources{
            excludes += "META-INF/INDEX.LIST"
            excludes += "META-INF/DEPENDENCIES"
        } // Replace with actual library path
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    //firebase libraries
    implementation("com.google.firebase:firebase-database:21.0.0")
    implementation("com.google.firebase:firebase-auth:23.0.0")
    implementation(platform("com.google.firebase:firebase-bom:33.0.0"))
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-storage:21.0.0")
    implementation ("com.google.firebase:firebase-messaging:24.0.0")
    //google maps libraries
    implementation ("com.google.android.gms:play-services-maps:18.2.0")
    implementation ("com.google.maps.android:android-maps-utils:2.2.3") //cluster
    implementation("com.google.android.libraries.places:places:3.5.0")

    implementation ("com.google.android.gms:play-services-auth:21.2.0")
//    {
//        exclude( group= "com.google.firebase", module= "firebase-core")
//    }
    // Facebook library
    implementation ("com.facebook.android:facebook-login:latest.release")
    // Image loading library
    implementation ("com.github.bumptech.glide:glide:4.16.0")
//    implementation ("com.google.cloud:google-cloud-core:2.23.0")
    implementation ("com.google.auth:google-auth-library-oauth2-http:1.23.0")

//    implementation ("javax.mail:javax.mail-api:1.6.2")
//    implementation ("com.sun.mail:javax.mail:1.6.2")
//    implementation ("com.sun.mail:android-activation:1.6.6")
    implementation (files("libs/activation.jar"))
    implementation (files("libs/additionnal.jar"))
    implementation (files("libs/mail.jar"))

//    implementation ("com.amazonaws:aws-android-sdk-core:2.56.1")
//    implementation ("com.amazonaws:aws-android-sdk-ses:2.56.1")


    implementation ("com.squareup.okhttp3:okhttp:4.12.0")
    implementation ("com.squareup.okhttp3:logging-interceptor:4.9.2")
    implementation ("org.json:json:20210307")
    annotationProcessor ("com.github.bumptech.glide:compiler:4.12.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}