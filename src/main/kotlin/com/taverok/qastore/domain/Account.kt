package com.taverok.qastore.domain

import java.time.LocalDateTime
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id


@Entity
data class Account(
    var username: String = "",
    var email: String = "",
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
    var isActive: Boolean = true
    var pass: String = ""

    var bonuses: Double = .0
    var phone: String = ""

    var createdAt: LocalDateTime = LocalDateTime.MIN
}