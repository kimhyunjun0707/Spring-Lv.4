package com.example.post.service;

import com.example.post.dto.*;
import com.example.post.entity.Comment;
import com.example.post.entity.Post;
import com.example.post.entity.UserRoleEnum;
import com.example.post.entity.Users;
import com.example.post.repository.CommentRepository;
import com.example.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor

public class CommentService {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;


    //댓글 작성
    @Transactional
    public CommentResponseDto create(CommentRequestDto commentRequestDto,Users user) {
        Post post = postRepository.findById(commentRequestDto.getPostId())
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 없습니다. "));
        Comment comment = commentRepository.saveAndFlush(new Comment(commentRequestDto,post,user));
        return new CommentResponseDto(comment);//Comment response 만들기
    }

    //댓글 수정
    @Transactional
    public ResponseEntity<?> update(Long id,CommentRequestDto commentRequestDto,Users user) {
        Comment comment = commentRepository.findById(id).orElseThrow(
                () -> new NullPointerException("해당 댓글은 존재하지 않습니다.")
        );
        if (comment.getUser().getId().equals(user.getId()) ||user.getRole().equals(UserRoleEnum.ADMIN) ) {
            comment.update(commentRequestDto);
            commentRepository.save(comment);
            return ResponseEntity.ok(new CommentResponseDto(comment));
        }else{
            ApiResult apiResult = ApiResult.builder()
                    .statusCode(HttpStatus.FORBIDDEN.value())
                    .message("작성자만 수정할 수 있습니다.")
                    .build();
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(apiResult);
        }
    }

    //댃글 삭제
    @Transactional
    public ResponseEntity<ApiResult> delete(Long id, Users user) {
        Optional<Comment> optionalComment = commentRepository.findById(id);
        ApiResult apiResult;

        if (optionalComment.isEmpty()) {
            apiResult = ApiResult.builder()
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .message("해당 댓글이 존재하지 않습니다.")
                    .build();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiResult);
        }
        Comment comment = optionalComment.get();
        if (comment.getUser().getId().equals(user.getId())||user.getRole().equals(UserRoleEnum.ADMIN)) {
            commentRepository.deleteById(id);

            apiResult = ApiResult.builder()
                    .statusCode(HttpStatus.OK.value())
                    .message("댓글이 삭제되었습니다.")
                    .build();
            return ResponseEntity.status(HttpStatus.OK).body(apiResult);
        } else {
            apiResult = ApiResult.builder()
                    .statusCode(HttpStatus.FORBIDDEN.value())
                    .message("작성자만 삭제할 수 있습니다.")
                    .build();
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(apiResult);
        }
    }




}
