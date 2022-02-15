package com.taverok.qastore.repository

import com.taverok.qastore.domain.Order
import org.springframework.data.jpa.repository.JpaRepository

interface OrderRepository: JpaRepository<Order, Long> {
    fun findAllByAccountId(accountId: Long): List<Order>
}