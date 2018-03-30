package server

import java.util.*

class Args constructor(args: Array<String>) {
    val apiUrl: String
    val apiPort: Int
    val httpPort: Int
    val socketIOPort: Int
    val jwtSecret: String

    init {
        val parsedArgs = parseArgs(args)
        this.apiUrl = parsedArgs["API_URL"] as String
        this.apiPort = parsedArgs["API_PORT"] as Int
        this.httpPort = parsedArgs["HTTP_PORT"] as Int
        this.socketIOPort = parsedArgs["SOCKET_IO_PORT"] as Int
        try {
            this.jwtSecret = parsedArgs["JWT_SECRET"] as String
        } catch (e: Exception) {
            throw Exception("Must provide JWT secret in runtime arguments (JWT_SECRET=<super_special_secret>)")
        }
    }

    private fun parseArgs(args: Array<String>): Map<String, Any> {
        val argMap = HashMap<String, Any>()
        argMap["API_URL"] = "localhost"
        argMap["API_PORT"] = 80
        argMap["HTTP_PORT"] = 80
        argMap["SOCKET_IO_PORT"] = 8080

        val argTypes = HashSet(Arrays.asList("API_URL", "API_PORT", "HTTP_PORT", "SOCKET_IO_PORT", "JWT_SECRET"))
        for (arg in args) {
            val key = arg.split("=".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0]
            val value = arg.split("=".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1]
            if (!argTypes.contains(key)) {
                throw IllegalArgumentException("Invalid argument: $arg")
            }
            argMap[key] = value
            if (key == "API_PORT" || key == "SOCKET_IO_PORT" || key == "HTTP_PORT") {
                argMap.replace(key, Integer.parseInt(argMap[key] as String))
            }
        }
        return argMap
    }
}