package com.taverok.qastore.domain

import com.taverok.qastore.domain.DeliveryType.PICKUP
import com.taverok.qastore.domain.PaymentType.CARD_PREPAYMENT
import java.time.LocalDateTime
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "orders")
data class Order(
    val accountId: Long = 0,
    var paymentType: PaymentType = CARD_PREPAYMENT,
    val deliveryType: DeliveryType = PICKUP,
    val amount: Double =.0,
    val spentBonuses: Double = .0,
    val earnedBonuses: Double = .0,
    val deliveryPrice: Double =.0,
    val total: Double =.0
){
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    var address: String = ""
    var paymentStatus: PaymentStatus = PaymentStatus.PAYMENT_REQUIRED

    var createdAt: LocalDateTime = LocalDateTime.MIN
}
