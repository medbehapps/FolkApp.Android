package ge.baqar.gogia.goefolk.ui.favourites

import kotlinx.coroutines.InternalCoroutinesApi
import org.koin.dsl.module

@InternalCoroutinesApi
val favouritesModule = module {
    factory { FavouritesViewModel(get(), get(), get()) }
}

