package ge.baqar.gogia.goefolk.ui.media.favourites

import ge.baqar.gogia.goefolk.http.service_implementations.SongServiceImpl
import ge.baqar.gogia.goefolk.model.SucceedResult
import ge.baqar.gogia.goefolk.ui.ReactiveViewModel
import ge.baqar.gogia.goefolk.utility.NetworkStatus
import kotlinx.coroutines.flow.Flow

class FavouritesViewModel(
    private val songService: SongServiceImpl,
    private val networkStatus: NetworkStatus
) :
    ReactiveViewModel<FavouriteAction, FavouriteResultState, FavouriteState>(FavouriteState.DEFAULT) {
    override fun FavouriteAction.process(): Flow<() -> FavouriteResultState> {
        return when (this) {
            is FavouritesList -> update {
                songService.favourites().collect {
                    if (it is SucceedResult) {
                        emit {
                            state.copy(isInProgress = false, error = null, favSongs = it.value)
                        }
                    }
                }
            }

            else -> update {

            }
        }
    }

}