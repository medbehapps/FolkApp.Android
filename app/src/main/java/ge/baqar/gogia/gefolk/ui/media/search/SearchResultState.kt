package ge.baqar.gogia.gefolk.ui.media.search

import ge.baqar.gogia.gefolk.model.SearchResult

open class SearchResultState

data class SearchState(
    val isInProgress: Boolean,
    val result: SearchResult?,
    val error: String?
) : SearchResultState() {

    companion object {
        val DEFAULT = SearchState(
            isInProgress = false,
            error = null,
            result = null
        )

        val LOADING = SearchState(
            isInProgress = true,
            error = null,
            result = null
        )
    }
}
