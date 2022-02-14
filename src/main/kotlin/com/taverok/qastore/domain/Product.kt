package com.taverok.qastore.domain

import java.time.LocalDateTime
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity
data class Product(
    var name: String = ""
){
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    var price: Double = .0
    var description: String = ""
    var bannerUrl: String = ""

    var isActive: Boolean = true
    var createdAt: LocalDateTime = LocalDateTime.MIN
}
