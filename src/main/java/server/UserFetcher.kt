package server

import com.fasterxml.jackson.databind.ObjectMapper
import okhttp3.OkHttpClient
import okhttp3.Request

class UserFetcher(private val apiHost: String, private val apiPort: Int) {
    fun getById(userId: String): UserAuthData {
        val request = Request.Builder()
                .url("http://$apiHost:$apiPort/user/$userId")
                .build()
        val response = OkHttpClient().newCall(request).execute()
        if (response.code() == 404) {
            throw Exception("User does not exist")
        }
        if (!response.isSuccessful) {
            throw Exception("An error occured fetching user from the api")
        }
        val jsonRes = response.body()!!.string()
        val resMap = ObjectMapper().readValue(jsonRes, Map::class.java)

        val oAuthId = resMap["oAuthId"]!! as String
        val oAuthProvider = resMap["oAuthProvider"]!! as String

        return APIUserAuthData(oAuthId, oAuthProvider)
    }
}

private data class APIUserAuthData(override val oAuthId: String, override val oAuthProvider: String) : UserAuthData