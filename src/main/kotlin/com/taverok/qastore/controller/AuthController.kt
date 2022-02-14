package com.taverok.qastore.controller

import com.taverok.qastore.config.security.AccountCredentials
import com.taverok.qastore.dto.JsonMessage
import com.taverok.qastore.service.AccountService
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/auth")
class AuthController(
    private val accountService: AccountService
) {
    @PutMapping
    fun accountAuth(
        @RequestBody request: AccountCredentials
    ): JsonMessage<String> {
        val jwt = accountService.getAuthJwt(request)

        return JsonMessage.of(jwt)
    }
}