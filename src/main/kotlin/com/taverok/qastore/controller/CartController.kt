package com.taverok.qastore.controller

import com.taverok.qastore.domain.Account
import com.taverok.qastore.dto.JsonMessage
import com.taverok.qastore.dto.response.CartResponse
import com.taverok.qastore.service.AccountService
import com.taverok.qastore.service.CartService
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import org.springframework.web.bind.annotation.*
import java.security.Principal

@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/cart")
class CartController(
    private val accountService: AccountService,
    private val cartService: CartService
) {
    @GetMapping
    fun getCurrent(
        @Parameter(hidden = true) principal: Principal
    ): JsonMessage<CartResponse> {
        val account = accountService.findActiveByEmailOrTrow(principal.name)
        return getResponse(account)
    }

    @PostMapping("/add")
    fun addItem(
        @RequestParam productId: Long,
        @Parameter(hidden = true) principal: Principal
    ): JsonMessage<CartResponse> {
        val account = accountService.findActiveByEmailOrTrow(principal.name)
        cartService.add(productId, account)

        return getResponse(account)
    }

    @DeleteMapping("/remove")
    fun removeItem(
        @RequestParam productId: Long,
        @Parameter(hidden = true) principal: Principal
    ): JsonMessage<CartResponse> {
        val account = accountService.findActiveByEmailOrTrow(principal.name)
        cartService.remove(productId, account)

        return getResponse(account)
    }

    private fun getResponse(account: Account): JsonMessage<CartResponse> {
        val cartItems = cartService.getAll(account)
        val response = CartResponse(
            items = cartItems.map { cartService.toResponse(it) }
        )

        return JsonMessage.of(response)
    }
}