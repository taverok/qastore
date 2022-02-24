package com.taverok.qastore.controller

import com.taverok.qastore.dto.JsonMessage
import com.taverok.qastore.dto.OK
import com.taverok.qastore.dto.request.OrderCreateRequest
import com.taverok.qastore.dto.request.OrderUpdateRequest
import com.taverok.qastore.dto.request.PaymentRequest
import com.taverok.qastore.dto.response.OrderResponse
import com.taverok.qastore.service.AccountService
import com.taverok.qastore.service.OrderService
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import org.springframework.web.bind.annotation.*
import java.security.Principal
import javax.validation.Valid

@SecurityRequirement(name = "bearerAuth")
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


    @DeleteMapping("{orderId}")
    fun cancel(
        @PathVariable orderId: Long,
        @Parameter(hidden = true) principal: Principal
    ): JsonMessage<Boolean> {
        val account = accountService.findActiveByEmailOrTrow(principal.name)
        orderService.cancel(orderId, account)

        return OK
    }

    @PatchMapping("{orderId}")
    fun update(
        @PathVariable orderId: Long,
        @RequestBody request: OrderUpdateRequest,
        @Parameter(hidden = true) principal: Principal
    ): JsonMessage<OrderResponse> {
        val account = accountService.findActiveByEmailOrTrow(principal.name)
        val order = orderService.update(orderId, request, account)
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

    @PutMapping("pay")
    fun payment(
        @Valid @RequestBody request: PaymentRequest,
        @Parameter(hidden = true) principal: Principal
    ): JsonMessage<OrderResponse> {
        val account = accountService.findActiveByEmailOrTrow(principal.name)
        val order = orderService.getByIdOrThrow(request.orderId)

        orderService.consumeBonuses(order, account)
        orderService.processPayment(request, account)

        val response = orderService.toResponse(order)

        return JsonMessage.of(response)
    }

}