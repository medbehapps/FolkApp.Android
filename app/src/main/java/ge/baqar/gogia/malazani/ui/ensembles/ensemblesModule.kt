package ge.baqar.gogia.malazani.ui.ensembles

import ge.baqar.gogia.malazani.http.FolkApiRepository
import kotlinx.coroutines.InternalCoroutinesApi
import org.koin.dsl.module

@InternalCoroutinesApi
val ensemblesModule = module {

    single { FolkApiRepository(get(), get(), get()) }
    //viewModel
    factory { EnsemblesViewModel(get(), get()) }
}