package ge.baqar.gogia.gefolk.http.request

data class LoginRequest(
    val email: String,
    val password: String,
    val deviceId: String,
    val platformOs: Long
)
