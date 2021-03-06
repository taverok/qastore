package com.taverok.qastore.service

import com.taverok.qastore.domain.Product
import com.taverok.qastore.exception.NotFoundException
import com.taverok.qastore.repository.ProductRepository
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

@Service
class ProductService(
    private val productRepository: ProductRepository
) {
    private var products : List<Product> = emptyList()

    fun getAllActive(): List<Product>{
        return products.filter { it.isActive }
    }

    fun getByIdOrThrow(productId: Long): Product{
        return products.firstOrNull{it.id == productId}
            ?: throw NotFoundException("product not found")
    }

    @Scheduled(fixedDelay = 60_000)
    fun updateCache(){
        products = productRepository.findAll()
    }
}