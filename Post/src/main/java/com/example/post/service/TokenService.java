package com.example.post.service;

import com.example.post.entity.Users;
import com.example.post.jwt.JwtUtil;
import com.example.post.repository.UserRepository;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

@Service
@RequiredArgsConstructor
public class TokenService {
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;


    public Users checkToken(HttpServletRequest request) {
        String token = jwtUtil.resolveToken(request);   //HTTP헤더에서 jwt토큰을 추출해서 token변수에저장
        //토큰이 없는게아니라면(토큰이있다면)
        if (token != null) {
            Claims claims;
            // 토큰의 기간이 만료되었거나 변조한토큰인지 검증을한다(토큰이 올바른 형식인가?)
            if (jwtUtil.validateToken(token)) {
                claims = jwtUtil.getUserInfoFromToken(token);
            } else {
                throw new IllegalArgumentException("변조되거나 올바른경로의 토큰이아닙니다.");
            }
            // 토큰에서 가져온 사용자 정보를 사용하여 DB 조회
            return userRepository.findByUsername(claims.getSubject()).orElseThrow(
                    () -> new IllegalArgumentException("해당 사용자를 찾을 수 없습니다")
            );
        }else{  //토큰은 없다 헤더에서 보내진 토큰이없다
            throw new IllegalArgumentException("토큰이 유효하지 않습니다.");
        }
    }

}
