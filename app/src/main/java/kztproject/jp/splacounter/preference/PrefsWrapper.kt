package kztproject.jp.splacounter.preference

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager

object PrefsWrapper {
    private lateinit var prefs: SharedPreferences
    private const val userIdKey = "user_id"

    fun initialize(context: Context) {
        prefs = PreferenceManager.getDefaultSharedPreferences(context)
    }

    var userId: Long
        get() = prefs.getLong(userIdKey, 0)
        set(value) = prefs.edit().putLong(userIdKey, value).apply()
}