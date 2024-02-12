package ge.baqar.gogia.goefolk.ui.media.artists

import ge.baqar.gogia.goefolk.http.service_implementations.ArtistsServiceImpl
import ge.baqar.gogia.goefolk.model.FailedResult
import ge.baqar.gogia.goefolk.model.SucceedResult
import ge.baqar.gogia.goefolk.ui.ReactiveViewModel
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.Flow

@InternalCoroutinesApi
class ArtistsViewModel(
    private val artistsService: ArtistsServiceImpl
) : ReactiveViewModel<ArtistsAction, ArtistsResult, ArtistsState>(ArtistsState.DEFAULT) {

    fun ensembles() = update {
        emit {
            state.copy(isInProgress = true)
        }
        artistsService.ensembles().collect { value ->
            if (value is SucceedResult) {
                emit {
                    value.value.sortBy { it.name }
                    state.copy(isInProgress = false, artists = value.value)
                }
            }
            if (value is FailedResult) {
                emit { state.copy(isInProgress = false, error = value.value.message) }
            }
        }
    }

    fun oldRecordings() = update {
        emit {
            state.copy(isInProgress = true)
        }
        artistsService.oldRecordings().collect { result ->
            if (result is SucceedResult) {
                emit {
                    state.copy(isInProgress = false, artists = result.value)
                }
            }
            if (result is FailedResult) {
                emit { state.copy(isInProgress = false, error = result.value.message) }
            }
        }
    }

    override fun ArtistsAction.process(): Flow<() -> ArtistsResult> {
        return when (this) {
            is ArtistsRequested -> {
                ensembles()
            }
            is OldRecordingsRequested -> {
                oldRecordings()
            }
            else -> update {

            }
        }
    }
}