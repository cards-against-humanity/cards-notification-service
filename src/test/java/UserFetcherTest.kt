import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.mockserver.integration.ClientAndServer
import org.mockserver.model.HttpRequest.request
import org.mockserver.model.HttpResponse.response
import server.UserFetcher
import kotlin.test.assertEquals

class UserFetcherTest {
    companion object {
        private val mockServer: ClientAndServer = ClientAndServer.startClientAndServer(8080)

        @JvmStatic
        @AfterAll
        fun stopServer() {
            mockServer.stop()
        }
    }

    private val userId = "foo"
    private val oAuthId = "1234"
    private val oAuthProvider = "google"

    private val fakeUserId = "fake_user_id"
    private val errorUserId = "error_user_id"

    init {
        mockServer.`when`(
                request()
                        .withMethod("GET")
                        .withPath("/user/${userId}")
        ).respond(
                response().withBody("{\"oAuthId\": \"${oAuthId}\", \"oAuthProvider\": \"${oAuthProvider}\"}")
        )

        mockServer.`when`(
                request()
                        .withMethod("GET")
                        .withPath("/user/${fakeUserId}")
        ).respond(
                response().withStatusCode(404)
        )

        mockServer.`when`(
                request()
                        .withMethod("GET")
                        .withPath("/user/${errorUserId}")
        ).respond(
                response().withStatusCode(400)
        )
    }

    @Test
    fun getValidUser() {
        val userData = UserFetcher("localhost", 8080).getById(userId)
        assertEquals(oAuthId, userData.oAuthId)
        assertEquals(oAuthProvider, userData.oAuthProvider)
    }

    @Test
    fun getInvalidUser() {
        val e = assertThrows(Exception::class.java) { UserFetcher("localhost", 8080).getById(fakeUserId) }
        assertEquals(e.message, "User does not exist")
    }

    @Test
    fun apiError() {
        val e = assertThrows(Exception::class.java) { UserFetcher("localhost", 8080).getById(errorUserId) }
        assertEquals(e.message, "An error occured fetching user from the api")
    }
}