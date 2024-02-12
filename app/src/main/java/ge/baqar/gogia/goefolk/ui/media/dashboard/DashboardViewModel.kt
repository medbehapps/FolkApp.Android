package ge.baqar.gogia.goefolk.ui.media.dashboard

import android.icu.util.Calendar
import ge.baqar.gogia.goefolk.ui.ReactiveViewModel
import ge.baqar.gogia.goefolk.http.service_implementations.DashboardServiceImpl
import ge.baqar.gogia.goefolk.model.FailedResult
import ge.baqar.gogia.goefolk.model.SucceedResult
import kotlinx.coroutines.flow.Flow

class DashboardViewModel(private val dashboardService: DashboardServiceImpl) :
    ReactiveViewModel<DashboardAction, DashboardResult, DashboardState>(
        DashboardState.DEFAULT
    ) {
    override fun DashboardAction.process(): Flow<() -> DashboardResult> {
        return when (this) {
            is DashboardDataRequested -> update {
                val time = Calendar.getInstance()
                val date = "${time.get(Calendar.YEAR)}/${time.get(Calendar.MONTH) + 1}/${time.get(Calendar.DAY_OF_MONTH)}"
                dashboardService.dashboardData(date).collect {
                    if (it is SucceedResult) {
                        emit {
                            state.copy(
                                isInProgress = false,
                                error = null,
                                daySong = it.value.daySong,
                                dayChant = it.value.dayChant,
                                holidayItems = it.value.holidayItems
                            )
                        }
                    }
                    if (it is FailedResult) {
                        emit { state.copy(isInProgress = false, error = it.value.message) }
                    }
                }
            }

            else -> update {

            }
        }
    }
}