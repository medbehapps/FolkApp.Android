package ge.baqar.gogia.goefolk.http

import com.google.gson.GsonBuilder
import ge.baqar.gogia.goefolk.http.services.AccountService
import ge.baqar.gogia.goefolk.http.services.ArtistsService
import ge.baqar.gogia.goefolk.http.services.SearchService
import ge.baqar.gogia.goefolk.http.services.SongService
import okhttp3.OkHttpClient
import org.koin.java.KoinJavaComponent.inject
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


private const val baseURL = "https://ammpjt8siw.us-east-1.awsapprunner.com/"
private var gson = GsonBuilder().setLenient().create()
private val jwtTokenInterceptor by inject<JwtTokenInterceptor>(JwtTokenInterceptor::class.java)
fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
    return Retrofit.Builder().baseUrl(baseURL).client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create(gson)).build()
}

fun provideOkHttpClient(): OkHttpClient {
    return OkHttpClient().newBuilder().build()
}

fun provideAuthorizedOkHttpClient(): OkHttpClient {
    val builder = OkHttpClient().newBuilder()
    builder.interceptors().add(jwtTokenInterceptor)
    return builder.build()
}

fun provideArtistsService(): ArtistsService {
    return provideRetrofit(provideAuthorizedOkHttpClient()).create(ArtistsService::class.java)
}

fun provideAccountService(): AccountService {
    return provideRetrofit(provideOkHttpClient()).create(AccountService::class.java)
}

fun provideSongsService(): SongService {
    return provideRetrofit(provideAuthorizedOkHttpClient()).create(SongService::class.java)
}

fun provideSearchService(): SearchService {
    return provideRetrofit(provideOkHttpClient()).create(SearchService::class.java)
}