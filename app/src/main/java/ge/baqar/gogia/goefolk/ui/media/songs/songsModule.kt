package ge.baqar.gogia.goefolk.ui.media.songs

import ge.baqar.gogia.goefolk.http.service_implementations.SongServiceImpl
import kotlinx.coroutines.InternalCoroutinesApi
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

@InternalCoroutinesApi
val songsModule = module {
    viewModel { SongsViewModel(get(), get(), get(), get()) }
    single { SongServiceImpl(get()) }
}

