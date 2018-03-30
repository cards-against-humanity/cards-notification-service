package server

import server.auth.UserAuthData

interface UserFetcher {
    fun getById(userId: String): UserAuthData
}