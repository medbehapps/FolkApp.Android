package ge.baqar.gogia.malazani.http

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


private const val baseURL = "https://ammpjt8siw.us-east-1.awsapprunner.com/"

fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
    return Retrofit.Builder()
        .baseUrl(baseURL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
}

fun provideOkHttpClient(): OkHttpClient {
    return OkHttpClient().newBuilder().build()
}


fun provideFolkApiEndpoint(): FolkApiService {
    return provideRetrofit(provideOkHttpClient()).create(FolkApiService::class.java)
}

fun provideSearchEndpoint(): SearchService {
    return provideRetrofit(provideOkHttpClient()).create(SearchService::class.java)
}