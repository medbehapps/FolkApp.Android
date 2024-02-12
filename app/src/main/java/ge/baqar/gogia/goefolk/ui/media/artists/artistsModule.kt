package ge.baqar.gogia.goefolk.ui.media.artists

import ge.baqar.gogia.goefolk.http.service_implementations.ArtistsServiceImpl
import kotlinx.coroutines.InternalCoroutinesApi
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

@InternalCoroutinesApi
val artistsModule = module {
    single { ArtistsServiceImpl(get()) }
    viewModel { ArtistsViewModel(get()) }
}