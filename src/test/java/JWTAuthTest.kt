import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import server.auth.JWTVerifier
import java.util.*
import kotlin.test.assertEquals

class UserCollectionTest {
    private val password = "1234567890password"
    private val verifier = JWTVerifier(password)

    @Test
    fun successfullyDecodeValidTokenWithExpiration() {
        val oAuthId = "1234"
        val oAuthProvider = "google"

        val token = JWT.create()
                .withClaim("oAuthId", oAuthId)
                .withClaim("oAuthProvider", oAuthProvider)
                .withExpiresAt(Date(System.currentTimeMillis() + 1000))
                .sign(Algorithm.HMAC256(password))
        val deserializedUserData = verifier.validate(token)

        assertEquals(oAuthId, deserializedUserData.oAuthId)
        assertEquals(oAuthProvider, deserializedUserData.oAuthProvider)
    }

    @Test
    fun successfullyDecodeValidTokenNoExpiration() {
        val oAuthId = "1234"
        val oAuthProvider = "google"

        val token = JWT.create()
                .withClaim("oAuthId", oAuthId)
                .withClaim("oAuthProvider", oAuthProvider)
                .sign(Algorithm.HMAC256(password))
        val deserializedUserData = verifier.validate(token)

        assertEquals(oAuthId, deserializedUserData.oAuthId)
        assertEquals(oAuthProvider, deserializedUserData.oAuthProvider)
    }

    @Test
    fun throwErrorForOutdatedToken() {
        val oAuthId = "1234"
        val oAuthProvider = "google"

        val token = JWT.create()
                .withClaim("oAuthId", oAuthId)
                .withClaim("oAuthProvider", oAuthProvider)
                .withExpiresAt(Date(System.currentTimeMillis() - 1000))
                .sign(Algorithm.HMAC256(password))
        val e = assertThrows(Exception::class.java, { verifier.validate(token) })
        assert(e.message!!.contains("Token has expired"))
    }

    @Test
    fun throwErrorWithInvalidPassword() {
        val oAuthId = "1234"
        val oAuthProvider = "google"

        val token = JWT.create()
                .withClaim("oAuthId", oAuthId)
                .withClaim("oAuthProvider", oAuthProvider)
                .sign(Algorithm.HMAC256(password + "1234"))

        val e = assertThrows(Exception::class.java, { verifier.validate(token) })
        assert(e.message!!.contains("The Token's Signature resulted invalid when verified using the Algorithm"))
    }

    @Test
    fun throwErrorWhenMissingUserData() {
        val oAuthId = "1234"

        val token = JWT.create()
                .sign(Algorithm.HMAC256(password))
        val e = assertThrows(Exception::class.java, { verifier.validate(token) })
        assertEquals("Token is missing required user claims", e.message)
    }

    @Test
    fun throwErrorWhenUserDataIsNotStrings() {
        val oAuthId = "1234"

        val token = JWT.create()
                .withClaim("oAuthId", 1)
                .withClaim("oAuthProvider", 1)
                .sign(Algorithm.HMAC256(password))
        val e = assertThrows(Exception::class.java, { verifier.validate(token) })
        assertEquals("Token is missing required user claims", e.message)
    }
}