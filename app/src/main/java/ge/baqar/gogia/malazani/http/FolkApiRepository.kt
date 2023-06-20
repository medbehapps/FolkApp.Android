package ge.baqar.gogia.malazani.http

import ge.baqar.gogia.malazani.model.Artist
import ge.baqar.gogia.malazani.model.ReactiveResult
import ge.baqar.gogia.malazani.model.SearchResult
import ge.baqar.gogia.malazani.model.SongsResponse
import ge.baqar.gogia.malazani.model.asError
import ge.baqar.gogia.malazani.model.asSuccess
import ge.baqar.gogia.malazani.utility.NetworkStatus
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOf
import java.io.InputStream

class FolkApiRepository(
    private var networkStatus: NetworkStatus,
    private var folkApiService: FolkApiService,
    private var searchService: SearchService
) {

    suspend fun ensembles(): Flow<ReactiveResult<String, MutableList<Artist>>> {
        return coroutineScope {
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
        }
    }

    suspend fun oldRecordings(): Flow<ReactiveResult<String, MutableList<Artist>>> {
        return coroutineScope {
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
        }
    }

    suspend fun songs(id: String): Flow<ReactiveResult<String, SongsResponse>> {
        return coroutineScope {
            if (networkStatus.isOnline()) {
                val songs = folkApiService.songs(id)
                val flow = callbackFlow<ReactiveResult<String, SongsResponse>> {
                    trySend(songs.asSuccess)
                    awaitClose { channel.close() }
                }
                return@coroutineScope flow
            } else {
                return@coroutineScope flowOf("network_is_off".asError)
            }
        }
    }

    suspend fun search(term: String): Flow<ReactiveResult<String, SearchResult>> {
        return coroutineScope {
            if (networkStatus.isOnline()) {
                val searchResult = searchService.search(term)
                val flow = callbackFlow<ReactiveResult<String, SearchResult>> {
                    trySend(searchResult.asSuccess)
                    awaitClose { channel.close() }
                }
                return@coroutineScope flow
            } else {
                return@coroutineScope flowOf("network_is_off".asError)
            }
        }
    }

    suspend fun downloadSong(id: String): ReactiveResult<String, InputStream> {
        val result = if (networkStatus.isOnline()) {
            val song = folkApiService.downloadSongFile(id).body()?.byteStream()
                ?: return "network_is_off".asError

            song.asSuccess
        } else {
            "network_is_off".asError
        }
        return result
    }

    suspend fun ensemble(ensembleId: String): Artist? {
        return if (networkStatus.isOnline()) {
            folkApiService.ensemble(ensembleId)
        } else {
            null
        }
    }
}