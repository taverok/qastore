package team.dats.qiwiManager.config.security

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import java.util.*

const val bearerPrefix = "Bearer "

class JwtConfig {
    val secret: String = "YZyQhG8h4xLdCP"

    fun newAuthToken(auth : Authentication): String {
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_YEAR, 1)

        val token = Jwts.builder()
                .setSubject(auth.name)
                .claim("rol", auth.authorities.map(GrantedAuthority::getAuthority) )
                .setIssuedAt(Date())
                .setExpiration(cal.time)
                .signWith(SignatureAlgorithm.HS512, secret.toByteArray())
                .compact()

        return bearerPrefix + token
    }
}
