package com.taverok.qastore.repository

import com.taverok.qastore.domain.BasketItem
import org.springframework.data.jpa.repository.JpaRepository

interface BasketRepository: JpaRepository<BasketItem, Long>{
    fun findAllByAccountIdAndOrderIdIsNull(accountId: Long): List<BasketItem>
}