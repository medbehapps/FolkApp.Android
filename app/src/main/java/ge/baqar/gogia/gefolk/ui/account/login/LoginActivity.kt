package ge.baqar.gogia.gefolk.ui.account.login

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.databinding.BindingAdapter
import androidx.lifecycle.lifecycleScope
import ge.baqar.gogia.gefolk.R
import ge.baqar.gogia.gefolk.databinding.ActivityLoginBinding
import ge.baqar.gogia.gefolk.model.LoginModel
import ge.baqar.gogia.gefolk.storage.FolkAppPreferences
import ge.baqar.gogia.gefolk.ui.account.register.RegisterActivity
import ge.baqar.gogia.gefolk.ui.media.AuthorizedActivity
import ge.baqar.gogia.gefolk.utility.DeviceId
import ge.baqar.gogia.gefolk.utility.TokenValidator
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import reactivecircus.flowbinding.android.view.clicks
import kotlin.time.ExperimentalTime


class LoginActivity : AppCompatActivity() {

    private val viewModel: LoginViewModel by viewModel()
    private lateinit var binding: ActivityLoginBinding
    private val preferences: FolkAppPreferences by inject()
    private val deviceId: DeviceId by inject()
    private var registerActivityLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == RESULT_OK) {
            val email = it.data?.getStringExtra("email")
            val password = it.data?.getStringExtra("password")

            binding.loginModel?.email = email
            binding.loginModel?.password = password
            if (binding.loginModel?.email != null && binding.loginModel?.password != null) {
                initializeIntents(
                    flowOf(
                        LoginRequested(
                            binding.loginModel?.email!!,
                            binding.loginModel?.password!!,
                            deviceId.get()!!
                        )
                    )
                )
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        if (preferences.getToken() != null) {
            if (!TokenValidator.isJWTExpired(preferences.getToken()!!)) {
                startMenuActivity()
                return
            }

            initializeIntents(
                flowOf(
                    LoginByTokenRequested(
                        preferences.getToken()!!,
                        deviceId.get()!!
                    )
                )
            )
            preferences.setToken(null)
        }

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.loginModel = LoginModel(null, null)
        binding.viewPassword = false
        initializeIntents(
            binding.loginButton.clicks()
                .map {
                    LoginRequested(
                        binding.loginModel?.email!!,
                        binding.loginModel?.password!!,
                        deviceId.get()!!
                    )
                }
        )

        binding.registrationLinkText.setOnClickListener {
            registerActivityLauncher.launch(Intent(this, RegisterActivity::class.java))
        }

        binding.showPasswordBtn.setOnClickListener {
            val viewPassword: Boolean? = binding.viewPassword
            if (viewPassword == true) {
                binding.viewPassword = false
            } else {
                binding.viewPassword = true
            }
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
            return
        }

        output.token?.let {
            viewModel.storeToken(it)
            startMenuActivity()
        }
    }

    private fun enableUi() {
        binding.progressBar.visibility = View.GONE
        binding.loginButton.isEnabled = true
        binding.emailEditText.isEnabled = true
        binding.passwordEditText.isEnabled = true
    }

    private fun disableUi() {
        binding.progressBar.visibility = View.VISIBLE
        binding.loginButton.isEnabled = false
        binding.emailEditText.isEnabled = false
        binding.passwordEditText.isEnabled = false
    }

    @SuppressLint("UnsafeOptInUsageError")
    @OptIn(InternalCoroutinesApi::class, ExperimentalTime::class)
    fun startMenuActivity() {
        val intent = Intent(this, AuthorizedActivity::class.java)
        startActivity(intent)
        finish()
    }

    companion object {
        @JvmStatic
        @BindingAdapter("app:showPassword")
        fun showPassword(view: AppCompatEditText, showPassword: Boolean) {
            if (showPassword)
                view.transformationMethod = null
            else
                view.transformationMethod = PasswordTransformationMethod()
        }

        @JvmStatic
        @BindingAdapter("app:showPasswordIcon")
        fun showPasswordIcon(view: AppCompatImageView, showPassword: Boolean) {
            if (showPassword)
                view.setImageResource(R.drawable.ic_baseline_visibility_on_24)
            else
                view.setImageResource(R.drawable.ic_baseline_visibility_off_24)
        }
    }
}