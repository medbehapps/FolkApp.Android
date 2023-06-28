package ge.baqar.gogia.goefolk.http.service_implementations

import ge.baqar.gogia.goefolk.http.response.BaseError
import ge.baqar.gogia.goefolk.http.services.SongService
import ge.baqar.gogia.goefolk.model.ReactiveResult
import ge.baqar.gogia.goefolk.model.Song
import ge.baqar.gogia.goefolk.model.SongsResponse
import ge.baqar.gogia.goefolk.model.asError
import ge.baqar.gogia.goefolk.model.asSuccess
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOf
import retrofit2.HttpException


class SongServiceImpl(
    private var songService: SongService
) : ServiceBase() {

    suspend fun songs(id: String): Flow<ReactiveResult<BaseError, SongsResponse>> {
        return coroutineScope {
            try {
                val result = songService.songs(id)
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

    suspend fun downloadSong(id: String): ReactiveResult<BaseError, ByteArray> {
        val song = songService.downloadSongFile(id).body()?.bytes()
        return song?.asSuccess!!
    }

    suspend fun markAsFavourite(id: String): Flow<ReactiveResult<BaseError, Boolean>> {
        return coroutineScope {
            try {
                val result = songService.markSongAsFavourite(id)
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

    suspend fun favourites(): Flow<ReactiveResult<BaseError, MutableList<Song>>> {
        return coroutineScope {
            try {
                val result = songService.favourites()
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