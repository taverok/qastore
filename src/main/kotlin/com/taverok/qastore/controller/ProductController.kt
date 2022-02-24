package com.taverok.qastore.controller

import com.taverok.qastore.dto.JsonMessage
import com.taverok.qastore.dto.response.ProductListResponse
import com.taverok.qastore.dto.response.ProductResponse
import com.taverok.qastore.service.ProductService
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/products")
class ProductController(
    val productService: ProductService
) {
    @GetMapping
    fun list(): JsonMessage<ProductListResponse> {
        val products = productService.getAllActive()
        val response = ProductListResponse(
            products = products.map { ProductResponse.of(it) }.sortedBy { it.price }
        )

        return JsonMessage.of(response)
    }
}