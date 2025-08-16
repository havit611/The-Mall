package com.themall.paymentservice.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

// 配置Swagger/OpenAPI文档的配置类
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI paymentServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Payment Service API")
                        .description("Payment Service API Doc - For payment and refund services, etc")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Lu/me/The real Payment Service Team")
                                .email("payment@themall.com")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8083")
                                .description("开发环境")
                ));
    }
}