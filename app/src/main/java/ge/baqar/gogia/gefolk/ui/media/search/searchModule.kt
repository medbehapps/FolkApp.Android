package ge.baqar.gogia.gefolk.ui.media.search

import ge.baqar.gogia.gefolk.http.service_implementations.ArtistsServiceImpl
import ge.baqar.gogia.gefolk.http.service_implementations.SearchServiceImpl
import kotlinx.coroutines.InternalCoroutinesApi
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

@InternalCoroutinesApi
val searchModule = module {
    viewModel { SearchViewModel(get(), get()) }
    single { SearchServiceImpl(get()) }
    single { ArtistsServiceImpl(get()) }
}

