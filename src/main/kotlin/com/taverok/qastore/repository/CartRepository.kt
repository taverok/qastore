package com.taverok.qastore.repository

import com.taverok.qastore.domain.CartItem
import org.springframework.data.jpa.repository.JpaRepository

interface CartRepository: JpaRepository<CartItem, Long>{
    fun findAllByAccountIdAndOrderIdIsNull(accountId: Long): List<CartItem>
}