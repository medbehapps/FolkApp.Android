package ge.baqar.gogia.gefolk.storage

import org.koin.dsl.module

val storageModule = module {
    single { FolkAppPreferences(get()) }
}