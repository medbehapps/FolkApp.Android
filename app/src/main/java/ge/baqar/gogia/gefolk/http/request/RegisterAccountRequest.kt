package ge.baqar.gogia.gefolk.http.request

data class RegisterAccountRequest(
    val email: String,
    val firstName: String,
    val lastName: String,
    val password: String
)