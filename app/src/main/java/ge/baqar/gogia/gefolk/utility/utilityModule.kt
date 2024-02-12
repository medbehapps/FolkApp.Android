package ge.baqar.gogia.gefolk.utility

import org.koin.dsl.module

val utilityModule = module {
    factory { NetworkStatus(get()) }
    single { FileExtensions(get()) }
    single { DeviceId(get()) }
}