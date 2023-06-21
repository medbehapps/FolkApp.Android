package ge.baqar.gogia.goefolk.storage

import ge.baqar.gogia.goefolk.storage.usecase.FileSaveController
import org.koin.dsl.module

val storageModule = module {
    single { FolkAppPreferences(get()) }
    single { AlbumDownloadProvider(get(), get(), get()) }
    single { FileSaveController.getInstance(get()) }
    factory { provideFolkApiDatabase(get()) }
}