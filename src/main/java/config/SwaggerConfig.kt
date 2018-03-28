package config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import springfox.documentation.builders.RequestHandlerSelectors
import springfox.documentation.service.ApiInfo
import springfox.documentation.service.Contact
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spring.web.plugins.Docket
import springfox.documentation.swagger2.annotations.EnableSwagger2

@Configuration
@EnableSwagger2
open class SwaggerConfig {
    @Bean
    open fun api(): Docket {
        return Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.basePackage("route"))
                .build().useDefaultResponseMessages(false)
                .apiInfo(metaData())
    }

    private fun metaData(): ApiInfo {
        return ApiInfo(
                "Cards Notification Service",
                "Cards service for push notifications and socket updates",
                "1.0",
                "I mean, I guess you can use this",
                Contact("Tommy Volk", "https://github.com/tvolk131/", "tvolk131@gmail.com"),
                "Apache License Version 2.0",
                "https://www.apache.org/licenses/LICENSE-2.0")
    }
}