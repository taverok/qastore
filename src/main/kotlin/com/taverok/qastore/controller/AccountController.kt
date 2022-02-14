package com.taverok.qastore.controller

import com.taverok.qastore.dto.JsonMessage
import com.taverok.qastore.dto.request.AccountCreateRequest
import com.taverok.qastore.dto.response.AccountResponse
import com.taverok.qastore.service.AccountService
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/account")
class AccountController(
    private val accountService: AccountService
) {
    @PutMapping
    fun create(
        @RequestBody request: AccountCreateRequest
    ): JsonMessage<AccountResponse> {
        val account = accountService.create(request)
        val response = accountService.getAccountResponse(account)

        return JsonMessage.of(response)
    }
}