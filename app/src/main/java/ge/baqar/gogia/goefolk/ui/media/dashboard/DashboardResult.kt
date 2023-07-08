package ge.baqar.gogia.goefolk.ui.media.dashboard

import ge.baqar.gogia.goefolk.model.HolidaySong
import ge.baqar.gogia.goefolk.model.Song


open class DashboardResult

data class DashboardState(
    val isInProgress: Boolean,
    val daySong: Song?,
    val dayChant: Song?,
    val holidaySongs: Array<HolidaySong>?,
    val error: String?
) : DashboardResult() {

    companion object {
        val DEFAULT = DashboardState(
            isInProgress = true,
            error = null,
            dayChant = null,
            daySong = null,
            holidaySongs = null
        )
    }
}

