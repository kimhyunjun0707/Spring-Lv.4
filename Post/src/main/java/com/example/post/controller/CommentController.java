package com.example.post.controller;


import com.example.post.dto.ApiResult;
import com.example.post.dto.CommentRequestDto;
import com.example.post.dto.CommentResponseDto;
import com.example.post.dto.ResponseDto;
import com.example.post.security.UserDetailsImpl;
import com.example.post.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
//@RequestMapping("/api/comment")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    //댓글 작성
    @PostMapping("/api/comment")
    public CommentResponseDto createPost(@RequestBody CommentRequestDto commentRequestDto, @AuthenticationPrincipal UserDetailsImpl userDetails){
        System.out.println("----------comment create----------");

        return commentService.create(commentRequestDto, userDetails.getUser());
    }
    //댓글 수정
    @PutMapping("/api/comment/{id}")
    public ResponseEntity<?> update(@PathVariable Long id,@RequestBody CommentRequestDto commentRequestDto,@AuthenticationPrincipal UserDetailsImpl userDetails){
        System.out.println("----------no."+id+ "update----------");

        return commentService.update(id,commentRequestDto, userDetails.getUser());

    }

    //댓글 삭제
    @DeleteMapping("/api/comment/{id}")
    public ResponseEntity<ApiResult> delete(@PathVariable Long id,@AuthenticationPrincipal UserDetailsImpl userDetails){
        System.out.println("----------no."+id+" delete----------");

        return commentService.delete(id, userDetails.getUser());

    }


}
