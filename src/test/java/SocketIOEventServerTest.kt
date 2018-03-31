import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import server.socketio.SocketIOEventServer
import java.util.*
import io.socket.client.Manager
import io.socket.engineio.client.Transport
import org.junit.jupiter.api.AfterAll
import kotlin.test.assertEquals

class SocketIOEventServerTest {
    companion object {
        private const val secret = "super_secret_password_13579"
        private var server = SocketIOEventServer(80, secret)

        @JvmStatic
        @AfterAll
        fun exit() {
            server.stop()
        }
    }

    @BeforeEach
    fun reset() {
        server.stop()
        server = SocketIOEventServer(80, secret)
    }

    @Test
    fun sendsEventToClient() {
        val oAuthId = "122234"
        val oAuthProvider = "google"
        val eventName = "test_event"
        val eventData = "some_data"

        var messageReceived = false
        var isConnected = false

        val socket = IO.socket("http://localhost")
        socket.setCookie("session", getEncodedUserJWT(oAuthId, oAuthProvider, secret))
        socket.on(Socket.EVENT_CONNECT, { _ -> isConnected = true })
        socket.on(Socket.EVENT_DISCONNECT, { _ -> isConnected = false })
        socket.on(eventName, { data ->
            assertEquals(eventData, data[0])
            messageReceived = true
        })
        socket.connect()

        waitUntil { isConnected }
        Thread.sleep(500)
        server.sendEvent(oAuthId, oAuthProvider, eventName, eventData)
        waitUntil { messageReceived }
        socket.disconnect()
        waitUntil { !isConnected }
    }
}

fun Socket.setCookie(name: String, cookie: String) {
    this.io().on(Manager.EVENT_TRANSPORT, Emitter.Listener { args ->
        run {
            val transport = args[0] as Transport

            transport.on(Transport.EVENT_REQUEST_HEADERS, { args ->
                run {
                    val headers = args[0] as MutableMap<String, List<String>>
                    headers.put("Cookie", Arrays.asList("$name=$cookie;"))
                }
            })
        }
    })
}

fun getEncodedUserJWT(oAuthId: String, oAuthProvider: String, secret: String): String {
    val jwt = JWT.create()
            .withClaim("oAuthId", oAuthId)
            .withClaim("oAuthProvider", oAuthProvider)
            .sign(Algorithm.HMAC256(secret))
    return Base64.getEncoder().encodeToString(jwt.toByteArray())
}

fun waitUntil(f: () -> Boolean) {
    while(!f()) {
        Thread.yield()
    }
}