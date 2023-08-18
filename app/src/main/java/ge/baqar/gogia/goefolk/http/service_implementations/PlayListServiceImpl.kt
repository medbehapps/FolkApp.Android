package ge.baqar.gogia.goefolk.http.service_implementations

import ge.baqar.gogia.goefolk.http.request.AddOrRemoveSongToPlayListRequest
import ge.baqar.gogia.goefolk.http.request.CreatePlayListRequest
import ge.baqar.gogia.goefolk.http.response.BaseError
import ge.baqar.gogia.goefolk.http.response.PlayList
import ge.baqar.gogia.goefolk.http.services.PlayListService
import ge.baqar.gogia.goefolk.model.ReactiveResult
import ge.baqar.gogia.goefolk.model.Song
import ge.baqar.gogia.goefolk.model.asError
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOf
import retrofit2.HttpException

class PlayListServiceImpl(
    private var playListService: PlayListService
) : ServiceBase() {

    suspend fun list(): Flow<ReactiveResult<BaseError, MutableList<PlayList>>> {
        return coroutineScope {
            try {
                val result = playListService.list()
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

    suspend fun createNew(name: String, songs: MutableList<String>): Flow<ReactiveResult<BaseError, Boolean>> {
        return coroutineScope {
            try {
                val result = playListService.createNew(CreatePlayListRequest(name, songs))
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

    suspend fun addOrRemoveSong(playlistId: String, songs: MutableList<String>, action: Int): Flow<ReactiveResult<BaseError, Boolean>> {
        return coroutineScope {
            try {
                val result = playListService.addOrRemoveSong(playlistId, AddOrRemoveSongToPlayListRequest(songs, action))
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

    suspend fun delete(id: String): Flow<ReactiveResult<BaseError, Boolean>> {
        return coroutineScope {
            try {
                val result = playListService.delete(id)
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