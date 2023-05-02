package com.example.post.dto;

import com.example.post.entity.Comment;
import com.example.post.entity.Post;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class ResponseDto {
    //클라이언트에게 보내줄 정보들

    private Long postId;

    private String username;
    private String title;
    private String content;

    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    private List<CommentResponseDto> commentList ;


    public ResponseDto(Post post) {
        this.createdAt = post.getCreatedAt();
        this.modifiedAt = post.getModifiedAt();
        this.postId = post.getId();
        this.title = post.getTitle();
        this.content = post.getContent();
        this.username = post.getUser().getUsername();
        List<Comment> comments = post.getComment(); //post엔티티에서 가저와서 comments에저장
        List<CommentResponseDto> cDtos = new ArrayList<>();//객체들을 저장할 ArrayList를 생성
        for (Comment comment : comments) {
            CommentResponseDto commentResponseDto = new CommentResponseDto(comment);
            cDtos.add(commentResponseDto); //cDtos에 저장
        }
        this.commentList = cDtos;
    }



}
