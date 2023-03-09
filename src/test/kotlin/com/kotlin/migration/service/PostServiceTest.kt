package com.kotlin.migration.service

import com.kotlin.migration.domain.Post
import com.kotlin.migration.exception.NotFoundPostException
import com.kotlin.migration.repository.PostRepository
import com.kotlin.migration.request.PostEdit
import com.kotlin.migration.request.PostWrite
import com.kotlin.migration.response.PostDetail
import com.kotlin.migration.response.PostSummary
import mu.KotlinLogging
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.domain.Page
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.crypto.password.PasswordEncoder

private val log = KotlinLogging.logger {}

@SpringBootTest
class PostServiceTest @Autowired constructor(
    private val postService: PostService,
    private val postRepository: PostRepository,
    private val passwordEncoder: PasswordEncoder) {

    @AfterEach
    fun clean() = postRepository.deleteAll()

    @Test
    @DisplayName("게시글 작성")
    fun write() {
        // given
        val request = PostWrite(
            username = "username",
            rawPassword = "password",
            title = "title",
            content = "content")

        // when
        val postId: Long? = postService.write(request)
        val count: Long = postRepository.count()

        // then
        assertThat(count).isEqualTo(1L)

        log.info { "post=${postId}" }
    }

    @Test
    @DisplayName("게시글 단건 조회")
    fun get() {
        // given
        val post = Post(
            username = "username",
            password = encode("password"),
            title = "title",
            content = "content")
        postRepository.save(post)

        // when
        val response: PostDetail = postService.get(post.id!!)

        // then
        assertThat(response.id).isEqualTo(post.id)
        assertThat(response.username).isEqualTo(post.username)
        assertThat(response.title).isEqualTo(post.title)
        assertThat(response.content).isEqualTo(post.content)

        log.info { "id=${response.id}, username=${response.username}, title=${response.title}, content=${response.content}" }
    }

    @Test
    @DisplayName("존재하지 않는 id 조회로 예외 발생")
    fun getException() {
        // expected
        assertThatThrownBy { postService.get(1_000L) }
            .isInstanceOf(NotFoundPostException::class.java)
    }

    @Test
    @DisplayName("페이지 조회")
    fun getPage() {
        for (number in 1 .. 15) {
            val post = Post(
                username = "username ${number}",
                password = encode("password ${number}"),
                title = "title ${number}",
                content = "content ${number}")
            postRepository.save(post)
        }

        // when
        val page1: Page<PostSummary> = postService.getList(1)
        val page2: Page<PostSummary> = postService.getList(2)

        // then
        assertThat(page1.content.size).isEqualTo(10)
        assertThat(page2.content.size).isEqualTo(5)
    }

    @Test
    @DisplayName("작성된 글의 제목과 내용 수정")
    fun edit() {
        // given
        val post = Post(
            username = "username",
            password = encode("password"),
            title = "title",
            content = "content")
        postRepository.save(post)

        val id: Long = post.id!!
        val request = PostEdit(
            title = "update title",
            content = "update content")

        // when
        val editPostId: Long? = postService.edit(id, request)

        // then
        val editPost: Post = postRepository.findByIdOrNull(editPostId) ?: throw RuntimeException()
        assertThat(editPost.title).isEqualTo("update title")
        assertThat(editPost.content).isEqualTo("update content")
    }

    @Test
    @DisplayName("존재하지 않는 id 수정으로 예외 발생")
    fun editException() {
        // given
        val request = PostEdit(
            title = "update title",
            content = "update content")

        // expected
        assertThatThrownBy { postService.edit(1_000L, request) }
            .isInstanceOf(NotFoundPostException::class.java)
    }

    @Test
    @DisplayName("글 삭제")
    fun delete() {
        // given
        val post = Post(
            username = "username",
            password = encode("password"),
            title = "title",
            content = "content")
        postRepository.save(post)

        val id: Long = post.id!!

        // when
        postService.delete(id)
        val count: Long = postRepository.count()

        // then
        assertThat(count).isEqualTo(0L)
    }

    @Test
    @DisplayName("존재하지 않는 id 삭제로 예외 발생")
    fun deleteException() {

        // expected
        assertThatThrownBy { postService.delete(1_000L) }
            .isInstanceOf(NotFoundPostException::class.java)
    }
    
    @Test
    @DisplayName("일치하는 비밀번호")
    fun correctPassword() {
        // given
        val post = Post(
            username = "username",
            password = encode("password"),
            title = "title",
            content = "content")
        postRepository.save(post)

        val id: Long = post.id!!

        // when
        val result: Boolean = postService.isCorrectPassword(id, "password")

        // then
        assertThat(result).isTrue
    }

    @Test
    @DisplayName("일치하지 않는 비밀번호")
    fun incorrectPassword() {
        // given
        val post = Post(
            username = "username",
            password = encode("password"),
            title = "title",
            content = "content")
        postRepository.save(post)

        val id: Long = post.id!!

        // when
        val result: Boolean = postService.isCorrectPassword(id, "incorrect")

        // then
        assertThat(result).isFalse
    }

    @Test
    @DisplayName("id를 Map 형식으로 변환")
    fun convertId() {
        // given
        val id = 1_000L

        // when
        val result: Map<String, Long> = postService.convertIdToMap(id)

        // then
        assertThat(result["id"]).isEqualTo(id)
    }

    ///

    private fun encode(rawPassword: String) =
        passwordEncoder.encode(rawPassword)
}