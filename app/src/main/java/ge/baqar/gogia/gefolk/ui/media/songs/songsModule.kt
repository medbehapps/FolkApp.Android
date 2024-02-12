package ge.baqar.gogia.gefolk.ui.media.songs

import ge.baqar.gogia.gefolk.http.service_implementations.PlayListServiceImpl
import ge.baqar.gogia.gefolk.http.service_implementations.SongServiceImpl
import ge.baqar.gogia.gefolk.ui.media.playlist.AddSongToPlayListDialog
import kotlinx.coroutines.InternalCoroutinesApi
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

@InternalCoroutinesApi
val songsModule = module {
    viewModel { SongsViewModel(get()) }
    single { PlayListServiceImpl(get()) }
    single { AddSongToPlayListDialog(get()) }
    single { SongServiceImpl(get()) }
}

