package ge.baqar.gogia.malazani.ui.search

import androidx.lifecycle.viewModelScope
import ge.baqar.gogia.malazani.arch.ReactiveViewModel
import ge.baqar.gogia.malazani.http.FolkApiRepository
import ge.baqar.gogia.malazani.model.Artist
import ge.baqar.gogia.malazani.model.FailedResult
import ge.baqar.gogia.malazani.model.ReactiveResult
import ge.baqar.gogia.malazani.model.SearchResult
import ge.baqar.gogia.malazani.model.SucceedResult
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.launch

@InternalCoroutinesApi
class SearchViewModel(
    private val folkApiRepository: FolkApiRepository
) : ReactiveViewModel<SearchAction, SearchResultState, SearchState>(SearchState.DEFAULT) {
    override fun SearchAction.process(): Flow<() -> SearchResultState> {
        return when (this) {
            is DoSearch -> {
                doSearch(term)
            }
            is ClearSearchResult -> update {
                emit {
                    state.copy(isInProgress = false, error = null, result = null)
                }
            }
            else -> update {

            }
        }
    }

    private fun doSearch(term: String) = update {
        emit {
            SearchState.LOADING
        }

        folkApiRepository.search(term).collect(object :FlowCollector<ReactiveResult<String, SearchResult>>{
            override suspend fun emit(result: ReactiveResult<String, SearchResult>) {
                if (result is SucceedResult) {
                    emit {
                        state.copy(isInProgress = false, result = result.value)
                    }
                }
                if (result is FailedResult) {
                    emit { state.copy(isInProgress = false, error = result.value) }
                }
            }

        })
    }

    fun ensembleById(ensembleId: String, completion: (Artist?) -> Unit){
        viewModelScope.launch {
            val ensemble = folkApiRepository.ensemble(ensembleId)
            completion(ensemble)
        }
    }
}