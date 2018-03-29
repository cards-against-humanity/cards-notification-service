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
    open fun getJWTVerifier(args: Args): JWTVerifier {
        println("SECRET: ${args.jwtSecret}")
        return JWTVerifier(args.jwtSecret)
    }

    @Bean
    open fun getEventServer(args: Args): EventServer {
        return createEventServer(args.socketIOPort, args.jwtSecret)
    }
}