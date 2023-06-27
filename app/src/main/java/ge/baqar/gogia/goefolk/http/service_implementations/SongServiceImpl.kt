package ge.baqar.gogia.goefolk.http.service_implementations

import ge.baqar.gogia.goefolk.http.services.SongService
import ge.baqar.gogia.goefolk.model.ReactiveResult
import ge.baqar.gogia.goefolk.model.SongsResponse
import ge.baqar.gogia.goefolk.model.asError
import ge.baqar.gogia.goefolk.model.asSuccess
import ge.baqar.gogia.goefolk.utility.NetworkStatus
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOf


class SongServiceImpl(
    private var networkStatus: NetworkStatus,
    private var songService: SongService
) {

    suspend fun songs(id: String): Flow<ReactiveResult<String, SongsResponse>> {
        return coroutineScope {
            if (networkStatus.isOnline()) {
                val songs = songService.songs(id)
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

    suspend fun downloadSong(id: String): ReactiveResult<String, ByteArray> {
        val result = if (networkStatus.isOnline()) {
            val song = songService.downloadSongFile(id).body()?.bytes()
                ?: return "network_is_off".asError

            song.asSuccess
        } else {
            "network_is_off".asError
        }
        return result
    }
}