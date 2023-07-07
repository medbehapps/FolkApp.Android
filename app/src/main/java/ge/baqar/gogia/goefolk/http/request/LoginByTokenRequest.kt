package ge.baqar.gogia.goefolk.http.request

data class LoginByTokenRequest(
    val token: String,
    val deviceId: String,
    val platformOs: Long
)

