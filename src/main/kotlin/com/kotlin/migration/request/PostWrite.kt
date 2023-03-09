package com.kotlin.migration.request

data class PostWrite(
    val username: String,
    val rawPassword: String,
    val title: String,
    val content: String)
