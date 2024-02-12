package ge.baqar.gogia.gefolk.ui.media

import ge.baqar.gogia.gefolk.FolkApplication
import ge.baqar.gogia.gefolk.ui.media.songs.SongsViewModel
import kotlinx.coroutines.InternalCoroutinesApi
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import kotlin.time.ExperimentalTime


@OptIn(InternalCoroutinesApi::class, ExperimentalTime::class)
val authorizedModule = module {
    single { (androidApplication() as FolkApplication).activeActivity }
    viewModel { SongsViewModel(get()) }
}