package com.taverok.sca.api.module.user.controller

import com.taverok.sca.api.config.security.JwtConfig
import com.taverok.sca.api.config.security.newAuthToken
import com.taverok.sca.api.module.user.config.AccountCredentials
import com.taverok.sca.api.module.user.config.DeviceCredentials
import com.taverok.sca.api.module.user.service.AccountService
import com.taverok.sca.api.module.user.service.DeviceService
import com.taverok.sca.common.JsonMessage
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/user/v1/auth")
class AuthController(
    private val deviceService: DeviceService,
    private val accountService: AccountService,
    private val jwtConfig: JwtConfig
) {
    @PutMapping
    fun deviceIdAuth(
        @RequestBody request: DeviceCredentials
    ): JsonMessage<String> {
        val device = deviceService.getByUuidOrThrow(request.deviceId)
        val jwt = newAuthToken(jwtConfig.secret, device.uuid, listOf(device.role.name))

        return JsonMessage.of(jwt)
    }

    @PutMapping("/account")
    fun accountAuth(
        @RequestBody request: AccountCredentials
    ): JsonMessage<String> {
        val jwt = accountService.getAuthJwt(request)

        return JsonMessage.of(jwt)
    }
}