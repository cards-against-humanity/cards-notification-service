package server.socketio

import com.corundumstudio.socketio.Configuration
import com.corundumstudio.socketio.SocketIOServer
import server.verifyUser
import java.util.*


fun createEventServer(port: Int, secret: String): EventServer {
    val socketConfig = Configuration()
    socketConfig.port = port
//    socketConfig.keyStorePassword = "fake_password" // TODO - Replace password with runtime argument

    val socketServer = SocketIOServer(socketConfig)
    strapServer(socketServer, secret)
    socketServer.start()
    return SocketIOEventServer(socketServer)
}

private fun strapServer(server: SocketIOServer, secret: String) {
    server.addConnectListener { client ->
        run {
            try {
                val userData = client.verifyUser(secret)
                println("A user has connected! oAuth ID: ${userData.oAuthId}, oAuth Provider: ${userData.oAuthProvider}")
                val encodedOAuthID = Base64.getEncoder().encodeToString(userData.oAuthId.toByteArray())
                val encodedOAuthProvider = Base64.getEncoder().encodeToString(userData.oAuthProvider.toByteArray())
                client.joinRoom("$encodedOAuthID $encodedOAuthProvider")
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    server.addDisconnectListener { client ->
        run {
            println("A user has disconnected!")
        }
    }
}

private class SocketIOEventServer(private val socketIOServer: SocketIOServer) : EventServer {
    override fun sendEvent(oAuthId: String, oAuthProvider: String, eventName: String, eventData: Any) {
        val encodedOAuthID = Base64.getEncoder().encodeToString(oAuthId.toByteArray())
        val encodedOAuthProvider = Base64.getEncoder().encodeToString(oAuthProvider.toByteArray())
        socketIOServer.getRoomOperations("$encodedOAuthID $encodedOAuthProvider").sendEvent(eventName, eventData)
    }

    override fun stop() {
        socketIOServer.stop()
    }
}

interface EventServer {
    fun sendEvent(oAuthId: String, oAuthProvider: String, eventName: String, eventData: Any)
    fun stop()
}