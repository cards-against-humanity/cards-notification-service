package mock

import server.UserFetcher
import server.auth.UserAuthData

class MockUserFetcher : UserFetcher {
    private val users: MutableMap<String, UserAuthData> = HashMap()

    fun setUser(id: String, oAuthId: String, oAuthProvider: String) {
        users[id] = MockUserAuthData(oAuthId, oAuthProvider)
    }

    override fun getById(userId: String): UserAuthData {
        return users[userId]!!
    }
}

private data class MockUserAuthData(override val oAuthId: String, override val oAuthProvider: String) : UserAuthData