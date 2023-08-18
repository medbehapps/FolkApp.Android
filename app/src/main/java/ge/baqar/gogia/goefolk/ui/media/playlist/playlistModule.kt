package ge.baqar.gogia.goefolk.ui.media.playlist

import ge.baqar.gogia.goefolk.http.service_implementations.PlayListServiceImpl
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val playlistModule = module {
    single { PlayListServiceImpl(get()) }
    viewModel<PlayListViewModel>()
}