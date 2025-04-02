package viettelsoftware.intern.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Library Management API")
                        .version("1.0")
                        .description("API documentation for Library Management System")
                        .contact(new Contact()
                                .name("Dev Team")
                                .email("dev-team@example.com")
                                .url("https://www.example.com")
                        )
                );
    }
}
