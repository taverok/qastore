package com.taverok.qastore.dto.request

import javax.validation.constraints.Pattern

data class AccountUpdateRequest(
    @Pattern(regexp = "[0-9a-zA-ZА-Яа-я ]*")
    val city: String?,
    @Pattern(regexp = "[0-9a-zA-ZА-Яа-я ]*")
    val street: String?,
    @Pattern(regexp = "[0-9]*")
    val house: String?,
    val apartment: String?
)
