package ge.baqar.gogia.goefolk.ui.media.favourites

import ge.baqar.gogia.goefolk.storage.db.FolkApiDao
import ge.baqar.gogia.goefolk.arch.ReactiveViewModel
import ge.baqar.gogia.goefolk.http.service_implementations.SongServiceImpl
import ge.baqar.gogia.goefolk.model.Song
import ge.baqar.gogia.goefolk.model.SucceedResult
import ge.baqar.gogia.goefolk.storage.usecase.FileSaveController
import ge.baqar.gogia.goefolk.utility.FileExtensions
import ge.baqar.gogia.goefolk.utility.NetworkStatus
import kotlinx.coroutines.flow.Flow
import okhttp3.internal.wait

class FavouritesViewModel(
    private val folkApiDao: FolkApiDao,
    private val saveController: FileSaveController,
    private val songService: SongServiceImpl,
    private val networkStatus: NetworkStatus,
    private val fileExtensions: FileExtensions
) :
    ReactiveViewModel<FavouriteAction, FavouriteResultState, FavouriteState>(FavouriteState.DEFAULT) {
    override fun FavouriteAction.process(): Flow<() -> FavouriteResultState> {
        return when (this) {
            is FavouritesList -> update {
                if (networkStatus.isOnline()) {
                    songService.favourites().collect {
                        if (it is SucceedResult) {
                            it.value.forEach {
                                it.isFav = true
                            }
                            emit {
                                state.copy(isInProgress = false, error = null, favSongs = it.value)
                            }
                        }
                    }
                } else {
                    val favSongs = folkApiDao.songs().groupBy {
                        it.ensembleId
                    }.flatMap {
                        val ensemble = folkApiDao.ensembleById(it.key)
                        it.value.map { song ->
                            val fileSystemSong =
                                saveController.getFile(ensemble?.nameEng!!, song.nameEng)
                            Song(
                                song.referenceId,
                                song.name,
                                song.filePath,
                                song.songType,
                                song.ensembleId,
                                ensemble.name,
                                false,
                                data = fileExtensions.read(fileSystemSong?.data),
                                isFav = true
                            )
                        }
                    }.toMutableList()
                    emit {
                        state.copy(isInProgress = false, error = null, favSongs = favSongs)
                    }
                }
            }

            else -> update {

            }
        }
    }

}