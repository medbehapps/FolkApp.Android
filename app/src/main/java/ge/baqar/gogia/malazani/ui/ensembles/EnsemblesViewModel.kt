package ge.baqar.gogia.malazani.ui.ensembles

import ge.baqar.gogia.malazani.arch.ReactiveViewModel
import ge.baqar.gogia.malazani.http.FolkApiRepository
import ge.baqar.gogia.malazani.model.Artist
import ge.baqar.gogia.malazani.model.FailedResult
import ge.baqar.gogia.malazani.model.ReactiveResult
import ge.baqar.gogia.malazani.model.SucceedResult
import ge.baqar.gogia.malazani.storage.CharConverter
import ge.baqar.gogia.malazani.storage.db.FolkApiDao
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector

@InternalCoroutinesApi
class EnsemblesViewModel(
    private val alazaniRepository: FolkApiRepository,
    private val folkApiDao: FolkApiDao
) : ReactiveViewModel<EnsemblesAction, EnsemblesResult, ArtistsState>(ArtistsState.DEFAULT) {

    fun ensembles() = update {
        emit {
            state.copy(isInProgress = true)
        }
        alazaniRepository.ensembles().collect(object: FlowCollector<ReactiveResult<String, MutableList<Artist>>>{
            override suspend fun emit(value: ReactiveResult<String, MutableList<Artist>>) {
                if (value is SucceedResult) {
                    emit {
                        value.value.sortBy { it.name }
                        value.value.forEach {
                            it.nameEng = CharConverter.toEng(it.name)
                        }
                        state.copy(isInProgress = false, artists = value.value)
                    }
                }
                if (value is FailedResult) {
                    val cachedEnsembles = folkApiDao.ensembles()
                    if (cachedEnsembles.isNotEmpty()) {
                        val mapped = cachedEnsembles.map {
                            Artist(
                                it.referenceId,
                                it.name,
                                it.nameEng,
                                it.artistType
                            )
                        }.toMutableList()
                        emit {
                            state.copy(isInProgress = false, artists = mapped)
                        }
                    }
                    emit { state.copy(isInProgress = false, error = value.value) }
                }
            }
        })
    }

    fun oldRecordings() = update {
        emit {
            state.copy(isInProgress = true)
        }
        alazaniRepository.oldRecordings().collect(object: FlowCollector<ReactiveResult<String, MutableList<Artist>>>{
            override suspend fun emit(result: ReactiveResult<String, MutableList<Artist>>) {
                if (result is SucceedResult) {
                    emit {
                        state.copy(isInProgress = false, artists = result.value)
                    }
                }
                if (result is FailedResult) {
                    emit { state.copy(isInProgress = false, error = result.value) }
                }
            }

        })
    }

    override fun EnsemblesAction.process(): Flow<() -> EnsemblesResult> {
        return when (this) {
            is EnsemblesLoaded -> update {
                emit {
                    state.copy(
                        isInProgress = false,
                        artists = state.artists,
                        error = null
                    )
                }
            }
            is EnsemblesRequested -> {
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