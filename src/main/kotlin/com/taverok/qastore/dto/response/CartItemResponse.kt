package com.taverok.qastore.dto.response

data class CartItemResponse(
    val product: ProductResponse,
    val totalPrice: Double,
    val quantity: Long
)

