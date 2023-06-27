package ge.baqar.gogia.goefolk.http

import org.koin.dsl.module

val networkModule = module {
    factory { provideArtistsService() }
    factory { provideAccountService() }
    factory { provideSongsService() }
    factory { provideSearchService() }
    factory { provideOkHttpClient() }
    factory { provideRetrofit(get()) }
    single { JwtTokenInterceptor(get()) }
}