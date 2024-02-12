package ge.baqar.gogia.goefolk.ui.media.playlist

import ge.baqar.gogia.goefolk.ui.ReactiveViewModel
import ge.baqar.gogia.goefolk.http.service_implementations.PlayListServiceImpl
import ge.baqar.gogia.goefolk.model.FailedResult
import ge.baqar.gogia.goefolk.model.SucceedResult
import kotlinx.coroutines.flow.Flow

class PlayListViewModel(
    private val playListService: PlayListServiceImpl
) : ReactiveViewModel<PlayListAction, PlayListState, PlayListResultState>(PlayListResultState.DEFAULT) {
    override fun PlayListAction.process(): Flow<() -> PlayListState> {
        return when (this) {
            is LoadPlayLists, is ReloadAction -> loadPlayLists()

            is RemoveSongsFromPlayList -> update {
                emit {
                    state.copy(isInProgress = true)
                }
                playListService.addOrRemoveSong(
                    playlistId,
                    songs.map { it.id }.toMutableList(),
                    action
                ).collect {
                    if (it is SucceedResult) {
                        emit { ReloadState() }
                        return@collect
                    }

                    if (it is FailedResult) {
                        emit {
                            state.copy(isInProgress = false, error = it.value.message)
                        }
                        return@collect
                    }
                }
            }

            is CreateNewPlaylist -> update {
                emit {
                    state.copy(isInProgress = true)
                }
                playListService.createNew(name, songs.map { it.id }.toMutableList()).collect {
                    if (it is SucceedResult) {
                        emit { ReloadState() }
                        return@collect
                    }

                    if (it is FailedResult) {
                        emit {
                            state.copy(isInProgress = false, error = it.value.message)
                        }
                        return@collect
                    }
                }
            }

            is DeletePlayListsAction -> update {
                emit {
                    state.copy(isInProgress = true)
                }

                playListsId
                    .map { playListService.delete(it) }
                    .forEach {
                        it.collect {
                            if (it is SucceedResult) {
                                emit {
                                    ReloadState()
                                }
                            }

                            if (it is FailedResult) {
                                emit {
                                    state.copy(isInProgress = false, error = it.value.message)
                                }
                                return@collect
                            }
                        }
                    }
            }

            else -> update {}
        }
    }

    private fun loadPlayLists() = update {
        emit {
            state.copy(isInProgress = true)
        }
        playListService.list().collect {
            if (it is SucceedResult) {
                emit {
                    state.copy(isInProgress = false, error = null, result = it.value)
                }
                return@collect
            }

            if (it is FailedResult) {
                emit {
                    state.copy(isInProgress = false, error = it.value.message)
                }
                return@collect
            }
        }
    }
}