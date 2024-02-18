package ge.baqar.gogia.gefolk.http

import org.koin.dsl.module

val networkModule = module {
    single { provideArtistsService() }
    single { provideAccountService() }
    single { provideSongsService() }
    single { provideSearchService() }
    single { provideDashboardService() }
    single { providePlaylistService() }
    single { JwtTokenInterceptor(get(), get()) }
    single { AuthGuardRequestInterceptor(get(), get(), get()) }
}