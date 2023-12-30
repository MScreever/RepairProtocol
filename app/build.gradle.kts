plugins {
    id("com.android.application")
}

android {
    namespace = "com.saxion.repairprotocol"
    compileSdk = 33

    defaultConfig {
        applicationId = "com.saxion.repairprotocol"
        minSdk = 19 // Android 4.4
        targetSdk = 33 // Android 13
        versionCode = 1
        versionName = "1.0"
        multiDexEnabled = true

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
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
    implementation("com.android.support:multidex:1.0.3")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.8.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("javax.xml.stream:stax-api:1.0-2")
    implementation("org.codehaus.woodstox:woodstox-core-asl:4.4.1")
    implementation("org.apache.commons:commons-compress:1.21")
    implementation("androidx.gridlayout:gridlayout:1.0.0")
    implementation("eu.agno3.jcifs:jcifs-ng:2.1.9")
    implementation(files("libs\\poi-ooxml-schemas-3.12-20150511-a.jar"))
    implementation(files("libs\\poi-3.12-android-a.jar"))
    implementation("androidx.navigation:navigation-fragment:2.5.3")
    implementation("androidx.navigation:navigation-ui:2.5.3")
    implementation("com.google.android.exoplayer:exoplayer:2.18.1")
    implementation("com.google.android.exoplayer:exoplayer-ui:2.18.1")
    implementation("com.google.zxing:core:3.4.1")
    implementation("com.journeyapps:zxing-android-embedded:3.6.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}