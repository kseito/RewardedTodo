package project.seito.screen_transition.preference

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager

class PrefsWrapper(context: Context) {
    private var prefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

    var userId: Long
        get() = prefs.getLong(userIdKey, 0)
        set(value) = prefs.edit().putLong(userIdKey, value).apply()

    companion object {
        private const val userIdKey = "user_id"
    }
}