package server

import config.SwaggerConfig
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration
import org.springframework.context.annotation.ComponentScan
import route.user.UserController

@EnableAutoConfiguration(exclude = [MongoAutoConfiguration::class])
@SpringBootApplication
@ComponentScan(basePackageClasses = [UserController::class, SwaggerConfig::class])
open class Main {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            SpringApplication.run(Main::class.java, *args)
        }
    }
}