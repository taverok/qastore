package com.taverok.qastore.dto.response

import java.time.LocalDateTime

data class AccountResponse(
    val email: String,
    val username: String,
    val bonuses: Double,
    val createdAt: LocalDateTime,
    val phone: String
)
