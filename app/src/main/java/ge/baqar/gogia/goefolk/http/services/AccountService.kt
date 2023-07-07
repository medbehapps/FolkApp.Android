package ge.baqar.gogia.goefolk.http.services

import ge.baqar.gogia.goefolk.http.request.LoginByTokenRequest
import ge.baqar.gogia.goefolk.http.request.LoginRequest
import ge.baqar.gogia.goefolk.http.request.RegisterAccountRequest
import ge.baqar.gogia.goefolk.http.request.VerifyAccountRequest
import ge.baqar.gogia.goefolk.http.response.ResponseBase
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface AccountService {

    @POST("account/register")
    suspend fun register(@Body request: RegisterAccountRequest): ResponseBase<String>

    @PUT("account/verify/{id}")
    suspend fun verify(@Body request: VerifyAccountRequest, @Path("id") id: String): ResponseBase<Boolean>

    @POST("account/login")
    suspend fun login(@Body request: LoginRequest): ResponseBase<String>

    @POST("account/login-by-token")
    suspend fun loginByToken(@Body request: LoginByTokenRequest): ResponseBase<String>
}