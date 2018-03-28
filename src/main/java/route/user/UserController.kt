package route.user

import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiResponse
import io.swagger.annotations.ApiResponses
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
class UserController {

    @RequestMapping(value = "/user/{id}", method = [RequestMethod.GET])
    @ApiOperation(value = "Get a user")
    @ApiResponses(
            ApiResponse(code = 200, message = "Endpoint successfully executed")
    )
    fun getUser(@PathVariable id: String): ResponseEntity<Int> {
        return ResponseEntity.ok(1)
    }
}
