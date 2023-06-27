package ge.baqar.gogia.goefolk.http

import ge.baqar.gogia.goefolk.storage.FolkAppPreferences
import ge.baqar.gogia.goefolk.utility.NetworkStatus
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response


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

        val request: Request = chain
            .request()
            .newBuilder()
           .addHeader("Authorization", "Bearer ${preferences.getToken()}")
            .build()
        return chain.proceed(request)
    }
}