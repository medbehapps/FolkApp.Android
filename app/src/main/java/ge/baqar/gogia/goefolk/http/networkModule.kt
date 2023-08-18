package ge.baqar.gogia.goefolk.http

import org.koin.dsl.module

val networkModule = module {
    single { provideArtistsService() }
    single { provideAccountService() }
    single { provideSongsService() }
    single { provideSearchService() }
    single { provideDashboardService() }
    single { providePlaylistService() }
    single { JwtTokenInterceptor(get(), get()) }
    single { RequestInterceptor(get(), get(), get()) }
}