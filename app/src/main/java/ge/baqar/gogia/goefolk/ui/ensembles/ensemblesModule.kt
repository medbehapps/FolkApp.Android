package ge.baqar.gogia.goefolk.ui.ensembles

import ge.baqar.gogia.goefolk.http.service_implementations.ArtistsServiceImpl
import ge.baqar.gogia.goefolk.http.service_implementations.SongServiceImpl
import kotlinx.coroutines.InternalCoroutinesApi
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

@InternalCoroutinesApi
val ensemblesModule = module {
    single { ArtistsServiceImpl(get(), get()) }
    viewModel { EnsemblesViewModel(get(), get()) }
}