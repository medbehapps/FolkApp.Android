package ge.baqar.gogia.gefolk.ui.media.playlist

import ge.baqar.gogia.gefolk.http.service_implementations.PlayListServiceImpl
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val playlistModule = module {
    single { PlayListServiceImpl(get()) }
    viewModel<PlayListViewModel>()
}