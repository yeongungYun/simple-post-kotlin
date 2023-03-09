package com.kotlin.migration.service

import com.kotlin.migration.domain.Post
import com.kotlin.migration.exception.NotFoundPostException
import com.kotlin.migration.exception.NullPostIdException
import com.kotlin.migration.repository.PostRepository
import com.kotlin.migration.request.PostEdit
import com.kotlin.migration.request.PostWrite
import com.kotlin.migration.response.PostDetail
import com.kotlin.migration.response.PostSummary
import com.kotlin.migration.util.MapConverter
import jakarta.transaction.Transactional
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

private val log = KotlinLogging.logger {}

@Service
class PostService @Autowired constructor(
    private val postRepository: PostRepository,
    private val passwordEncoder: PasswordEncoder) {

    private val amountPerPage: Int = 10

    @Transactional
    fun write(request: PostWrite): Long? {
        val post: Post = Post(
            username = request.username,
            password = encode(request.rawPassword),
            title = request.title,
            content = request.content)
        postRepository.save(post)
        log.info { "글 작성 id=${post.id}" }

        return post.id
    }

    fun get(id: Long): PostDetail {
        val post: Post = findPost(id)

        log.info { "글 조회 id=${post.id}" }
        return PostDetail(
            id = post.id!!,
            username = post.username,
            title = post.title,
            content = post.content
        )
    }

    fun getList(currentPage: Int): Page<PostSummary> {
        val pageable: Pageable =
            PageRequest.of(currentPage - 1, amountPerPage, Sort.by("id").descending())
        return postRepository.findAll(pageable).map {
            PostSummary(
                id = it.id!!,
                username = it.username,
                title = it.title
            )
        }
    }

    @Transactional
    fun edit(id: Long, request: PostEdit): Long? {
        val post: Post = findPost(id)
        post.updateTitle(request.title)
        post.updateContent(request.content)

        return post.id;
    }

    @Transactional
    fun delete(id: Long) {
        val post: Post = findPost(id)
        postRepository.delete(post)
    }

    fun isCorrectPassword(id: Long, rawPassword: String): Boolean {
        val post: Post = findPost(id)
        return passwordEncoder.matches(rawPassword, post.password)
    }

    fun convertIdToMap(id: Long?): Map<String, Long?> =
        MapConverter.convert("id", id)

    private fun findPost(id: Long): Post {
        val findPost: Post = postRepository.findByIdOrNull(id) ?: throw NotFoundPostException()
        findPost.id ?: throw NullPostIdException()
        return findPost
    }

    private fun encode(rawPassword: String): String =
        passwordEncoder.encode(rawPassword)
}
