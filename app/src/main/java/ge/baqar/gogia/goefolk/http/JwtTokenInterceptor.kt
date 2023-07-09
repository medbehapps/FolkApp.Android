package ge.baqar.gogia.goefolk.http

import android.os.Build
import android.util.Log
import ge.baqar.gogia.goefolk.storage.FolkAppPreferences
import ge.baqar.gogia.goefolk.utility.NetworkStatus
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import org.koin.android.BuildConfig


class JwtTokenInterceptor(
    private val preferences: FolkAppPreferences,
    private val networkStatus: NetworkStatus
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        if (!networkStatus.isOnline()) {
            chain.call().cancel()
            return Response.Builder()
                .build()
        }
        if (BuildConfig.DEBUG) {
            Log.d("JWT_Token", preferences.getToken()!!)
        }
        val request: Request = chain
            .request()
            .newBuilder()
            .addHeader("Authorization", "Bearer ${preferences.getToken()}")
            .build()
        return chain.proceed(request)
    }
}