package com.taverok.qastore.security

import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Component
import team.dats.qiwiManager.service.UserService

@Component
class UserDetailsService(
    private val userService: UserService
) : UserDetailsService {

    override fun loadUserByUsername(username: String): UserDetails {
        val user = userService.findActiveByUsernameOrTrow(username)

        val roles = user.role?.name?.let{SimpleGrantedAuthority(it)}?.let { listOf(it) } ?: listOf()
        return User(user.username, user.pass, roles)
    }
}

