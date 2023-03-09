package com.kotlin.migration.repository

import com.kotlin.migration.domain.Post
import org.springframework.data.jpa.repository.JpaRepository

interface PostRepository : JpaRepository<Post, Long> {
}