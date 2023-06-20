package ge.baqar.gogia.malazani.ui.ensembles

import ge.baqar.gogia.malazani.model.Artist


//Actions
open class EnsemblesAction
data class EnsemblesLoaded(val artists: MutableList<Artist>) : EnsemblesAction()
class EnsemblesRequested : EnsemblesAction()
class OldRecordingsRequested : EnsemblesAction()