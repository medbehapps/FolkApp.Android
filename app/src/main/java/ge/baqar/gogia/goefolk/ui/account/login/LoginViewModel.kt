package ge.baqar.gogia.goefolk.ui.account.login

import ge.baqar.gogia.goefolk.arch.ReactiveViewModel
import ge.baqar.gogia.goefolk.http.service_implementations.AccountServiceImpl
import ge.baqar.gogia.goefolk.model.FailedResult
import ge.baqar.gogia.goefolk.model.SucceedResult
import ge.baqar.gogia.goefolk.storage.FolkAppPreferences
import kotlinx.coroutines.flow.Flow

class LoginViewModel(
    private val accountService: AccountServiceImpl,
    private val preferences: FolkAppPreferences
) :
    ReactiveViewModel<LoginActions, LoginResult, LoginState>(
        LoginState.DEFAULT
    ) {
    override fun LoginActions.process(): Flow<() -> LoginResult> {
        return when (this) {
            is LoginRequested -> {
                login(email, password, deviceId)
            }
            is LoginByTokenRequested -> {
                loginByToken(token, deviceId)
            }
            else -> update {

            }
        }
    }

    private fun login(email: String, password: String, deviceId: String) = update {
        emit {
            state.copy(isInProgress = true)
        }
        accountService.login(email, password, deviceId).collect { value ->
            if (value is SucceedResult) {
                emit {
                    state.copy(isInProgress = false, token = value.value, error = null)
                }
            }
            if (value is FailedResult) {
                emit { state.copy(isInProgress = false, error = value.value.message) }
            }
        }
    }

    private fun loginByToken(token: String, deviceId: String) = update {
        emit {
            state.copy(isInProgress = true)
        }
        accountService.loginByToken(token, deviceId).collect { value ->
            if (value is SucceedResult) {
                emit {
                    state.copy(isInProgress = false, token = value.value, error = null)
                }
            }
            if (value is FailedResult) {
                emit { state.copy(isInProgress = false, error = value.value.message) }
            }
        }
    }
    fun storeToken(token: String?) {
        preferences.setToken(token)
    }
}