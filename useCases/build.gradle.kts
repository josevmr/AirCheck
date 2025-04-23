plugins {
    id("java-library")
    alias(libs.plugins.jetbrains.kotlin.jvm)
    alias(libs.plugins.ksp)
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

kotlin {
    compilerOptions {
        jvmTarget = org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11
    }
}
dependencies {
    implementation(project(":domain"))
    implementation(project(":data"))
    implementation(libs.kotlinx.coroutines.core)

    //Koin
    implementation(platform(libs.koin.bom))
    implementation(libs.koin.core)

    //Koin ksp
    implementation(libs.koin.annotations)
    ksp(libs.koin.compiler)

    //Mockito, test coroutines, turbine
    implementation(libs.mockito.kotlin)
    implementation(libs.kotlin.coroutine.test)
    implementation(libs.turbine)
    testImplementation(libs.junit)
}