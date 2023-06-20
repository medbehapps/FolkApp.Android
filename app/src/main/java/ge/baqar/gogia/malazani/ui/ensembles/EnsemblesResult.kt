package ge.baqar.gogia.malazani.ui.ensembles

import ge.baqar.gogia.malazani.model.Artist


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
