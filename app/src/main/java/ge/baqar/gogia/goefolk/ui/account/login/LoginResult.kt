package ge.baqar.gogia.goefolk.ui.account.login

open class LoginResult

data class LoginState(
    val isInProgress: Boolean,
    val token: String?,
    val error: String?
) : LoginResult() {

    companion object {
        val DEFAULT = LoginState(
            isInProgress = false,
            error = null,
            token = null
        )
    }
}