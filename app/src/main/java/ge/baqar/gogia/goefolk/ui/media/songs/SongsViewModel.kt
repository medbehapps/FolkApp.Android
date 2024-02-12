package ge.baqar.gogia.goefolk.ui.media.songs

import ge.baqar.gogia.goefolk.http.response.BaseError
import ge.baqar.gogia.goefolk.http.service_implementations.SongServiceImpl
import ge.baqar.gogia.goefolk.model.Artist
import ge.baqar.gogia.goefolk.model.FailedResult
import ge.baqar.gogia.goefolk.model.ReactiveResult
import ge.baqar.gogia.goefolk.model.Song
import ge.baqar.gogia.goefolk.model.SucceedResult
import ge.baqar.gogia.goefolk.ui.ReactiveViewModel
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.Flow

@InternalCoroutinesApi
class SongsViewModel(
    private val songsService: SongServiceImpl
) : ReactiveViewModel<SongsAction, SongsResult, ArtistState>(ArtistState.DEFAULT) {

    fun songs(
        artist: Artist
    ) = update {
        emit {
            ArtistState.IS_LOADING
        }

        songsService.songs(artist.id)
            .collect { value ->
                if (value is SucceedResult) {

                    emit {
                        state.copy(
                            isInProgress = false,
                            songs = value.value.songs,
                            chants = value.value.chants
                        )
                    }
                }
                if (value is FailedResult) {
                    emit {
                        state.copy(isInProgress = false, error = value.value.message)
                    }
                }
            }
    }

    override fun SongsAction.process(): Flow<() -> SongsResult> {
        return when (this) {
            is SongsRequested -> {
                songs(artist)
            }

            else -> update {

            }
        }
    }

    suspend fun log(songId: String, logType: Int) {
        songsService.log(songId, logType).collect {

        }
    }

    suspend fun fetchSong(artistId: String, songId: String, callback: (Song) -> Unit) {
        songsService.songs(artistId).collect { value ->
            if (value is SucceedResult) {

                val song = value.value.chants.firstOrNull { song ->
                    song.id == songId
                } ?: value.value.songs.firstOrNull { song ->
                    song.id == songId
                }

                callback.invoke(song!!)
            }
        }
    }

    suspend fun markAsFavourite(songId: String): Flow<ReactiveResult<BaseError, Boolean>> {
        return songsService.markAsFavourite(songId)
    }
}