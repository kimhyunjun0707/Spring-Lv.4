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


    @PostMapping("/auth/signup-page")
    public ResponseEntity<ApiResult> signupPage(@Valid @RequestBody SignupRequestDto signupRequestDto,BindingResult bindingResult) {

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
    @ResponseBody
    @PostMapping("/auth/login-page")
    public ResponseEntity<ApiResult> loginPage(@RequestBody SignupRequestDto signupRequestDto, HttpServletResponse response){

        return userService.login(signupRequestDto,response);

    }



}
