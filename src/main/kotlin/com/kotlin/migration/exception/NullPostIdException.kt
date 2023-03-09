package com.kotlin.migration.exception

class NullPostIdException : RuntimeException(MESSAGE) {

    companion object {
        private const val MESSAGE: String = "id가 null입니다."
    }
}