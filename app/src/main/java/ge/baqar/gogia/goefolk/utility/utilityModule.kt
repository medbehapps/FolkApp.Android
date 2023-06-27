package ge.baqar.gogia.goefolk.utility

import org.koin.dsl.module

val utilityModule = module {
    factory { NetworkStatus(get()) }
    single { FileExtensions(get()) }
    single { DeviceId(get()) }
}