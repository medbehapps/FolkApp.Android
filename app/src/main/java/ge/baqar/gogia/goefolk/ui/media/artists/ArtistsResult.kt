package ge.baqar.gogia.goefolk.ui.media.artists

import ge.baqar.gogia.goefolk.model.Artist


open class ArtistsResult

data class ArtistsState(
    val isInProgress: Boolean,
    val artists: MutableList<Artist>,
    val error: String?
) : ArtistsResult() {

    companion object {
        val DEFAULT = ArtistsState(
            isInProgress = true,
            error = null,
            artists = mutableListOf()
        )
    }
}
