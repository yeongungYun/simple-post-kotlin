package com.kotlin.migration.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id

@Entity
class Post(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(nullable = false)
    val username: String,

    @Column(nullable = false)
    val password: String,

    @Column(nullable = false)
    var title: String,

    @Column(nullable = false)
    var content: String) {

    fun updateTitle(updatedTitle: String) {
        title = updatedTitle
    }

    fun updateContent(updatedContent: String) {
        content = updatedContent
    }
}