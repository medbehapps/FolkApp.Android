package ge.baqar.gogia.goefolk.ui.media.dashboard

import ge.baqar.gogia.goefolk.http.response.HolidaySongData
import ge.baqar.gogia.goefolk.model.Song


open class DashboardResult

data class DashboardState(
    val isInProgress: Boolean,
    val daySong: Song?,
    val dayChant: Song?,
    val holdayData: HolidaySongData?,
    val error: String?
) : DashboardResult() {

    companion object {
        val DEFAULT = DashboardState(
            isInProgress = true,
            error = null,
            dayChant = null,
            daySong = null,
            holdayData = null
        )
    }
}

