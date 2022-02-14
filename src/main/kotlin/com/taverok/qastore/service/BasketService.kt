package com.taverok.qastore.service

import com.taverok.qastore.domain.Account
import com.taverok.qastore.domain.BasketItem
import com.taverok.qastore.dto.response.BasketItemResponse
import com.taverok.qastore.dto.response.ProductResponse
import com.taverok.qastore.repository.BasketRepository
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import javax.transaction.Transactional

@Service
class BasketService(
    private val basketRepository: BasketRepository,
    private val productService: ProductService
) {
    @Transactional
    fun add(productId: Long, account: Account) {
        val item = getBasketItem(productId, account)
            ?: BasketItem(
                accountId = account.id!!,
                productId = productId,
                quantity = 0,
            ).also {
                it.createdAt = LocalDateTime.now()
            }

        item.quantity++

        basketRepository.save(item)
    }

    @Transactional
    fun remove(productId: Long, account: Account) {
        val item = getBasketItem(productId, account)

        when {
            item == null -> {
                return
            }
            item.quantity <= 1 -> {
                basketRepository.delete(item)
            }
            else -> {
                item.quantity--
                basketRepository.save(item)
            }
        }
    }

    fun getAll(account: Account): List<BasketItem> {
        return basketRepository.findAllByAccountIdAndOrderIdIsNull(account.id!!)
    }

    fun toResponse(item: BasketItem): BasketItemResponse {
        val product = productService.getByIdOrThrow(item.productId)
        return BasketItemResponse(
            product = ProductResponse.of(product),
            totalPrice = item.quantity * product.price,
            quantity = item.quantity
        )
    }

    private fun getBasketItem(
        productId: Long,
        account: Account
    ): BasketItem? {
        productService.getByIdOrThrow(productId)
        val currentBasket = getAll(account)

        return currentBasket.firstOrNull { it.productId == productId }
    }

}