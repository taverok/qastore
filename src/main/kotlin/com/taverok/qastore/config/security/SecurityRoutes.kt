package com.taverok.qastore.config.security

import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest
import org.springframework.context.annotation.Bean
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import javax.servlet.http.HttpServletResponse


@EnableWebSecurity
class SecurityConfig(
    private val userDetailsService: UserDetailsService,
    private val jwtConfig: JwtConfig,
    private val encoder: BCryptPasswordEncoder
) : WebSecurityConfigurerAdapter() {
    override fun configure(http: HttpSecurity) {
        http
            .csrf().disable()
            .cors().and()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .exceptionHandling()
            .authenticationEntryPoint { _, rsp, _ -> rsp.sendError(HttpServletResponse.SC_UNAUTHORIZED) }
            .and()
            .addFilter(JwtAuthorizationFilter(authenticationManager(), jwtConfig))
            .authorizeRequests()

            .antMatchers("/error").permitAll()
            .requestMatchers(EndpointRequest.toAnyEndpoint()).permitAll()
            .antMatchers(*swaggerPublicUrls).permitAll()

            .antMatchers(HttpMethod.POST, ).permitAll()

//            // OTHER
            .anyRequest().authenticated()
    }

    override fun configure(auth: AuthenticationManagerBuilder) {
        auth.userDetailsService(userDetailsService).passwordEncoder(encoder)
    }

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val source = UrlBasedCorsConfigurationSource()
        val config = CorsConfiguration()
        source.registerCorsConfiguration("/**", config.applyPermitDefaultValues())
        config.exposedHeaders = listOf("Authorization")
        config.allowedMethods = HttpMethod.values().map { it.name }
        return source
    }
}

val swaggerPublicUrls = listOf("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**").toTypedArray()