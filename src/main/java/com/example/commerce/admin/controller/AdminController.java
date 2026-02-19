package com.example.commerce.admin.controller;

import com.example.commerce.admin.dto.*;
import com.example.commerce.admin.service.AdminService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AdminController {
    private final AdminService adminService;

    @PostMapping("/signup")
    ResponseEntity<SignupResponse> signup(@Valid @RequestBody SignupRequest request){
        SignupResponse response = adminService.signup(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    ResponseEntity<LoginResponse> login(
            @Valid @RequestBody LoginRequest request,
            HttpSession session
    ){
        LoginResponse response= adminService.login(request);

        SessionAdmin sessionAdmin = new SessionAdmin(response);

        session.setAttribute("loginAdmin", sessionAdmin);
        session.setMaxInactiveInterval(120);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

}
