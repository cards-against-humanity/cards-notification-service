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
        private const val socketHost = "http://localhost:8080"
        private var server = SocketIOEventServer(8080, secret)

        @JvmStatic
        @AfterAll
        fun exit() {
            server.stop()
        }
    }

    @BeforeEach
    fun reset() {
        server.stop()
        server = SocketIOEventServer(8080, secret)
    }

    @Test
    fun sendsEventToClient() {
        val oAuthId = "122234"
        val oAuthProvider = "google"
        val eventName = "test_event"
        val eventData = "some_data"

        var messageReceived = false

        val socket = TestableSocket(socketHost)
        socket.setSessionCookie(oAuthId, oAuthProvider, secret)
        socket.on(eventName, Emitter.Listener { data ->
            assertEquals(eventData, data[0])
            messageReceived = true
        })
        socket.connect()
        server.sendEvent(oAuthId, oAuthProvider, eventName, eventData)
        waitUntil { messageReceived }
        socket.disconnect()
    }

    @Test
    fun sendsEventsOnlyToSpecificUser() {
        val socketOneOAuthId = "122234"
        val socketOneOAuthProvider = "google"
        val socketTwoOAuthId = "543982"
        val socketTwoOAuthProvider = "google"
        val eventName = "test_event"
        val eventData = "some_data"

        var socketOneMessageReceived = false
        var socketTwoMessageReceived = false

        val socketOne = TestableSocket(socketHost)
        val socketTwo = TestableSocket(socketHost)
        socketOne.setSessionCookie(socketOneOAuthId, socketOneOAuthProvider, secret)
        socketTwo.setSessionCookie(socketTwoOAuthId, socketTwoOAuthProvider, secret)
        socketOne.on(eventName, Emitter.Listener { data ->
            assertEquals(eventData, data[0])
            socketOneMessageReceived = true
        })
        socketTwo.on(eventName, Emitter.Listener { data ->
            assertEquals(eventData, data[0])
            socketTwoMessageReceived = true
        })
        socketOne.connect()
        socketTwo.connect()

        server.sendEvent(socketOneOAuthId, socketOneOAuthProvider, eventName, eventData)
        waitUntil { socketOneMessageReceived }
        Thread.sleep(500)
        assert(!socketTwoMessageReceived)
        socketOne.disconnect()
        socketTwo.disconnect()
    }

    @Test
    fun sendsEventToMultipleClientsBelongingToSameUser() {
        val socketOAuthId = "122234"
        val socketOAuthProvider = "google"
        val eventName = "test_event"
        val eventData = "some_data"

        var socketOneMessageReceived = false
        var socketTwoMessageReceived = false

        val socketOne = TestableSocket(socketHost)
        val socketTwo = TestableSocket(socketHost)
        socketOne.setSessionCookie(socketOAuthId, socketOAuthProvider, secret)
        socketTwo.setSessionCookie(socketOAuthId, socketOAuthProvider, secret)
        socketOne.on(eventName, Emitter.Listener { data ->
            assertEquals(eventData, data[0])
            socketOneMessageReceived = true
        })
        socketTwo.on(eventName, Emitter.Listener { data ->
            assertEquals(eventData, data[0])
            socketTwoMessageReceived = true
        })
        socketOne.connect()
        socketTwo.connect()

        server.sendEvent(socketOAuthId, socketOAuthProvider, eventName, eventData)
        waitUntil { socketOneMessageReceived }
        waitUntil { socketTwoMessageReceived }
        socketOne.disconnect()
        socketTwo.disconnect()
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

private class TestableSocket(host: String) {
    private val socket = IO.socket(host)
    private var isConnected = false

    init {
        socket.on(Socket.EVENT_CONNECT, { _ -> isConnected = true })
        socket.on(Socket.EVENT_DISCONNECT, { _ -> isConnected = false })
    }

    fun setSessionCookie(oAuthId: String, oAuthProvider: String, secret: String) {
        socket.setCookie("session", getEncodedUserJWT(oAuthId, oAuthProvider, secret))
    }

    fun connect() {
        socket.connect()
        waitUntil { isConnected }
        Thread.sleep(500)
    }

    fun disconnect() {
        socket.disconnect()
        waitUntil { !isConnected }
        Thread.sleep(500)
    }

    fun on(eventName: String, f: Emitter.Listener) {
        socket.on(eventName, f)
    }
}