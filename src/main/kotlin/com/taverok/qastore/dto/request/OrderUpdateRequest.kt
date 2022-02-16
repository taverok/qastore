package com.taverok.qastore.dto.request

import com.taverok.qastore.domain.DeliveryType
import com.taverok.qastore.domain.PaymentType

data class OrderUpdateRequest (
    var paymentType: PaymentType? = null,
    val deliveryType: DeliveryType? = null,
    val bonuses: Double? = null
)
