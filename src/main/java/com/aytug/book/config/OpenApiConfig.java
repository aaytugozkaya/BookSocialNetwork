package com.aytug.book.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;

@OpenAPIDefinition(
        info = @Info(
                contact = @Contact(
                        name = "Book API",
                        email = "aaytugozkaya@gmail.com"
                ),
                description = "Book Social Network API",
                title = "Book API specs",
                version = "1.0.0"

        ),
        servers = {
                @io.swagger.v3.oas.annotations.servers.Server(
                        url = "http://localhost:8088/api/v1",
                        description = "Local Env"
                )
        },
        security = {
                @io.swagger.v3.oas.annotations.security.SecurityRequirement(
                        name = "bearer-key"
                )
        }
)
@SecurityScheme(
        name="bearer-key",
        description = "Bearer Token",
        scheme = "bearer",
        type= SecuritySchemeType.HTTP,
        bearerFormat = "JWT",in = SecuritySchemeIn.HEADER

)
public class OpenApiConfig {
}
