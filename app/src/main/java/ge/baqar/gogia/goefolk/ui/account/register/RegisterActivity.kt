package ge.baqar.gogia.goefolk.ui.account.register

import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatImageView
import androidx.databinding.BindingAdapter
import androidx.lifecycle.lifecycleScope
import ge.baqar.gogia.goefolk.R
import ge.baqar.gogia.goefolk.databinding.ActivityRegisterBinding
import ge.baqar.gogia.goefolk.model.RegistrationModel
import ge.baqar.gogia.goefolk.model.VerificationModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.viewmodel.ext.android.viewModel
import reactivecircus.flowbinding.android.view.clicks
import timber.log.Timber

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private val viewModel: RegisterViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val registrationModel = RegistrationModel(
            null, null, null, null
        )
        val verificationModel = VerificationModel(
            null
        )
        binding.showVerification = false
        binding.viewPassword = false
        binding.registerModel = registrationModel
        binding.verificationModel = verificationModel
        initializeIntents(
            binding.registerButton.clicks()
                .map {
                    RegisterRequested(
                        binding.registerModel?.email,
                        binding.registerModel?.firstName,
                        binding.registerModel?.lastName,
                        binding.registerModel?.password,
                    )
                }
        )

        binding.verifyButton.setOnClickListener {
            initializeIntents(
                flowOf(
                    VerificationRequested(
                        binding.verificationModel?.code,
                        viewModel.state.accountId!!
                    )
                )
            )
        }
        binding.include.tabBackImageView.setOnClickListener {
            finish()
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

    private fun initializeIntents(inputs: Flow<RegisterActions>) {
        viewModel.intents(inputs)
            .onEach { output ->
                when (output) {
                    is RegisterState -> render(output)
                }
            }
            .launchIn(lifecycleScope)
    }

    private fun render(state: RegisterState) {
        if (state.isInProgress) {
            disableUi()
            return
        }

        enableUi()
        if (state.error != null) {
            Toast.makeText(this, state.error, Toast.LENGTH_LONG).show()
            Timber.i(state.error)
            return
        }

        if (state.accountId != null) {
            binding.showVerification = true
            return
        }

        if (state.verified) {
            finish()
        }
    }

    private fun enableUi() {
        binding.progressBar.visibility = View.GONE
        binding.disableUi = false
    }

    private fun disableUi() {
        binding.progressBar.visibility = View.VISIBLE
        binding.disableUi = true
    }

    companion object {

        @JvmStatic
        @BindingAdapter("app:visible")
        fun visible(view: View, visible: Boolean) {
            if (visible) {
                view.visibility = View.VISIBLE
            } else {
                view.visibility = View.GONE
            }
        }

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
                view.setImageResource(R.drawable.baseline_visibility_on_24)
            else
                view.setImageResource(R.drawable.baseline_visibility_off_24)
        }
    }
}