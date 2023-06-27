package ge.baqar.gogia.goefolk.http.service_implementations

import ge.baqar.gogia.goefolk.http.request.LoginRequest
import ge.baqar.gogia.goefolk.http.response.LoginErrorResponse
import ge.baqar.gogia.goefolk.http.services.AccountService
import ge.baqar.gogia.goefolk.model.ReactiveResult
import ge.baqar.gogia.goefolk.model.asError
import ge.baqar.gogia.goefolk.model.asSuccess
import ge.baqar.gogia.goefolk.utility.NetworkStatus
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOf

class AccountServiceImpl(
    private var networkStatus: NetworkStatus,
    private var accountService: AccountService
) {

    suspend fun login(
        email: String,
        password: String,
        deviceId: String
    ): Flow<ReactiveResult<LoginErrorResponse, String>> {
        return coroutineScope {
            try {
                if (networkStatus.isOnline()) {
                    val loginResult = accountService.login(LoginRequest(email, password, deviceId, 1))
                    val flow = callbackFlow<ReactiveResult<LoginErrorResponse, String>> {
                        trySend(loginResult.body()?.asSuccess!!)
                        awaitClose { channel.close() }
                    }
                    return@coroutineScope flow
                } else {
                    return@coroutineScope flowOf(LoginErrorResponse("network_is_off").asError)
                }
            } catch (ex: Exception) {
                ex.printStackTrace()
                return@coroutineScope flowOf(LoginErrorResponse("network_is_off").asError)
            }
        }
    }
}