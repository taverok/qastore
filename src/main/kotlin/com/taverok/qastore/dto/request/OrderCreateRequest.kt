package com.taverok.qastore.dto.request

import com.taverok.qastore.domain.DeliveryType
import com.taverok.qastore.domain.PaymentType
import javax.validation.constraints.Min

data class OrderCreateRequest(
    @get:Min(0)
    val bonuses: Double,
    val paymentType: PaymentType,
    val deliveryType: DeliveryType
)
