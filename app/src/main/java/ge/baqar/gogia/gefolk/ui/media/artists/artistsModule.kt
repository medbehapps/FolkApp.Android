package ge.baqar.gogia.gefolk.ui.media.artists

import ge.baqar.gogia.gefolk.http.service_implementations.ArtistsServiceImpl
import kotlinx.coroutines.InternalCoroutinesApi
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

@InternalCoroutinesApi
val artistsModule = module {
    single { ArtistsServiceImpl(get()) }
    viewModel { ArtistsViewModel(get()) }
}