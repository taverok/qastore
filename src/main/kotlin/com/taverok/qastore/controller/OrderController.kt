package com.taverok.qastore.controller

import com.taverok.qastore.dto.JsonMessage
import com.taverok.qastore.dto.request.OrderCreateRequest
import com.taverok.qastore.dto.response.OrderResponse
import com.taverok.qastore.service.AccountService
import com.taverok.qastore.service.OrderService
import io.swagger.v3.oas.annotations.Parameter
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.security.Principal
import javax.validation.Valid

@RestController
@RequestMapping("/orders")
class OrderController(
    private val orderService: OrderService,
    private val accountService: AccountService
) {
    @PostMapping
    fun create(
        @Valid @RequestBody request: OrderCreateRequest,
        @Parameter(hidden = true) principal: Principal
    ): JsonMessage<OrderResponse> {
        val account = accountService.findActiveByEmailOrTrow(principal.name)
        val order = orderService.save(request, account)
        val response = orderService.toResponse(order)

        return JsonMessage.of(response)
    }

    @PutMapping("/preview")
    fun preview(
        @Valid @RequestBody request: OrderCreateRequest,
        @Parameter(hidden = true) principal: Principal
    ): JsonMessage<OrderResponse> {
        val account = accountService.findActiveByEmailOrTrow(principal.name)
        val order = orderService.newOrder(request, account)
        val response = orderService.toResponse(order)

        return JsonMessage.of(response)
    }

    @GetMapping
    fun list(
        @Parameter(hidden = true) principal: Principal
    ): JsonMessage<List<OrderResponse>> {
        val account = accountService.findActiveByEmailOrTrow(principal.name)
        val orders = orderService.getAll(account)
        val response = orders.map { orderService.toResponse(it) }

        return JsonMessage.of(response)
    }
}