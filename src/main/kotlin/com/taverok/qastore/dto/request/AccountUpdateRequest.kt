package com.taverok.qastore.dto.request

import javax.validation.constraints.Pattern

data class AccountUpdateRequest(
    @get:Pattern(regexp = "[0-9a-zA-ZА-Яа-я ]*")
    val city: String?,
    @get:Pattern(regexp = "[0-9a-zA-ZА-Яа-я ]*")
    val street: String?,
    @get:Pattern(regexp = "[0-9]*")
    val house: String?,
    val apartment: String?,
    @get:Pattern(regexp = "[0-9 +()-]*")
    val phone: String?
)
