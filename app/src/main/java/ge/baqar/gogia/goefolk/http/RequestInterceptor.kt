package ge.baqar.gogia.goefolk.http

import android.app.Application
import com.google.gson.Gson
import ge.baqar.gogia.goefolk.FolkApplication
import ge.baqar.gogia.goefolk.http.response.BaseError
import ge.baqar.gogia.goefolk.http.response.ResponseBase
import ge.baqar.gogia.goefolk.storage.FolkAppPreferences
import ge.baqar.gogia.goefolk.utility.NetworkStatus
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okhttp3.ResponseBody

class RequestInterceptor constructor(
    private val preferences: FolkAppPreferences,
    private var networkStatus: NetworkStatus,
    private val application: Application?
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        if (!networkStatus.isOnline()) {
            val response = Gson().toJson(ResponseBase("", null))
            chain.call().cancel()
            return Response.Builder()
                .code(200)
                .request(request)
                .protocol(Protocol.HTTP_2)
                .message("")
                .body(
                    ResponseBody.create("application/json".toMediaType(), response)
                )
                .build()
        }

        val response = chain.proceed(request)
        try {
            if (!checkAuthorization(response)) {
                (application as FolkApplication).let {
                    preferences.setToken(null)
                    application.logOut()
                    return Response.Builder()
                        .code(401)
                        .request(request)
                        .protocol(Protocol.HTTP_2)
                        .message("authentication error")
                        .body(
                            ResponseBody.create(
                                "application/text".toMediaType(),
                                "authentication error"
                            )
                        )
                        .build()
                }
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return response
    }

    private fun checkAuthorization(response: Response): Boolean {
        return response.code != 401
    }
}