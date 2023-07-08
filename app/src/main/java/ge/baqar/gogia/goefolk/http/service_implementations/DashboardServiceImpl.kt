package ge.baqar.gogia.goefolk.http.service_implementations

import ge.baqar.gogia.goefolk.http.response.BaseError
import ge.baqar.gogia.goefolk.http.response.DashboardDataResponse
import ge.baqar.gogia.goefolk.http.services.DashboardService
import ge.baqar.gogia.goefolk.model.ReactiveResult
import ge.baqar.gogia.goefolk.model.asError
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOf
import retrofit2.HttpException

class DashboardServiceImpl(
    private var dashboardService: DashboardService
) : ServiceBase() {

    suspend fun dashboardData(
    ): Flow<ReactiveResult<BaseError, DashboardDataResponse>> {
        return coroutineScope {
            try {
                val result = dashboardService.dashboardData()
                val flow = callbackFlow {
                    trySend(mapToReactiveResult(result))
                    awaitClose { channel.close() }
                }
                return@coroutineScope flow
            } catch (ex: HttpException) {
                val baseError = escapeServerError(ex)
                return@coroutineScope flowOf(baseError.error?.asError!!)
            }
        }
    }

}