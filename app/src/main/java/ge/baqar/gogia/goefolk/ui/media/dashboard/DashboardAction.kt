package ge.baqar.gogia.goefolk.ui.media.dashboard
 
import ge.baqar.gogia.goefolk.model.HolidaySong
import ge.baqar.gogia.goefolk.model.Song


//Actions
open class DashboardAction
class DashboardDataRequested : DashboardAction()
data class DashboardDataLoaded(val daySong: Song, val dayChant: Song, val holidaySongs: MutableList<HolidaySong>) : DashboardAction()