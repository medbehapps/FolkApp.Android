package ge.baqar.gogia.goefolk.ui.media.dashboard

import ge.baqar.gogia.goefolk.arch.ReactiveViewModel
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
                dashboardService.dashboardData().collect {
                    if (it is SucceedResult) {
                        emit {
                            state.copy(
                                isInProgress = false,
                                error = null,
                                daySong = it.value.daySong,
                                dayChant = it.value.dayChant,
                                holdayData = it.value.holidaySongData
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