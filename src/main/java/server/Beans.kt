package server

import org.springframework.boot.ApplicationArguments
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import server.socketio.EventServer
import server.socketio.createEventServer

@Configuration
open class Beans {
    @Bean
    open fun getArgs(args: ApplicationArguments): Args {
        return Args(args.sourceArgs)
    }

    @Bean
    open fun getUserFetcher(args: Args): UserFetcher {
        return APIUserFetcher(args.apiUrl, args.apiPort)
    }

    @Bean
    open fun getEventServer(args: Args): EventServer {
        return createEventServer(args.socketIOPort, args.jwtSecret)
    }
}