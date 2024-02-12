package ge.baqar.gogia.gefolk.http.service_implementations

import ge.baqar.gogia.gefolk.http.response.BaseError
import ge.baqar.gogia.gefolk.http.services.ArtistsService
import ge.baqar.gogia.gefolk.model.Artist
import ge.baqar.gogia.gefolk.model.ReactiveResult
import ge.baqar.gogia.gefolk.model.asError
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOf

class ArtistsServiceImpl(
    private var artistsService: ArtistsService
) : ServiceBase() {


    suspend fun ensembles(
    ): Flow<ReactiveResult<BaseError, MutableList<Artist>>> {
        return coroutineScope {
            try {
                val result = artistsService.ensembles()
                val flow = callbackFlow {
                    trySend(mapToReactiveResult(result))
                    awaitClose { channel.close() }
                }
                return@coroutineScope flow
            } catch (ex: Exception) {
                ex.printStackTrace()
                return@coroutineScope flowOf(BaseError("network_is_off").asError)
            }
        }
    }

    suspend fun oldRecordings(): Flow<ReactiveResult<BaseError, MutableList<Artist>>> {
        return coroutineScope {
            val result = artistsService.oldRecordings()
            val flow = callbackFlow {
                trySend(mapToReactiveResult(result))
                awaitClose { channel.close() }
            }
            return@coroutineScope flow
        }
    }

    suspend fun ensemble(ensembleId: String): Artist? {
        return artistsService.ensemble(ensembleId).body
    }
}