package ge.baqar.gogia.goefolk.ui.account.login

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import ge.baqar.gogia.goefolk.databinding.FragmentAccountLoginBinding
import ge.baqar.gogia.goefolk.ui.MenuActivity
import ge.baqar.gogia.goefolk.ui.account.AccountActivity
import ge.baqar.gogia.goefolk.utility.DeviceId
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import org.koin.android.ext.android.inject
import reactivecircus.flowbinding.android.view.clicks
import timber.log.Timber
import kotlin.time.ExperimentalTime

@OptIn(InternalCoroutinesApi::class)
class LoginFragment : Fragment() {
    private var binding: FragmentAccountLoginBinding? = null
    private val viewModel: LoginViewModel by inject()
    private val deviceId: DeviceId by inject()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAccountLoginBinding.inflate(inflater, container, false)
        initializeIntents(
            binding?.loginButton?.clicks()
                ?.map {
                    LoginRequested(
                        binding?.emailEditText?.text?.toString()!!,
                        binding?.passwordEditText?.text?.toString()!!,
                        deviceId.get()!!
                    )
                }!!
        )
        return binding?.root!!
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

    @OptIn(ExperimentalTime::class)
    private fun render(output: LoginState) {
        if (output.isInProgress) {
            disableUi()
            return
        }

        enableUi()
        if (output.error != null) {
            val errorId = resources.getIdentifier(output.error, "string", context?.packageName)
            val error = getString(errorId)
            Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
            Timber.i(error)
            return
        }

        output.token?.let {
            viewModel.storeToken(it)
            (activity as? AccountActivity)?.startMenuActivity()
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
}