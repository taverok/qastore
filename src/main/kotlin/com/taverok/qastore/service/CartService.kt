package com.taverok.qastore.service

import com.taverok.qastore.domain.Account
import com.taverok.qastore.domain.CartItem
import com.taverok.qastore.dto.response.CartItemResponse
import com.taverok.qastore.dto.response.ProductResponse
import com.taverok.qastore.repository.CartRepository
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import javax.transaction.Transactional

@Service
class CartService(
    private val cartRepository: CartRepository,
    private val productService: ProductService
) {
    @Transactional
    fun add(productId: Long, account: Account) {
        val item = getBasketItem(productId, account)
            ?: CartItem(
                accountId = account.id!!,
                productId = productId,
                quantity = 0,
            ).also {
                it.createdAt = LocalDateTime.now()
            }

        item.quantity++

        cartRepository.save(item)
    }

    @Transactional
    fun remove(productId: Long, account: Account) {
        val item = getBasketItem(productId, account)

        when {
            item == null -> {
                return
            }
            item.quantity <= 1 -> {
                cartRepository.delete(item)
            }
            else -> {
                item.quantity--
                cartRepository.save(item)
            }
        }
    }

    fun getAll(account: Account): List<CartItem> {
        return cartRepository.findAllByAccountIdAndOrderIdIsNull(account.id!!)
    }

    fun toResponse(item: CartItem): CartItemResponse {
        val product = productService.getByIdOrThrow(item.productId)
        return CartItemResponse(
            product = ProductResponse.of(product),
            totalPrice = item.quantity * product.price,
            quantity = item.quantity
        )
    }

    private fun getBasketItem(
        productId: Long,
        account: Account
    ): CartItem? {
        productService.getByIdOrThrow(productId)
        val currentBasket = getAll(account)

        return currentBasket.firstOrNull { it.productId == productId }
    }

    fun save(items: List<CartItem>) {
        cartRepository.saveAll(items)
    }

}