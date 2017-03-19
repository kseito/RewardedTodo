package kztproject.jp.splacounter.preference;

import android.content.Context;

/**
 * need wrap class
 * http://sys1yagi.hatenablog.com/entry/2015/08/16/003051
 */
public class AppPrefsProvider {
    AppPrefs prefs;
    public AppPrefsProvider(Context context) {
        prefs = AppPrefs.get(context);
    }
    public AppPrefs get() {
        return prefs;
    }
}
