package com.taverok.qastore.domain

import com.taverok.qastore.domain.DeliveryType.PICKUP
import com.taverok.qastore.domain.PaymentType.CARD_PREPAYMENT
import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Table(name = "orders")
data class Order(
    val accountId: Long = 0,
    @Enumerated(EnumType.STRING)
    var paymentType: PaymentType = CARD_PREPAYMENT,
    @Enumerated(EnumType.STRING)
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
    @Enumerated(EnumType.STRING)
    var status: OrderStatus = OrderStatus.PAYMENT_REQUIRED

    var createdAt: LocalDateTime = LocalDateTime.MIN
    var payedAt: LocalDateTime? = null
}
