package com.example.post.service;

import com.example.post.dto.ApiResult;
import com.example.post.dto.SignupRequestDto;
import com.example.post.entity.UserRoleEnum;
import com.example.post.entity.Users;
import com.example.post.jwt.JwtUtil;
import com.example.post.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

@Service
@RequiredArgsConstructor

public class UserService {


    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private static final String ADMIN_TOKEN = "123";

    @Transactional

    public ResponseEntity<ApiResult> signup(SignupRequestDto signupRequestDto) {
        String username = signupRequestDto.getUsername();
        String password = signupRequestDto.getPassword();
        Optional<Users> found = userRepository.findByUsername(username);
        ApiResult apiResult;
        UserRoleEnum role = UserRoleEnum.USER;
        //isPresent : Optional객체가 값을 가지고있는지 확이하는 메서드

        if (found.isPresent()) {
            apiResult = ApiResult.builder()
                    .statusCode(HttpStatus.CONFLICT.value())
                    .message("중복된 아이디가 존재합니다")
                    .build();
            return ResponseEntity.status(HttpStatus.CONFLICT).body(apiResult);
        }
        if(signupRequestDto.isAdmin()) {
            if(!signupRequestDto.getAdminToken().equals(ADMIN_TOKEN)){
                apiResult = ApiResult.builder()
                        .statusCode(HttpStatus.UNAUTHORIZED.value())
                        .message("관리자 암호가 틀려 등록이 불가능합니다")
                        .build();
                        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(apiResult);
                  }
            role= UserRoleEnum.ADMIN;
            }

        Users user = new Users(username, password,role);
        userRepository.save(user);
        apiResult = ApiResult.builder()
                .statusCode(HttpStatus.OK.value())
                .message("회원 가입 성공")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(apiResult);
    }


    public ResponseEntity<ApiResult> login(SignupRequestDto signupRequestDto, HttpServletResponse response) {
        String username = signupRequestDto.getUsername();
        String password = signupRequestDto.getPassword();
        ApiResult apiResult;
        Optional<Users> optionalUser = userRepository.findByUsername(username);

        if (!optionalUser.isPresent()) {
            apiResult = ApiResult.builder()
                    .statusCode(HttpStatus.CONFLICT.value())
                    .message("등록된 사용자가 없습니다.")
                    .build();
            return ResponseEntity.status(HttpStatus.CONFLICT).body(apiResult);
        }
        Users user = optionalUser.get();

        if (!user.getPassword().equals(password)) {
            apiResult = ApiResult.builder()
                    .statusCode(HttpStatus.UNAUTHORIZED.value())
                    .message("비밀번호가 일치하지 않습니다.")
                    .build();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(apiResult);
        }
        response.addHeader(JwtUtil.AUTHORIZATION_HEADER,jwtUtil.createToken(user.getUsername()));
        apiResult = ApiResult.builder()
                .statusCode(HttpStatus.OK.value())
                .message("로그인 성공")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(apiResult);
    }
}
