package com.taverok.qastore.config.security

import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter

import org.springframework.http.HttpHeaders
import org.springframework.util.StringUtils.hasText
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class JwtAuthorizationFilter(
        authenticationManager: AuthenticationManager,
        private val jwtConfig: JwtConfig
) : BasicAuthenticationFilter(authenticationManager) {
    override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse,
                                  filterChain: FilterChain) {
        val header = request.getHeader(HttpHeaders.AUTHORIZATION)

        if (hasText(header) && header.startsWith(bearerPrefix)) {
            SecurityContextHolder.getContext().authentication =
                tokenToAuthentication(request.getHeader(HttpHeaders.AUTHORIZATION), jwtConfig.secret)
        }

        filterChain.doFilter(request, response)
    }

}
