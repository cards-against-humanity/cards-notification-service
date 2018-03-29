package route.user

import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiResponse
import io.swagger.annotations.ApiResponses
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import server.socketio.EventServer

@RestController
class EventController(eventServer: EventServer) {

    @RequestMapping(value = "event/user/{id}", method = [RequestMethod.POST])
    @ApiOperation(value = "Push data to user")
    @ApiResponses(
            ApiResponse(code = 200, message = "Endpoint successfully executed")
    )
    fun getUser(@PathVariable id: String): ResponseEntity<Int> {
        return ResponseEntity.ok(1)
    }
}
