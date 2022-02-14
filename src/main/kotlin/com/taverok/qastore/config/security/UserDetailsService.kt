package com.taverok.qastore.config.security

import com.taverok.qastore.domain.AccountRoles
import com.taverok.qastore.service.AccountService
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Component

@Component
class UserDetailsService(
    private val accountService: AccountService
) : UserDetailsService {

    override fun loadUserByUsername(username: String): UserDetails {
        val user = accountService.findActiveByEmailOrTrow(username)

        val roles = listOf(SimpleGrantedAuthority(AccountRoles.USER.name))
        return User(user.username, user.pass, roles)
    }
}

