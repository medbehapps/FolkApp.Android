package ge.baqar.gogia.goefolk.http.service_implementations

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import ge.baqar.gogia.goefolk.http.response.BaseError
import ge.baqar.gogia.goefolk.http.response.ResponseBase
import ge.baqar.gogia.goefolk.model.ReactiveResult
import ge.baqar.gogia.goefolk.model.asError
import ge.baqar.gogia.goefolk.model.asSuccess
import okhttp3.ResponseBody
import retrofit2.HttpException
import java.lang.reflect.Type

open class ServiceBase {
    fun <TResponse> mapToReactiveResult(response: ResponseBase<TResponse>): ReactiveResult<BaseError, TResponse> {
        if (response.body != null)
            return response.body.asSuccess

        return response.error?.asError!!
    }

    fun escapeServerError(exception: HttpException): ResponseBase<String> {
        val errorText = exception.response()?.message()
        return ResponseBase(null, BaseError(errorText!!))
    }
}