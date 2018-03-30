package server.socketio

interface EventServer {
    fun sendEvent(oAuthId: String, oAuthProvider: String, eventName: String, eventData: Any)
    fun stop()
}