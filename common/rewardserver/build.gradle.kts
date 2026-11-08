plugins { alias(libs.plugins.rewardedtodo.android.library) }
android { namespace = "jp.kztproject.rewardedtodo.common.rewardserver" }
dependencies {
    implementation(project(":common:kvs"))
    implementation(libs.hilt.android)
    implementation(libs.datastore.preferences)
    implementation(libs.coroutines.core)
    api(libs.retrofit.core)
    implementation(libs.moshi)
}
