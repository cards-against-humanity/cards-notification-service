package route.user

import com.fasterxml.jackson.annotation.JsonProperty
import io.swagger.annotations.*
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import server.socketio.EventServer
import server.Args
import server.UserAuthData
import server.UserFetcher

@RestController
class EventController(private val eventServer: EventServer, private val args: Args, private val userFetcher: UserFetcher) {

    @RequestMapping(value = "event/user/{userId}", method = [RequestMethod.POST])
    @ApiOperation(value = "Push data to user")
    @ApiResponses(
            ApiResponse(code = 201, message = "Event was processed successfully"),
            ApiResponse(code = 404, message = "User does not exist"),
            ApiResponse(code = 400, message = "Bad request body")
    )
    fun postEvent(@PathVariable userId: String, @RequestBody event: EventRequest): ResponseEntity<Int> {
        val userData: UserAuthData
        try {
            userData = userFetcher.getById(userId)
        } catch (e: Exception) {
            e.printStackTrace()
            return ResponseEntity.notFound().build()
        }
        eventServer.sendEvent(userData.oAuthId, userData.oAuthProvider, event.eventName, event.eventData)
        return ResponseEntity.noContent().build()
    }

}

data class EventRequest(@JsonProperty("event") val eventName: String, @JsonProperty("data") val eventData: Any)