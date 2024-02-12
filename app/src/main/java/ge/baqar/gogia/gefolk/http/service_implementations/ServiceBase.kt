package ge.baqar.gogia.gefolk.http.service_implementations

import ge.baqar.gogia.gefolk.http.response.BaseError
import ge.baqar.gogia.gefolk.http.response.ResponseBase
import ge.baqar.gogia.gefolk.model.ReactiveResult
import ge.baqar.gogia.gefolk.model.asError
import ge.baqar.gogia.gefolk.model.asSuccess
import retrofit2.HttpException

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