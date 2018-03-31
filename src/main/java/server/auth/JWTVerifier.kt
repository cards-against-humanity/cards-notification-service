package server.auth

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.corundumstudio.socketio.SocketIOClient
import org.apache.commons.codec.binary.Base64

class JWTVerifier(private val secret: String) {
    fun validate(token: String): UserAuthData {
        val token = JWT.require(Algorithm.HMAC256(secret)).build().verify(token)
        val oAuthIdClaim = token.claims["oAuthId"]
        val oAuthProviderClaim = token.claims["oAuthProvider"]
        if (oAuthIdClaim?.asString() == null || oAuthProviderClaim?.asString() == null) {
            throw Exception("Token is missing required user claims")
        }
        return JWTUserAuthData(oAuthIdClaim.asString(), oAuthProviderClaim.asString())
    }

    fun validate(socket: SocketIOClient): UserAuthData {
        val cookiesString = socket.handshakeData.httpHeaders["Cookie"]!! // TODO - Test this line and throw exception with appropriate error message
        val cookies = parseCookies(cookiesString)
        var token = String(Base64.decodeBase64(cookies["session"]!![0])).replace("\"", "")
        return this.validate(token)
    }
}

private data class JWTUserAuthData(override val oAuthId: String, override val oAuthProvider: String) : UserAuthData

private fun parseCookies(cookies: String): Map<String, List<String>> {
    var namedCookieList = cookies.split(";").map { s -> s.trim() }
    namedCookieList = namedCookieList.filter { cookie -> !cookie.isBlank() }
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