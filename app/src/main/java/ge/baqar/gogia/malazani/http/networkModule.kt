package ge.baqar.gogia.malazani.http

import org.koin.dsl.module

val networkModule = module {
    factory { provideFolkApiEndpoint() }
    factory { provideSearchEndpoint() }
    factory { provideOkHttpClient() }
    factory { provideRetrofit(get()) }
}