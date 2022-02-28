package com.taverok.qastore.dto.request

import javax.validation.constraints.Pattern

data class AccountCreateRequest(
    val username: String,
    @get:Pattern(regexp = ".*@.*\\..*")
    val email: String,
    val pass: String
)
