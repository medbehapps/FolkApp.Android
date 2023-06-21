package ge.baqar.gogia.goefolk.ui.ensembles

import ge.baqar.gogia.goefolk.model.Artist


//Actions
open class EnsemblesAction
data class EnsemblesLoaded(val artists: MutableList<Artist>) : EnsemblesAction()
class EnsemblesRequested : EnsemblesAction()
class OldRecordingsRequested : EnsemblesAction()