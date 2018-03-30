package server.auth

interface UserAuthData {
    val oAuthId: String
    val oAuthProvider: String
}