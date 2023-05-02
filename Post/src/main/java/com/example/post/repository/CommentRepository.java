package com.example.post.repository;

import com.example.post.entity.Comment;
import com.example.post.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment,Long> {
    Optional<Comment> findByIdAndUserId(Long commentId , Long userId);
    List<Comment> findAllByPostId(Long postId);//게시글삭제시 댓글전체조회

}
