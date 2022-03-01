package com.taverok.qastore.dto.request

import com.taverok.qastore.domain.DeliveryType
import com.taverok.qastore.domain.PaymentType

data class OrderCreateRequest(
    val paymentType: PaymentType,
    val deliveryType: DeliveryType,
    val useBonuses: Boolean = false
)
