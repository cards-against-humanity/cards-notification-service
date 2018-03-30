import mock.MockEventServer
import mock.MockUserFetcher
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import route.event.EventController
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import kotlin.test.assertEquals

class EventControllerTest {
    private var userFetcher = MockUserFetcher()
    private var eventServer = MockEventServer()
    private var mockMvc = MockMvcBuilders.standaloneSetup(EventController(eventServer, userFetcher)).build()


    @BeforeEach
    fun reset() {
        userFetcher = MockUserFetcher()
        eventServer = MockEventServer()
        mockMvc = MockMvcBuilders.standaloneSetup(EventController(eventServer, userFetcher)).build()
    }

    @Test
    fun successfulRequest() {
        val userId = "1"
        val eventName = "test_event"
        val eventData = "test_event_data"
        userFetcher.setUser(userId, "1234", "google")

        assertEquals(0, eventServer.events.size)

        val request = mockMvc.perform(post("/event/user/$userId").contentType(MediaType.APPLICATION_JSON).content("{\"event\": \"$eventName\", \"data\": \"$eventData\"}")).andExpect(status().isNoContent)

        assertEquals(1, eventServer.events.size)
        assertEquals(eventName, eventServer.events[0].eventName)
        assertEquals(eventData, eventServer.events[0].eventData)
    }

    @Test
    fun noDataInRequest() {
        userFetcher.setUser("1", "1234", "google")
        mockMvc.perform(post("/event/user/1").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest)
    }

    @Test
    fun postToNonExistingUser() {
         mockMvc.perform(post("/event/user/fake_user").contentType(MediaType.APPLICATION_JSON).content("{\"event\": \"test\", \"data\": \"test\"}")).andExpect(status().isNotFound)
    }
}