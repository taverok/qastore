package com.taverok.qastore.dto.request

data class AccountCreateRequest(
    val username: String,
    val email: String,
    val pass: String
)
