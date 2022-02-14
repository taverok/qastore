package com.taverok.qastore.config.security

import io.jsonwebtoken.*
import mu.KotlinLogging
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import java.util.*


const val bearerPrefix = "Bearer "

private val log = KotlinLogging.logger {}

fun newAuthToken(secret: String, subj: String, roles: List<String> = emptyList() ): String {
    val cal = Calendar.getInstance()
    cal.add(Calendar.DAY_OF_YEAR, 1)

    val token = Jwts.builder()
        .setSubject(subj)
        .claim("rol", roles )
        .setIssuedAt(Date())
        .setExpiration(cal.time)
        .signWith(SignatureAlgorithm.HS512, secret.toByteArray())
        .compact()

    return bearerPrefix + token
}

fun tokenToAuthentication(authHeader: String, secret: String): UsernamePasswordAuthenticationToken? {
    val token =authHeader.replace(bearerPrefix, "")
    var deviceId = ""

    try {
        val signingKey = secret.toByteArray()

        val jwt = Jwts.parser()
            .setSigningKey(signingKey)
            .parseClaimsJws(token)

        if (Objects.isNull(jwt.body.expiration)) {
            log.error("JWT has no expire attribute (deviceId: {}): {}", deviceId, token)
            return null
        }

        deviceId = jwt.body.subject

        val authorities = (jwt.body["rol"] as List<*>)
            .map { authority -> SimpleGrantedAuthority("ROLE_$authority") }
            .toList()

        return UsernamePasswordAuthenticationToken(deviceId, null, authorities)
    } catch (exception: ExpiredJwtException) {
        log.info("JWT expired (deviceId: {}): {}", deviceId, exception.message)
    } catch (exception: SignatureException) {
        log.warn("JWT invalid signature (deviceId: {}): {} failed : {}", deviceId, token, exception.message)
    } catch (exception: JwtException) {
        log.warn("JWT failed (deviceId: {}): {} {}", deviceId, token, exception.message)
    }

    return null
}