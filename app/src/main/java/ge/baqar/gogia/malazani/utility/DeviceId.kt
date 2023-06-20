package ge.baqar.gogia.malazani.utility

import android.annotation.SuppressLint
import android.content.Context
import android.provider.Settings

class DeviceId(private val context: Context) {
    @SuppressLint("HardwareIds")
    fun get (): String? {
        return Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
    }
}