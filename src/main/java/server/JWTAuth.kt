package server

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.corundumstudio.socketio.SocketIOClient
import org.apache.commons.codec.binary.Base64
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class JWTVerifier(private val password: String) {
    fun validate(token: String): UserAuthData {
        val token = JWT.require(Algorithm.HMAC256(password)).build().verify(token)
        val oAuthIdClaim = token.claims["oAuthId"]
        val oAuthProviderClaim = token.claims["oAuthProvider"]
        if (oAuthIdClaim?.asString() == null || oAuthProviderClaim?.asString() == null) {
            throw Exception("Token is missing required user claims")
        }
        return JWTUserAuthData(oAuthIdClaim.asString(), oAuthProviderClaim.asString())
    }
}

private data class JWTUserAuthData(override val oAuthId: String, override val oAuthProvider: String) : UserAuthData

interface UserAuthData {
    val oAuthId: String
    val oAuthProvider: String
}


private fun parseCookies(cookies: String): Map<String, List<String>> {
    val namedCookieList = cookies.split(";").map { s -> s.trim() }
    val cookieMap: MutableMap<String, MutableList<String>> = HashMap()

    namedCookieList.forEach { cookie ->
        run {
            val name = cookie.split("=")[0]
            val cookieData = cookie.split("$name=")[1]
            if (cookieMap[name] == null) {
                cookieMap[name] = ArrayList()
            }
            cookieMap[name]!!.add(cookieData)
        }
    }

    return cookieMap
}

fun SocketIOClient.verifyUser(secret: String): UserAuthData {
    val cookiesString = this.handshakeData.httpHeaders["Cookie"]!!
    val cookies = parseCookies(cookiesString)
    var token = String(Base64.decodeBase64(cookies["session"]!![0])).replace("\"", "")
    return JWTVerifier(secret).validate(token)
}