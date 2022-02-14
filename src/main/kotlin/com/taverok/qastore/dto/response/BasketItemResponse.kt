package com.taverok.qastore.dto.response

data class BasketItemResponse(
    val product: ProductResponse,
    val totalPrice: Double,
    val quantity: Long
)

