plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

apply(from = rootProject.file("gradle/android_common.gradle"))
apply(from = rootProject.file("gradle/ktlint.gradle"))

dependencies {

    implementation("androidx.core:core-ktx:1.8.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.9.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}
