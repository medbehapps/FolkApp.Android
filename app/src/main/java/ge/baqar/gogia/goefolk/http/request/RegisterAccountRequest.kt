package ge.baqar.gogia.goefolk.http.request

data class RegisterAccountRequest(
    val firstName: String,
    val lastName: String,
    val email: String,
    val password: String
)