package ge.baqar.gogia.goefolk.ui.media.search

import androidx.lifecycle.viewModelScope
import ge.baqar.gogia.goefolk.ui.ReactiveViewModel
import ge.baqar.gogia.goefolk.http.service_implementations.ArtistsServiceImpl
import ge.baqar.gogia.goefolk.http.service_implementations.SearchServiceImpl
import ge.baqar.gogia.goefolk.model.Artist
import ge.baqar.gogia.goefolk.model.FailedResult
import ge.baqar.gogia.goefolk.model.SucceedResult
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

@InternalCoroutinesApi
class SearchViewModel(
    private val searchService: SearchServiceImpl,
    private val artistsService: ArtistsServiceImpl
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

        searchService.search(term).collect { result ->
            if (result is SucceedResult) {
                emit {
                    state.copy(isInProgress = false, result = result.value)
                }
            }
            if (result is FailedResult) {
                emit { state.copy(isInProgress = false, error = result.value.message) }
            }
        }
    }

    fun ensembleById(ensembleId: String, completion: (Artist?) -> Unit) {
        viewModelScope.launch {
            val ensemble = artistsService.ensemble(ensembleId)
            completion(ensemble)
        }
    }
}