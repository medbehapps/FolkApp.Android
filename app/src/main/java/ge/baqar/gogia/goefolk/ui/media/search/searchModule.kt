package ge.baqar.gogia.goefolk.ui.media.search

import ge.baqar.gogia.goefolk.http.service_implementations.ArtistsServiceImpl
import ge.baqar.gogia.goefolk.http.service_implementations.SearchServiceImpl
import ge.baqar.gogia.goefolk.http.service_implementations.SongServiceImpl
import kotlinx.coroutines.InternalCoroutinesApi
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

@InternalCoroutinesApi
val searchModule = module {
    viewModel { SearchViewModel(get(), get()) }
    single { SearchServiceImpl(get()) }
    single { ArtistsServiceImpl(get()) }
}

