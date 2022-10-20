package com.dsg.wardstudy.domain.user.controller;

import com.dsg.wardstudy.domain.user.dto.LoginDto;
import com.dsg.wardstudy.domain.user.dto.SignUpRequest;
import com.dsg.wardstudy.domain.user.service.LoginService;
import com.dsg.wardstudy.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Log4j2
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final LoginService loginService;

    // 회원가입(register)
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Valid @RequestBody SignUpRequest signUpDto) {

        log.info("users signup, signUpDto: {}", signUpDto);
        LoginDto loginDto = userService.signUp(signUpDto);
        return new ResponseEntity<>(loginDto, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDto loginDto) {
        LoginDto findUserDto = userService.getByEmailAndPassword(loginDto.getEmail(), loginDto.getPassword());

        loginService.loginUser(findUserDto.getId());

        return ResponseEntity.ok("login success!");
    }

    @PostMapping("/logout")
    public ResponseEntity<?> login() {
        loginService.logoutUser();

        return ResponseEntity.ok("logout success!");
    }
}
