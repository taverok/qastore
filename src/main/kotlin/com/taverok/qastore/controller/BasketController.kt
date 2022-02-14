package com.taverok.qastore.controller

import com.taverok.qastore.domain.Account
import com.taverok.qastore.dto.JsonMessage
import com.taverok.qastore.dto.response.BasketResponse
import com.taverok.qastore.service.AccountService
import com.taverok.qastore.service.BasketService
import io.swagger.v3.oas.annotations.Parameter
import org.springframework.web.bind.annotation.*
import java.security.Principal

@RestController
@RequestMapping("/basket")
class BasketController(
    private val accountService: AccountService,
    private val basketService: BasketService
) {
    @GetMapping
    fun getCurrent(
        @Parameter(hidden = true) principal: Principal
    ): JsonMessage<BasketResponse> {
        val account = accountService.findActiveByEmailOrTrow(principal.name)
        return getResponse(account)
    }

    @PostMapping("/add")
    fun addItem(
        @RequestParam productId: Long,
        @Parameter(hidden = true) principal: Principal
    ): JsonMessage<BasketResponse> {
        val account = accountService.findActiveByEmailOrTrow(principal.name)
        basketService.add(productId, account)

        return getResponse(account)
    }

    @DeleteMapping("/remove")
    fun removeItem(
        @RequestParam productId: Long,
        @Parameter(hidden = true) principal: Principal
    ): JsonMessage<BasketResponse> {
        val account = accountService.findActiveByEmailOrTrow(principal.name)
        basketService.remove(productId, account)

        return getResponse(account)
    }

    private fun getResponse(account: Account): JsonMessage<BasketResponse> {
        val basketItems = basketService.getAll(account)
        val response = BasketResponse(
            items = basketItems.map { basketService.toResponse(it) }
        )

        return JsonMessage.of(response)
    }
}