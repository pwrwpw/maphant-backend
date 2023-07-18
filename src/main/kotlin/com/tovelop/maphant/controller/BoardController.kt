package com.tovelop.maphant.controller

import com.tovelop.maphant.dto.BoardDTO
import com.tovelop.maphant.service.BoardService
import com.tovelop.maphant.type.response.Response
import com.tovelop.maphant.type.response.ResponseUnit
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/board")
class BoardController(@Autowired val boardService: BoardService) {

    @GetMapping("/main")
    fun readBoard(): ResponseEntity<ResponseUnit> {
        // 보드 메인 (선택한 장르의 게시글)
        // 정렬(추천수, 생성 일자)
        // 추천수, 작성자(익명인지), 수정 일자, 제목,
        // return: json
        return ResponseEntity.ok(Response.stateOnly(true))
    }

    @PostMapping("/recommend")
    fun recommendHandle(@RequestBody post: BoardDTO): ResponseEntity<ResponseUnit> {
        // 추천수 증가
        // return: json
        return ResponseEntity.ok(Response.stateOnly(true))
    }

    @PostMapping("/read")
    fun readPost(@RequestBody post: BoardDTO): ResponseEntity<ResponseUnit> {
        // 한 개의 게시글 읽어오기
        // 제목, 내용, 댓글, 추천수, 수정 일자, 작성자가 로그인한 사람과 같은지 확인
        // return: json
        return ResponseEntity.ok(Response.stateOnly(true))
    }

    @DeleteMapping("/post/delete")
    fun deletePost(@RequestBody post: BoardDTO): ResponseEntity<ResponseUnit> {
        // 게시글 삭제
        // 관리자 권한 확인(관리자는 모든 게시글 삭제 가능)
        // 본인 게시글 인지 확인

        boardService.deletePost(post.id)
        return ResponseEntity.ok(Response.stateOnly(true))
    }

    @PostMapping("/create")
    fun createPost(@RequestBody post: BoardDTO): ResponseEntity<ResponseUnit> {
        // 제목 내용 빈칸인지 확인
        if (post.title.isNotBlank() && post.body.isNotBlank()) {
            boardService.createPost(post)
            return ResponseEntity.ok(Response.stateOnly(true))
        } else {
            return ResponseEntity.ok(Response.stateOnly(false)) // 제목 또는 내용이 빈칸인 경우 실패 응답을 반환합니다.
        }
    }

    @PutMapping("/update")
    fun updatePost(@RequestBody post: BoardDTO): ResponseEntity<ResponseUnit> {
        // 현재 로그인 한 사용를 가져옴
        // 게시글 읽어오기
        //  val rePost = boardService.readPost(post.id)
        // 제목 내용 빈칸인지 확인
        // 본인 게시글 인지 확인
        // 관리자 권한 확인 (관리자는 수정이 가능한가?)
        // 수정
        return ResponseEntity.ok(Response.stateOnly(true))
    }
}
