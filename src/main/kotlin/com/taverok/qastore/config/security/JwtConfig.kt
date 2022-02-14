package com.taverok.qastore.config.security

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration

@Configuration
class JwtConfig {
    @Value("\${spring.security.jwt.secret}")
    val secret: String = ""
}
