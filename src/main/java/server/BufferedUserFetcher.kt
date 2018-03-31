package server

import server.auth.UserAuthData
import java.util.LinkedHashMap



class BufferedUserFetcher(private val fetcher: UserFetcher, private val maxSize: Int) : UserFetcher {
    private val userCache = object : LinkedHashMap<String, UserAuthData>() {
        override fun removeEldestEntry(eldest: MutableMap.MutableEntry<String, UserAuthData>?): Boolean {
            return size > maxSize
        }
    }

    override fun getById(userId: String): UserAuthData {
        if (userCache[userId] == null) {
            userCache[userId] = fetcher.getById(userId)
        }
        return userCache[userId]!!
    }
}