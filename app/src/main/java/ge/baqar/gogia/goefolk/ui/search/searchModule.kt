package ge.baqar.gogia.goefolk.ui.search

import kotlinx.coroutines.InternalCoroutinesApi
import org.koin.dsl.module

@InternalCoroutinesApi
val searchModule = module {
    factory { SearchViewModel(get()) }
}

