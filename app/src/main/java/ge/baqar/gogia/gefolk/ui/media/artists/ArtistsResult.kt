package ge.baqar.gogia.gefolk.ui.media.artists

import ge.baqar.gogia.gefolk.model.Artist


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
