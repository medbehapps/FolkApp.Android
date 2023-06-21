package ge.baqar.gogia.goefolk.ui.ensembles

import ge.baqar.gogia.goefolk.http.FolkApiRepository
import kotlinx.coroutines.InternalCoroutinesApi
import org.koin.dsl.module

@InternalCoroutinesApi
val ensemblesModule = module {

    single { FolkApiRepository(get(), get(), get()) }
    //viewModel
    factory { EnsemblesViewModel(get(), get()) }
}