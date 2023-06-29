package ge.baqar.gogia.goefolk.ui.media.ensembles

import ge.baqar.gogia.goefolk.model.Artist


//Actions
open class EnsemblesAction
data class EnsemblesLoaded(val artists: MutableList<Artist>) : ge.baqar.gogia.goefolk.ui.media.ensembles.EnsemblesAction()
class EnsemblesRequested : ge.baqar.gogia.goefolk.ui.media.ensembles.EnsemblesAction()
class OldRecordingsRequested : ge.baqar.gogia.goefolk.ui.media.ensembles.EnsemblesAction()