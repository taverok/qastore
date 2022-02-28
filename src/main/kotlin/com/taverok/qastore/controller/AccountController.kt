package com.taverok.qastore.controller

import com.taverok.qastore.dto.JsonMessage
import com.taverok.qastore.dto.request.AccountCreateRequest
import com.taverok.qastore.dto.request.AccountUpdateRequest
import com.taverok.qastore.dto.response.AccountResponse
import com.taverok.qastore.service.AccountService
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import java.security.Principal
import javax.validation.Valid
import javax.validation.constraints.Min

@SecurityRequirement(name = "bearerAuth")
@Validated
@RestController
@RequestMapping("/accounts")
class AccountController(
    private val accountService: AccountService
) {
    @SecurityRequirement(name = "")
    @PutMapping
    fun create(
        @Valid @RequestBody request: AccountCreateRequest
    ): JsonMessage<AccountResponse> {
        val account = accountService.create(request)
        val response = accountService.getAccountResponse(account)

        return JsonMessage.of(response)
    }

    @GetMapping
    fun show(
        @Parameter(hidden = true) principal: Principal
    ): JsonMessage<AccountResponse> {
        val account = accountService.findActiveByEmailOrTrow(principal.name)
        val response = accountService.getAccountResponse(account)

        return JsonMessage.of(response)
    }

    @PatchMapping
    fun update(
        @Valid @RequestBody accountUpdateRequest: AccountUpdateRequest,
        @Parameter(hidden = true) principal: Principal
    ): JsonMessage<AccountResponse> {
        val account = accountService.findActiveByEmailOrTrow(principal.name)
        accountService.update(accountUpdateRequest, account)

        val response = accountService.getAccountResponse(account)

        return JsonMessage.of(response)
    }

    @PutMapping("/setBonuses")
    fun setBonuses(
        @Min(0) @RequestParam amount: Double,
        @Parameter(hidden = true) principal: Principal
    ): JsonMessage<AccountResponse> {
        val account = accountService.findActiveByEmailOrTrow(principal.name)
        accountService.setBonuses(amount, account)

        val response = accountService.getAccountResponse(account)

        return JsonMessage.of(response)
    }
}