package com.taverok.qastore.dto.response

import com.taverok.qastore.domain.DeliveryType
import com.taverok.qastore.domain.OrderStatus
import com.taverok.qastore.domain.PaymentType
import java.time.LocalDateTime

data class OrderResponse(
    val id: Long?,
    val orderAmount: Double,
    val total: Double,
    val spentBonuses: Double,
    val earnedBonuses: Double,
    val deliveryPrice: Double,
    val address: String,

    val paymentType: PaymentType,
    val deliveryType: DeliveryType,
    val status: OrderStatus,
    val createdAt: LocalDateTime
)
