package ge.baqar.gogia.goefolk.storage

import android.content.Context
import android.content.SharedPreferences

class FolkAppPreferences(private val context: Context) {
    private val preferences: SharedPreferences by lazy {
        context.getSharedPreferences(context.packageName, Context.MODE_PRIVATE)!!
    }
    private val playerControlsAreVisibleKey = "playerControlsAreVisible"
    private val autoPlayEnabledKey = "autoPlayEnabledKey"
    private val timerSetKey = "timerSetKey"
    private val tokenKey = "tokenKey"
    private val currentSong = "currentSong"
    private val currentArtist = "currentArtist"

    fun updateAutoPlay(autoPlayEnabled: Int) {
        preferences.edit()
            .putInt(autoPlayEnabledKey, autoPlayEnabled)
            .apply()
    }

    fun getAutoPlay(): Int {
        return preferences.getInt(
            autoPlayEnabledKey,
            ge.baqar.gogia.goefolk.model.AutoPlayState.OFF
        )
    }

    fun setPlayerState(playerControlsAreVisible: Boolean) {
        preferences.edit()
            .putBoolean(playerControlsAreVisibleKey, playerControlsAreVisible)
            .apply()
    }

    fun setTimerSet(enabled: Boolean) {
        preferences.edit()
            .putBoolean(timerSetKey, enabled)
            .apply()
    }

    fun getTimerSet(): Boolean {
        return preferences.getBoolean(timerSetKey, false)
    }

    fun setToken(token: String?) {
        preferences.edit()
            .putString(tokenKey, token)
            .apply()
    }

    fun getToken(): String? {
        return preferences.getString(tokenKey, null)
    }

    fun setCurrentSong(id: String) {
        preferences.edit()
            .putString(currentSong, id)
            .apply()
    }

    fun getCurrentSong(): String? {
        return preferences.getString(currentSong, null)
    }

    fun setCurrentArtist(artistId: String) {
        preferences.edit()
            .putString(currentArtist, artistId)
            .apply()
    }

    fun getCurrentArtist(): String? {
        return preferences.getString(currentArtist, null)
    }
}