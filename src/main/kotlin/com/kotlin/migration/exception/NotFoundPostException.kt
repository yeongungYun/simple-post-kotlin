package com.kotlin.migration.exception

class NotFoundPostException : RuntimeException(MESSAGE) {

    companion object {
        private const val MESSAGE: String = "게시글을 찾을 수 없습니다."
    }
}