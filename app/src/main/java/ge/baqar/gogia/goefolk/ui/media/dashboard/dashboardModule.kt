package ge.baqar.gogia.goefolk.ui.media.dashboard

import ge.baqar.gogia.goefolk.http.service_implementations.DashboardServiceImpl
import kotlinx.coroutines.InternalCoroutinesApi
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

@InternalCoroutinesApi
val dashboardModule = module {
    single { DashboardServiceImpl(get()) }
    viewModel { DashboardViewModel(get()) }
}