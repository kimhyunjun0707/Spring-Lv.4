package com.example.post.service;

import com.example.post.dto.ApiResult;
import com.example.post.dto.RequestDto;
import com.example.post.dto.ResponseDto;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostService {
    //의존성
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    //모든글 조회
    @Transactional
    public List<ResponseDto> getAllPosts() {
        List<Post> posts = postRepository.findAllByOrderByModifiedAtDesc();  //내림차순으로받아오고
        List<ResponseDto> Posts = new ArrayList<>();                        //반환타입을 정해주고새로운 어레이리스트생성
        for (Post post : posts) {                                           //반복문돌면서 생성한곳에 넣어준다
            Posts.add(new ResponseDto(post));
        }
        return Posts;
    }
    //작성자의 글 모두조회
    @Transactional
    public List<ResponseDto> getPosts(Users user) {
        List<ResponseDto> list = new ArrayList<>();
        List<Post> postList;
        postList = postRepository.findAllByUserId(user.getId());

        for (Post post : postList) {
            list.add(new ResponseDto(post));
        }
        return list;
        }

    //글 번호로 단일조회
    @Transactional
    public ResponseDto getPost(Long id,Users user) {

        Post post = postRepository.findByIdAndUserId(id, user.getId()).orElseThrow(
                () -> new IllegalArgumentException("해당 게시물이 존재하지 않거나 접근할 수 없습니다.")
        );
        return new ResponseDto(post);
        }
    //게시글 작성
    @Transactional
    public ResponseDto create(RequestDto requestDto, Users user) {
            Post post = postRepository.saveAndFlush(new Post(requestDto, user));

            return new ResponseDto(post);
        }

    //글 수정
    @Transactional
    public ResponseEntity<?> update(Long id, RequestDto requestDto,Users user) {
        Post post = postRepository.findById(id).orElseThrow(
                () -> new NullPointerException("해당 글은 존재하지 않습니다.")
        );
        if (post.getUser().getId().equals(user.getId()) ||user.getRole().equals(UserRoleEnum.ADMIN) ) {
            post.update(requestDto);
            postRepository.save(post);
            return ResponseEntity.ok(new ResponseDto(post));
        } else {
            ApiResult apiResult = ApiResult.builder()
                    .statusCode(HttpStatus.FORBIDDEN.value())
                    .message("작성자만 수정할 수 있습니다.")
                    .build();
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(apiResult);
        }
    }
    //삭제
    public ResponseEntity<ApiResult> delete(Long id,Users user) {

        Optional<Post> optionalPost = postRepository.findById(id);
        ApiResult apiResult;

        if (!optionalPost.isPresent()) {
            apiResult = ApiResult.builder()
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .message("해당 게시물이 존재하지 않습니다.")
                    .build();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiResult);
        }
        Post post = optionalPost.get();

        if (post.getUser().getId().equals(user.getId())||user.getRole().equals(UserRoleEnum.ADMIN)) {
            List<Comment> comments = commentRepository.findAllByPostId(id); //삭제전에 post id에있는 댓글을 전부찾고
            commentRepository.deleteAll(comments);                          //삭제한다.
            postRepository.deleteById(id);

            apiResult = ApiResult.builder()
                    .statusCode(HttpStatus.OK.value())
                    .message("게시물이 삭제되었습니다.")
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
