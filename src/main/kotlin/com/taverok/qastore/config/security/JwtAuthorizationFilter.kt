package com.taverok.qastore.security

import io.jsonwebtoken.*
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter

import mu.KotlinLogging
import org.springframework.http.HttpHeaders
import org.springframework.util.StringUtils.hasText
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import java.util.Objects

import team.dats.qiwiManager.config.security.JwtConfig
import team.dats.qiwiManager.config.security.bearerPrefix

class JwtAuthorizationFilter(
        authenticationManager: AuthenticationManager,
        private val jwtConfig: JwtConfig
) : BasicAuthenticationFilter(authenticationManager) {
    private val log = KotlinLogging.logger {}

    override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse,
                                  filterChain: FilterChain) {
        val header = request.getHeader(HttpHeaders.AUTHORIZATION)

        if ( hasText(header) && header.startsWith(bearerPrefix) ){
            SecurityContextHolder.getContext().authentication = getJwtAuthentication(request)
        }

        filterChain.doFilter(request, response)
    }

    private fun getJwtAuthentication(request: HttpServletRequest): UsernamePasswordAuthenticationToken? {
        val token = request.getHeader(HttpHeaders.AUTHORIZATION).replace(bearerPrefix, "")
        var username = ""

        try {
            val signingKey = jwtConfig.secret.toByteArray()

            val jwt = Jwts.parser()
                    .setSigningKey(signingKey)
                    .parseClaimsJws(token)

            if (Objects.isNull(jwt.body.expiration)) {
                log.error("JWT has no expire attribute (deviceId: {}): {}", username, token)
                return null
            }

            username = jwt.body.subject

            val authorities = (jwt.body["rol"] as List<*>)
                    .map { authority -> SimpleGrantedAuthority("ROLE_$authority") }
                    .toList()

            return UsernamePasswordAuthenticationToken(username, null, authorities)
        } catch (exception: ExpiredJwtException) {
            log.info("JWT expired (deviceId: {}): {}", username, exception.message)
        }catch (exception: SignatureException) {
            log.warn("JWT invalid signature (deviceId: {}): {} failed : {}", username, token, exception.message)
        } catch (exception: JwtException) {
            log.warn("JWT failed (deviceId: {}): {} {}", username, token, exception.message)
        }

        return null
    }
}
