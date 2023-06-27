package ge.baqar.gogia.goefolk.http.service_implementations

import ge.baqar.gogia.goefolk.http.request.LoginRequest
import ge.baqar.gogia.goefolk.http.response.BaseError
import ge.baqar.gogia.goefolk.http.services.AccountService
import ge.baqar.gogia.goefolk.model.ReactiveResult
import ge.baqar.gogia.goefolk.model.asError
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOf
import retrofit2.HttpException

class AccountServiceImpl(
    private var accountService: AccountService
) : ServiceBase() {

    suspend fun login(
        email: String,
        password: String,
        deviceId: String
    ): Flow<ReactiveResult<BaseError, String>> {
        return coroutineScope {
            try {
                val loginResult =
                    accountService.login(LoginRequest(email, password, deviceId, 1))
                val flow = callbackFlow {
                    trySend(mapToReactiveResult(loginResult))
                    awaitClose { channel.close() }
                }
                return@coroutineScope flow
            } catch (ex: HttpException) {
                val response = escapeServerError(ex)
                return@coroutineScope flowOf(response.error?.asError!!)
            }
        }
    }
}