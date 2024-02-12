package ge.baqar.gogia.gefolk.ui.account.register

open class RegisterActions
class RegisterRequested(
    val email: String?,
    val firstName: String?,
    val lastName: String?,
    val password: String?
) : RegisterActions()

class VerificationRequested(
    val code: String?,
    val accountId: String
) : RegisterActions()