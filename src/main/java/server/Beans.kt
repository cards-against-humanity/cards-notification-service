package server

import org.springframework.boot.ApplicationArguments
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import server.socketio.EventServer
import server.socketio.SocketIOEventServer

@Configuration
open class Beans {
    @Bean
    open fun getArgs(args: ApplicationArguments): Args {
        return Args(args.sourceArgs)
    }

    @Bean
    open fun getUserFetcher(args: Args): UserFetcher {
        return ApiUserFetcher(args.apiUrl, args.apiPort)
    }

    @Bean
    open fun getEventServer(args: Args): EventServer {
        return SocketIOEventServer(args.socketIOPort, args.jwtSecret)
    }
}