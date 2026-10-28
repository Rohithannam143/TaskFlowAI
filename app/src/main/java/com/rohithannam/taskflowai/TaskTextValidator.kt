package com.rohithannam.taskflowai

object TaskTextValidator {
    const val MAX_LENGTH = 120

    fun normalize(input: String): String? {
        val value = input.trim().replace(Regex("\\s+"), " ")
        if (value.isBlank()) return null
        return value.take(MAX_LENGTH)
    }
}
