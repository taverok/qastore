package com.taverok.qastore.dto.request

import org.hibernate.validator.constraints.Length
import java.time.LocalDate
import javax.validation.Valid
import javax.validation.constraints.Future
import javax.validation.constraints.Max
import javax.validation.constraints.Min

data class PaymentRequest(
    val orderId: Long,
    @get:Valid val card: PaymentCard
)

data class PaymentCard(
    val no: String,

    @get:Future
    val validThru: LocalDate,

    @get:Min(0)
    @get:Max(999)
    val cvv: Long,

    @get:Length(min = 5)
    val cardholder: String
)