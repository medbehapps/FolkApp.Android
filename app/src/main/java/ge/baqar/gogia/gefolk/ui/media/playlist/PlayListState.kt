package ge.baqar.gogia.gefolk.ui.media.playlist

import ge.baqar.gogia.gefolk.http.response.PlayList

open class PlayListState(
    open val isInProgress: Boolean,
    open val result: MutableList<PlayList>,
    open val error: String?
)

data class PlayListResultState(
    override val isInProgress: Boolean,
    override val result: MutableList<PlayList>,
    override val error: String?
) : PlayListState(isInProgress, result, error) {
    companion object {
        val DEFAULT = PlayListResultState(
            isInProgress = false,
            error = null,
            result = mutableListOf()
        )
    }
}

class ReloadState : PlayListState(
    isInProgress = false,
    error = null,
    result = mutableListOf()
)