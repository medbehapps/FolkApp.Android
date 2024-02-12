package ge.baqar.gogia.gefolk.http.services

import ge.baqar.gogia.gefolk.http.request.LoginByTokenRequest
import ge.baqar.gogia.gefolk.http.request.LoginRequest
import ge.baqar.gogia.gefolk.http.request.RegisterAccountRequest
import ge.baqar.gogia.gefolk.http.request.VerifyAccountRequest
import ge.baqar.gogia.gefolk.http.response.ResponseBase
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