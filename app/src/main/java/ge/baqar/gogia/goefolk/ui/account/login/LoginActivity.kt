package ge.baqar.gogia.goefolk.ui.account.login

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import ge.baqar.gogia.goefolk.databinding.ActivityLoginBinding
import ge.baqar.gogia.goefolk.model.LoginModel
import ge.baqar.gogia.goefolk.storage.FolkAppPreferences
import ge.baqar.gogia.goefolk.ui.MenuActivity
import ge.baqar.gogia.goefolk.ui.account.register.RegisterActivity
import ge.baqar.gogia.goefolk.utility.DeviceId
import ge.baqar.gogia.goefolk.utility.TokenValidator
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import reactivecircus.flowbinding.android.view.clicks
import timber.log.Timber
import kotlin.time.ExperimentalTime

class LoginActivity : AppCompatActivity() {

    private val viewModel: LoginViewModel by viewModel()
    private var binding: ActivityLoginBinding? = null
    private val preferences: FolkAppPreferences by inject()
    private val deviceId: DeviceId by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        if (preferences.getToken() != null && !TokenValidator.isJWTExpired(preferences.getToken()!!)) {
            startMenuActivity()
            return
        } else {
            preferences.setToken(null)
        }

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        binding?.loginModel = LoginModel(null, null)
        initializeIntents(
            binding?.loginButton?.clicks()
                ?.map {
                    LoginRequested(
                        binding?.loginModel?.email!!,
                        binding?.loginModel?.password!!,
                        deviceId.get()!!
                    )
                }!!
        )

        binding?.registrationLinkText?.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun initializeIntents(inputs: Flow<LoginActions>) {
        viewModel.intents(inputs)
            .onEach { output ->
                when (output) {
                    is LoginState -> render(output)
                }
            }
            .launchIn(lifecycleScope)
    }

    private fun render(output: LoginState) {
        if (output.isInProgress) {
            disableUi()
            return
        }

        enableUi()
        if (output.error != null) {
            Toast.makeText(this, output.error, Toast.LENGTH_SHORT).show()
            Timber.i(output.error)
            return
        }

        output.token?.let {
            viewModel.storeToken(it)
            startMenuActivity()
        }
    }

    private fun enableUi() {
        binding?.loginButton?.isEnabled = true
        binding?.emailEditText?.isEnabled = true
        binding?.passwordEditText?.isEnabled = true
    }

    private fun disableUi() {
        binding?.loginButton?.isEnabled = false
        binding?.emailEditText?.isEnabled = false
        binding?.passwordEditText?.isEnabled = false
    }

    @OptIn(InternalCoroutinesApi::class, ExperimentalTime::class)
    fun startMenuActivity() {
        val intent = Intent(this, MenuActivity::class.java)
        startActivity(intent)
        finish()
    }
}