package ge.baqar.gogia.goefolk.ui.account.register

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.BindingAdapter
import androidx.lifecycle.lifecycleScope
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
    private var binding: ActivityRegisterBinding? = null
    private val viewModel: RegisterViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        val registrationModel = RegistrationModel(
            null, null, null, null
        )
        val verificationModel = VerificationModel(
            null
        )
        binding?.showVerification = false
        binding?.registerModel = registrationModel
        binding?.verificationModel = verificationModel
        initializeIntents(
            binding?.registerButton?.clicks()
                ?.map {
                    RegisterRequested(
                        binding?.registerModel?.email,
                        binding?.registerModel?.firstName,
                        binding?.registerModel?.lastName,
                        binding?.registerModel?.password,
                    )
                }!!
        )

        binding?.verifyButton?.setOnClickListener {
            initializeIntents(
                flowOf(
                    VerificationRequested(
                        binding?.verificationModel?.code,
                        viewModel.state.accountId!!
                    )
                )
            )
        }
        binding?.include?.tabBackImageView?.setOnClickListener {
            finish()
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
            return
        }

        if (state.error != null) {
            Toast.makeText(this, state.error, Toast.LENGTH_LONG).show()
            Timber.i(state.error)
            return
        }

        if (state.accountId != null) {
            binding?.showVerification = true
            return
        }

        if (state.verified) {
            finish()
        }
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
    }
}