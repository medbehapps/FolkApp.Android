package ge.baqar.gogia.gefolk.http

import com.google.gson.GsonBuilder
import ge.baqar.gogia.gefolk.http.services.AccountService
import ge.baqar.gogia.gefolk.http.services.ArtistsService
import ge.baqar.gogia.gefolk.http.services.DashboardService
import ge.baqar.gogia.gefolk.http.services.PlayListService
import ge.baqar.gogia.gefolk.http.services.SearchService
import ge.baqar.gogia.gefolk.http.services.SongService
import okhttp3.OkHttpClient
import org.koin.java.KoinJavaComponent.inject
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


private const val baseURL = "https://ammpjt8siw.us-east-1.awsapprunner.com/"

private var gson = GsonBuilder().setLenient().create()
private val jwtTokenInterceptor by inject<JwtTokenInterceptor>(JwtTokenInterceptor::class.java)
private val requestInterceptor by inject<AuthGuardRequestInterceptor>(AuthGuardRequestInterceptor::class.java)

fun provideFolkApiUrl(okHttpClient: OkHttpClient): Retrofit {
    return Retrofit.Builder().baseUrl(baseURL).client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create(gson)).build()
}

fun provideOkHttpClient(): OkHttpClient {
    val builder = OkHttpClient()
        .newBuilder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .addInterceptor(requestInterceptor)
    return builder.build()
}

fun provideAuthorizedOkHttpClient(): OkHttpClient {
    val builder = OkHttpClient().newBuilder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .addInterceptor(requestInterceptor)
        .addInterceptor(jwtTokenInterceptor)
    return builder.build()
}

fun provideArtistsService(): ArtistsService {
    return provideFolkApiUrl(provideAuthorizedOkHttpClient()).create(ArtistsService::class.java)
}

fun provideAccountService(): AccountService {
    return provideFolkApiUrl(provideOkHttpClient()).create(AccountService::class.java)
}

fun provideSongsService(): SongService {
    return provideFolkApiUrl(provideAuthorizedOkHttpClient()).create(SongService::class.java)
}

fun provideSearchService(): SearchService {
    return provideFolkApiUrl(provideAuthorizedOkHttpClient()).create(SearchService::class.java)
}

fun provideDashboardService(): DashboardService {
    return provideFolkApiUrl(provideAuthorizedOkHttpClient()).create(DashboardService::class.java)
}

fun providePlaylistService(): PlayListService {
    return provideFolkApiUrl(provideAuthorizedOkHttpClient()).create(PlayListService::class.java)
}
