package ge.baqar.gogia.malazani.ui.songs

import ge.baqar.gogia.malazani.arch.ReactiveViewModel
import ge.baqar.gogia.malazani.http.FolkApiRepository
import ge.baqar.gogia.malazani.model.Artist
import ge.baqar.gogia.malazani.model.FailedResult
import ge.baqar.gogia.malazani.model.SongType
import ge.baqar.gogia.malazani.model.SucceedResult
import ge.baqar.gogia.malazani.storage.CharConverter
import ge.baqar.gogia.malazani.storage.db.FolkApiDao
import ge.baqar.gogia.malazani.storage.usecase.FileSaveController
import ge.baqar.gogia.malazani.utility.FileExtensions
import ge.baqar.gogia.malazani.utility.toModel
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.Flow

@InternalCoroutinesApi
class SongsViewModel(
    private val folkApiRepository: FolkApiRepository,
    private val folkApiDao: FolkApiDao,
    private val saveController: FileSaveController,
    private val fileExtensions: FileExtensions
) : ReactiveViewModel<SongsAction, SongsResult, ArtistState>(ArtistState.DEFAULT) {

    fun songs(
        artist: Artist
    ) = update {
        emit {
            ArtistState.IS_LOADING
        }

        folkApiRepository.songs(artist.id)
            .collect { value ->
                if (value is SucceedResult) {
                    val songs = folkApiDao.songsByEnsembleId(artist.id)

                    value.value.chants.forEach { song ->
                        song.nameEng = CharConverter.toEng(song.name)
                        song.isFav =
                            songs.firstOrNull { it.referenceId == song.id } != null
                    }
                    value.value.songs.forEach { song ->
                        song.nameEng = CharConverter.toEng(song.name)
                        song.isFav =
                            songs.firstOrNull { it.referenceId == song.id } != null
                    }
                    emit {
                        state.copy(
                            isInProgress = false,
                            songs = value.value.songs,
                            chants = value.value.chants
                        )
                    }
                }
                if (value is FailedResult) {
                    val cacheSongs = folkApiDao.songsByEnsembleId(artist.id)
                    val songs = cacheSongs
                        .filter { it.songType == SongType.Song.index }
                        .map {
                            val fileSystemSong =
                                saveController.getFile(artist.nameEng, it.nameEng)
                            it.toModel(artist.name, fileExtensions.read(fileSystemSong?.data))
                        }
                        .toMutableList()

                    val chants = cacheSongs
                        .filter { it.songType == SongType.Chant.index }
                        .map {
                            val fileSystemSong =
                                saveController.getFile(artist.nameEng, it.nameEng)
                            it.toModel(artist.name, fileExtensions.read(fileSystemSong?.data))
                        }
                        .toMutableList()

                    emit {
                        state.copy(
                            isInProgress = false,
                            songs = songs,
                            chants = chants
                        )
                    }
                    emit {
                        state.copy(isInProgress = false, error = value.value)
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

    suspend fun isSongFav(songId: String): Boolean {
        return folkApiDao.song(songId) != null
    }
}