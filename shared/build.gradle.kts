plugins {
    id("java-library")
    alias(libs.plugins.jetbrains.kotlin.jvm)
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

    //Mockito, test coroutines, turbine
    implementation(libs.mockito.kotlin)
    implementation(libs.kotlin.coroutine.test)
    implementation(libs.turbine)
    testImplementation(libs.junit)
}
