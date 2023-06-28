package ge.baqar.gogia.goefolk.ui.account.register

open class RegisterResult

data class RegisterState(
    val isInProgress: Boolean,
    val accountId: String?,
    val error: String?,
    val verified: Boolean = false
) : RegisterResult() {

    companion object {
        val DEFAULT = RegisterState(
            isInProgress = false,
            error = null,
            accountId = null
        )
    }
}