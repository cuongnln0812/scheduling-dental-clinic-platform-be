package com.example.dentalclinicschedulingplatform.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.beans.factory.annotation.Value;

@OpenAPIDefinition(
        info = @Info(
                title = "F-Dental API Specification",
                version = "v1"),
//        servers = {
//                @Server(
//                        description = "Local ENV",
//                        url = "http://localhost:8080/" //đẩy ra env
//                ),
//                @Server(
//                        description = "Production ENV",
//                        url = "http://fdental.ap-southeast-1.elasticbeanstalk.com/" //đẩy ra env
//                )
//        },
        security = @SecurityRequirement(name = "BearerAuth")
)
@SecurityScheme(
        name = "bearerAuth",
        description = "JWT auth description",
        scheme = "bearer",
        type = SecuritySchemeType.HTTP,
        in = SecuritySchemeIn.HEADER,
        bearerFormat = "JWT")

public class OpenAPIConfig {

}
