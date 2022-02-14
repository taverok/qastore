package com.taverok.qastore.domain

import java.time.LocalDateTime
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity
data class BasketItem(
    val accountId: Long = 0,
    val productId: Long = 0,
    var quantity: Long = 0
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    var orderId: Long? = null

    var createdAt: LocalDateTime = LocalDateTime.MIN
}