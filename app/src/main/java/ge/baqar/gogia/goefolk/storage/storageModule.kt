package ge.baqar.gogia.goefolk.storage

import org.koin.dsl.module

val storageModule = module {
    single { FolkAppPreferences(get()) }
}