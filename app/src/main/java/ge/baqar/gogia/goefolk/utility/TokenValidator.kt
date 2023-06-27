package ge.baqar.gogia.goefolk.utility

import com.auth0.jwt.JWT
import com.auth0.jwt.interfaces.Claim
import com.auth0.jwt.interfaces.DecodedJWT
import java.util.Date


class TokenValidator {
    var tokenExpired = true

    @Throws(Exception::class)
    fun isTokenValid(token: String): Boolean {
        validateToken(token)
        return tokenExpired
    }

    @Throws(Exception::class)
    fun getExtractedData(token: String): MutableMap<String, Claim>? {
        return validateToken(token)
    }

    @Throws(Exception::class)
    private fun validateToken(token: String): MutableMap<String, Claim>? {
        return try {
            val jwt = JWT.decode(token)
            tokenExpired = false
            return jwt.claims
        } catch (ex: Exception) {
            return mutableMapOf()
        }
    }

    companion object {
        fun isJWTExpired(token: String): Boolean {
            val jwt = JWT.decode(token)
            val expiresAt: Date = jwt.expiresAt
            return expiresAt.before(Date())
        }
    }
}