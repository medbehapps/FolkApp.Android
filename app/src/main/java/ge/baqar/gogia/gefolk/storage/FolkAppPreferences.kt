package ge.baqar.gogia.gefolk.storage

import android.content.Context
import android.content.SharedPreferences

class FolkAppPreferences(private val context: Context) {
    private val preferences: SharedPreferences by lazy {
        context.getSharedPreferences(context.packageName, Context.MODE_PRIVATE)!!
    }
    private val autoPlayEnabledKey = "autoPlayEnabledKey"
    private val tokenKey = "tokenKey"

    fun updateAutoPlay(autoPlayEnabled: Int) {
        preferences.edit()
            .putInt(autoPlayEnabledKey, autoPlayEnabled)
            .apply()
    }

    fun getAutoPlay(): Int {
        return preferences.getInt(
            autoPlayEnabledKey,
            ge.baqar.gogia.gefolk.model.AutoPlayState.OFF
        )
    }

    fun setToken(token: String?) {
        preferences.edit()
            .putString(tokenKey, token)
            .apply()
    }

    fun getToken(): String? {
        return preferences.getString(tokenKey, null)
    }
}