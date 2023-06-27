package ge.baqar.gogia.goefolk.ui.account

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import ge.baqar.gogia.goefolk.R
import ge.baqar.gogia.goefolk.storage.FolkAppPreferences
import ge.baqar.gogia.goefolk.ui.MenuActivity
import ge.baqar.gogia.goefolk.utility.TokenValidator
import kotlinx.coroutines.InternalCoroutinesApi
import org.koin.android.ext.android.inject
import kotlin.time.ExperimentalTime

class AccountActivity : AppCompatActivity() {

    private val preferences: FolkAppPreferences by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        if (preferences.getToken() != null && !TokenValidator.isJWTExpired(preferences.getToken()!!)) {
            startMenuActivity()
            return
        } else {
            preferences.setToken(null)
        }

        setContentView(R.layout.activity_account)
    }

    @OptIn(InternalCoroutinesApi::class, ExperimentalTime::class)
    fun startMenuActivity() {
        val intent = Intent(this, MenuActivity::class.java)
        startActivity(intent)
        finish()
    }
}