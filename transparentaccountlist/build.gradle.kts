plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("com.google.devtools.ksp")
}

android {
    namespace = "com.zj.csastest.transparentaccountlist"
    compileSdk = libs.versions.compileSdk.get().toInt()


    defaultConfig {
        minSdk = libs.versions.minSdk.get().toInt()
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        consumerProguardFiles("consumer-rules.pro")
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
}

dependencies {

//    implementation (libs.threetenAbp) // todo use date mapper in future

    implementation (libs.appCompat)
    implementation (libs.recyclerView)
    implementation (libs.materialDesign)
    implementation (libs.swipeRefreshLayout)

    implementation (libs.daggerRuntime)
    ksp (libs.daggerCompiler)

    implementation (libs.rxJava)
    implementation (libs.rxAndroid)
    implementation (libs.rxKotlin)
    implementation (libs.rxRelay)

    implementation (libs.okhttpClient)
    implementation (libs.okhttpLoggingInterceptor)
    implementation (libs.retrofitClient)
    implementation (libs.retrofitRxjavaAdapter)
    implementation (libs.retrofitMoshiConverter)

    implementation (libs.moshiClient)
    ksp (libs.moshiCompiler)

    implementation (libs.sqlDelightDriver)
    implementation (libs.sqlDelightRxJava)

    implementation (libs.conductorRuntime)

    implementation(project(":core"))

//    testImplementation (libs.sqlDelightJdbcDriver)
//    testImplementation deps.test.mockitoCore
//    testImplementation deps.test.mockitoKotlin
//    testImplementation deps.test.junit
//    testImplementation 'org.mockito:mockito-inline:2.13.0'
//    testImplementation 'io.mockk:mockk:1.12.0'
}