package com.taverok.qastore.dto.response

import com.taverok.qastore.domain.Product

data class ProductResponse(
    var name: String,
    var id: Long,

    var price: Double,
    var description: String,
    var bannerUrl: String
){
    companion object{
        fun of(p: Product): ProductResponse{
            return ProductResponse(
                name = p.name,
                id = p.id ?: 0,
                price = p.price,
                description = p.description,
                bannerUrl = p.bannerUrl
            )
        }
    }
}