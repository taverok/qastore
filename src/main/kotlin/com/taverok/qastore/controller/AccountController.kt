package com.taverok.qastore.controller

import com.taverok.qastore.dto.JsonMessage
import com.taverok.qastore.dto.request.AccountCreateRequest
import com.taverok.qastore.dto.request.AccountUpdateRequest
import com.taverok.qastore.dto.response.AccountResponse
import com.taverok.qastore.service.AccountService
import io.swagger.v3.oas.annotations.Parameter
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.security.Principal
import javax.validation.Valid
import javax.validation.constraints.Min

@Validated
@RestController
@RequestMapping("/accounts")
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