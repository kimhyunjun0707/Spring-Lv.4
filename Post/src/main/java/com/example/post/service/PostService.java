package com.example.post.service;

import com.example.post.dto.ApiResult;
import com.example.post.dto.RequestDto;
import com.example.post.dto.ResponseDto;
import com.example.post.entity.Comment;
import com.example.post.entity.Post;
import com.example.post.entity.UserRoleEnum;
import com.example.post.entity.Users;
import com.example.post.jwt.JwtUtil;
import com.example.post.repository.CommentRepository;
import com.example.post.repository.PostRepository;
import com.example.post.repository.UserRepository;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostService {
    //의존성
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final TokenService tokenService;

    //@Transactional
    //데이터베이스를 다룰 때 트랜잭션을 적용하면 데이터 추가, 갱신, 삭제 등으로 이루어진 작업을 처리하던 중 오류가 발생했을 때 모든 작업들을 원상태로 되돌릴 수 있다.
    // 모든 작업들이 성공해야만 최종적으로 데이터베이스에 반영하도록 한다

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
    public List<ResponseDto> getPosts(HttpServletRequest request) {
        Users user = tokenService.checkToken(request);
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
    public ResponseDto getPost(Long id, HttpServletRequest request) {
        Users user = tokenService.checkToken(request);
        Post post = postRepository.findByIdAndUserId(id, user.getId()).orElseThrow(
                () -> new IllegalArgumentException("해당 게시물이 존재하지 않거나 접근할 수 없습니다.")
        );
        return new ResponseDto(post);
        }
    //requestDto로 받아온정보를 엔티티의 생성자에 정보를 넣어주고 저장.
    //같은 정보를 responsDto에 인자로넣어서 반환

    //게시글 작성
    @Transactional
    public ResponseDto create(RequestDto requestDto, HttpServletRequest request) {
        Users user = tokenService.checkToken(request);
            // 요청받은 DTO 로 DB에 저장할 객체 만들기
            Post post = postRepository.saveAndFlush(new Post(requestDto, user));

            return new ResponseDto(post);
        }

    //글 수정
    @Transactional
    //ResponseEntity<?> ResponseEntity의 body에 ResponseDto와 ApiResult 두 가지 타입을 모두 사용할 수 있게했는데
    //이게 맞는지는 잘모르겠습니다.
    public ResponseEntity<?> update(Long id, RequestDto requestDto, HttpServletRequest request) {
        // 토큰의 유효성검사를 통과하고 유저정보를 저장한다
        Users user = tokenService.checkToken(request);
        // post에서 id에맞는 게시글을 찾아온다. 글이없으면 예외처리 메시지반환
        //findByIdAndUserId(id, user.getId()를 findById(id) 로변경
        Post post = postRepository.findById(id).orElseThrow(
                () -> new NullPointerException("해당 글은 존재하지 않습니다.")
        );
        // post의 정보중 유저의 아이디와  위에서 받아온 유저의 정보를 비교해서 같다면 업데이트
        if (post.getUser().getId().equals(user.getId()) ||user.getRole().equals(UserRoleEnum.ADMIN) ) {
            post.update(requestDto);
            postRepository.save(post);
            return ResponseEntity.ok(new ResponseDto(post));
        //아니라면 예외메시지를 클라이언트에게 반환
        } else {
            ApiResult apiResult = ApiResult.builder()
                    .statusCode(HttpStatus.FORBIDDEN.value())
                    .message("작성자만 수정할 수 있습니다.")
                    .build();
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(apiResult);
        }
    }

    //삭제
    public ResponseEntity<ApiResult> delete(Long id, HttpServletRequest request) {
        Users user = tokenService.checkToken(request);
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
