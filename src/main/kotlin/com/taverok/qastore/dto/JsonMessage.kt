package com.taverok.qastore.dto

data class JsonMessage<T>(
    val content: T? = null,
    val errors: List<String>? = null
) {
    companion object {
        fun <T> of(message: T): JsonMessage<T> {
            return JsonMessage(message, emptyList())
        }

        fun <T> withError(error: String): JsonMessage<T> {
            return JsonMessage(null, listOf(error))
        }
    }
}

val OK = JsonMessage.of(true)