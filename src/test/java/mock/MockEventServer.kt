package mock

import server.socketio.EventServer

class MockEventServer : EventServer {
    var isRunning = true
    private set

    var events: MutableList<Event> = ArrayList()
    private set

    override fun sendEvent(oAuthId: String, oAuthProvider: String, eventName: String, eventData: Any) {
        events.add(Event(oAuthId, oAuthProvider, eventName, eventData))
    }

    override fun stop() {
        isRunning = false
    }
}

data class Event(val oAuthId: String, val oAuthProvider: String, val eventName: String, val eventData: Any)