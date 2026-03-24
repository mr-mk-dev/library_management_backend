package com.hacktropia.configration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI libraryOpenAPI() {

        SecurityScheme bearerScheme = new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .description("Enter your JWT token (without 'Bearer ' prefix)");

        return new OpenAPI()
                .info(new Info()
                        .title("Library Management System API")
                        .description("""
                                Complete REST API documentation for the Library Management System.

                                ## Features
                                - **Authentication** — JWT-based signup, login, password reset
                                - **Book Management** — CRUD, search, bulk operations
                                - **Book Loans** — Checkout, checkin, renewal, overdue tracking
                                - **Reservations** — Queue-based book reservations
                                - **Subscriptions** — Membership plans with payment integration
                                - **Payments** — RazorPay payment gateway integration
                                - **Fines** — Overdue/damage/loss fine management
                                - **Reviews & Wishlists** — User book reviews and wishlists

                                ## Authentication
                                1. Call `POST /auth/login` or `POST /auth/signup` to get a JWT token
                                2. Click the **Authorize** button (🔒) above
                                3. Enter the JWT token to authenticate all subsequent requests
                                """)
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Hacktropia")
                                .email("luvag0707@gmail.com"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8082")
                                .description("Local Development Server")))
                .components(new Components()
                        .addSecuritySchemes("Bearer Authentication", bearerScheme))
                .addSecurityItem(new SecurityRequirement()
                        .addList("Bearer Authentication"));
    }
}
