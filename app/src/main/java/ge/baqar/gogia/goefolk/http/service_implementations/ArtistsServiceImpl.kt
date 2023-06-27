package ge.baqar.gogia.goefolk.http.service_implementations

import ge.baqar.gogia.goefolk.http.services.ArtistsService
import ge.baqar.gogia.goefolk.model.Artist
import ge.baqar.gogia.goefolk.model.ReactiveResult
import ge.baqar.gogia.goefolk.model.asError
import ge.baqar.gogia.goefolk.model.asSuccess
import ge.baqar.gogia.goefolk.utility.NetworkStatus
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOf

class ArtistsServiceImpl(
    private var networkStatus: NetworkStatus,
    private var folkApiService: ArtistsService
) {

    suspend fun ensembles(): Flow<ReactiveResult<String, MutableList<Artist>>> {
        return coroutineScope {
            try {
                if (networkStatus.isOnline()) {
                    val ensembles = folkApiService.ensembles()
                    val flow = callbackFlow<ReactiveResult<String, MutableList<Artist>>> {
                        trySend(ensembles.asSuccess)
                        awaitClose { channel.close() }
                    }
                    return@coroutineScope flow
                } else {
                    return@coroutineScope flowOf("network_is_off".asError)
                }
            } catch (ex: Exception) {
                return@coroutineScope flowOf("network_is_off".asError)
            }
        }
    }

    suspend fun oldRecordings(): Flow<ReactiveResult<String, MutableList<Artist>>> {
        return coroutineScope {
            try {
                if (networkStatus.isOnline()) {
                    val ensembles = folkApiService.oldRecordings()
                    val flow = callbackFlow<ReactiveResult<String, MutableList<Artist>>> {
                        trySend(ensembles.asSuccess)
                        awaitClose { channel.close() }
                    }
                    return@coroutineScope flow
                } else {
                    return@coroutineScope flowOf("network_is_off".asError)
                }
            } catch (ex: Exception) {
                return@coroutineScope flowOf("network_is_off".asError)
            }
        }
    }

    suspend fun ensemble(ensembleId: String): Artist? {
        return if (networkStatus.isOnline()) {
            folkApiService.ensemble(ensembleId)
        } else {
            null
        }
    }
}