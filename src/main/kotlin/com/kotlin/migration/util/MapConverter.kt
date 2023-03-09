package com.kotlin.migration.util

import org.springframework.stereotype.Component

@Component
open class MapConverter {

    companion object {
        fun <T>convert(key: String, value: T): Map<String, T> {
            return mapOf(key to value)
        }
    }
}