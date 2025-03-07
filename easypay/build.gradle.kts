import com.vanniktech.maven.publish.AndroidSingleVariantLibrary

plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("com.vanniktech.maven.publish") version "0.28.0"
    id("org.jetbrains.kotlin.plugin.parcelize")
}

android {
    namespace = "com.easypaysolutions"
    compileSdk = 34

    defaultConfig {
        minSdk = 29

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }
    buildFeatures {
        buildConfig = true
    }
    buildTypes {
        debug {
            // TODO: Replace URL with demo one
            buildConfigField(
                "String",
                "API_URL",
                "\"https://easypay5.com/APIcardProcMobile/\""
            )
            buildConfigField("String", "API_VERSION", "\"v1.0.0\"")
            buildConfigField(
                "String",
                "SESSION_KEY_FOR_TESTS",
                "\"${System.getenv("SESSION_KEY") ?: ""}\""
            )
            buildConfigField(
                "String",
                "HMAC_SECRET_FOR_TESTS",
                "\"${System.getenv("HMAC_SECRET") ?: ""}\""
            )
        }
        release {
            buildConfigField(
                "String",
                "API_URL",
                "\"https://easypay5.com/APIcardProcMobile/\""
            )
            buildConfigField("String", "API_VERSION", "\"v1.0.0\"")
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
}

mavenPublishing {
    coordinates(artifactId = "easypay")

    configure(
        AndroidSingleVariantLibrary(
            variant = "release",
            sourcesJar = true,
            publishJavadocJar = true,
        )
    )
}

dependencies {

    implementation("androidx.core:core-ktx:1.13.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.test:core-ktx:1.5.0")

    // Tests
    androidTestImplementation("androidx.test:runner:1.5.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.mockito:mockito-core:5.11.0")
    testImplementation("org.mockito.kotlin:mockito-kotlin:5.2.1")
    testImplementation("com.squareup.okhttp3:mockwebserver:4.12.0")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.8.0")

    //Koin
    val koinVersion = "3.5.4"
    implementation("io.insert-koin:koin-android:$koinVersion")

    //Retrofit2
    val retrofit2Version = "2.11.0"
    implementation("com.squareup.retrofit2:retrofit:$retrofit2Version")
    implementation("com.squareup.retrofit2:converter-gson:$retrofit2Version")

    //OkHttp3
    val okhttp3Version = "4.12.0"
    implementation("com.squareup.okhttp3:okhttp:$okhttp3Version")
    implementation("com.squareup.okhttp3:logging-interceptor:$okhttp3Version")

    //RootBeer
    val rootbeerVersion = "0.1.0"
    implementation("com.scottyab:rootbeer-lib:$rootbeerVersion")

    //Sentry
    val sentryVersion = "7.8.0"
    implementation("io.sentry:sentry-android:$sentryVersion")
}