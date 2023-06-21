package ge.baqar.gogia.goefolk.ui.ensembles

import ge.baqar.gogia.goefolk.model.Artist


open class EnsemblesResult

data class ArtistsState(
    val isInProgress: Boolean,
    val artists: MutableList<Artist>,
    val error: String?
) : EnsemblesResult() {

    companion object {
        val DEFAULT = ArtistsState(
            isInProgress = true,
            error = null,
            artists = mutableListOf()
        )
    }
}
