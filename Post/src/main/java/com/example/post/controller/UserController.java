package com.example.post.controller;

import com.example.post.dto.ApiResult;
import com.example.post.dto.SignupRequestDto;
import com.example.post.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.BindingResult;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@RequiredArgsConstructor
@RequestMapping("/api/user")
@RestController
public class UserController {

    private final UserService userService;


    @PostMapping("/auth/signup")
    public ResponseEntity<ApiResult> signup(@Valid @RequestBody SignupRequestDto signupRequestDto,BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            String errorMessage = bindingResult.getAllErrors().get(0).getDefaultMessage();
            ApiResult apiResult = ApiResult.builder()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .message(errorMessage)
                    .build();
            return ResponseEntity.badRequest().body(apiResult);
        } else {
            return userService.signup(signupRequestDto);
        }
    }

    //Status code 200:HttpStatus.OK.value()
    //200코드는 요청이 성공적으로 처리되었음을 나타냄
    //Status code 201:HttpStatus.CREATED.value()
    //201코드는 Created상태를 나타내며 새로운 리소스가 성공적으로 생성되었음을 나타냄

    @ResponseBody
    @PostMapping("/auth/login")
    public ResponseEntity<ApiResult> login(@RequestBody SignupRequestDto signupRequestDto, HttpServletResponse response){

        return userService.login(signupRequestDto,response);

    }


}
