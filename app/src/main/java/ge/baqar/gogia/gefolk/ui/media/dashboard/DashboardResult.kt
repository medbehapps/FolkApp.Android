package ge.baqar.gogia.gefolk.ui.media.dashboard

import ge.baqar.gogia.gefolk.http.response.HolidaySongData
import ge.baqar.gogia.gefolk.model.Song


open class DashboardResult

data class DashboardState(
    val isInProgress: Boolean,
    val daySong: Song?,
    val dayChant: Song?,
    val holidayItems: MutableList<HolidaySongData>?,
    val error: String?
) : DashboardResult() {

    companion object {
        val DEFAULT = DashboardState(
            isInProgress = true,
            error = null,
            dayChant = null,
            daySong = null,
            holidayItems = null
        )
    }
}

