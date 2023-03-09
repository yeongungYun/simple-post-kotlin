package com.kotlin.migration.controller

import com.kotlin.migration.request.PostEdit
import com.kotlin.migration.request.PostWrite
import com.kotlin.migration.response.PostDetail
import com.kotlin.migration.response.PostSummary
import com.kotlin.migration.service.PostService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus.*
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RequestMapping("/posts")
@RestController
class PostController @Autowired constructor(
    private val postService: PostService
) {

    /*
    글 단건 조회
     */
    @GetMapping("/post/{id}")
    fun get(@PathVariable(name = "id") id: Long): PostDetail =
        postService.get(id)

    /*
    기본 페이지 조회
     */
    @GetMapping("/")
    fun getInitialPage() =
        postService.getList(1)

    /*
    해당 페이지 조회
     */
    @GetMapping("/{page}")
    fun getPage(@PathVariable(name = "page", required = false) page: Int): List<PostSummary> =
        postService.getList(page).content

    /*
    글 작성
     */
    @ResponseStatus(CREATED)
    @PostMapping("/post")
    fun write(@RequestBody postWrite: PostWrite): IdMap {
        val id: Long? = postService.write(postWrite)
        return postService.convertIdToMap(id)
    }

    /*
    글 수정
     */
    @PatchMapping("/posts/{id}")
    fun edit(@PathVariable(name = "id") id: Long, @RequestBody postEdit: PostEdit): IdMap {
        val editPostId: Long? = postService.edit(id, postEdit)
        return postService.convertIdToMap(editPostId)
    }

    /*
    글 삭제
     */
    @DeleteMapping("/post/{id}")
    fun delete(@PathVariable(name = "id") id: Long) =
        postService.delete(id)

    /*
    비밀번호 확인
     */
    @PostMapping("/post/check/{id}")
    fun checkPassword(@PathVariable(name = "id") id: Long, @RequestBody rawPassword: String): ResponseEntity<Unit> =
        if (postService.isCorrectPassword(id, rawPassword)) {
            ResponseEntity(OK)
        }
        else {
            ResponseEntity(UNAUTHORIZED)
        }
}

typealias IdMap = Map<String, Long?>