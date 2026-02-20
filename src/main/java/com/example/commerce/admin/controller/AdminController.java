package com.example.commerce.admin.controller;

import com.example.commerce.admin.dto.*;
import com.example.commerce.admin.service.AdminService;
import com.example.commerce.global.common.CommonResponseDTO;
import com.example.commerce.global.common.CommonResponseHandler;
import com.example.commerce.global.common.SuccessCode;
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
    ResponseEntity<CommonResponseDTO<SignupResponse>> signup(@Valid @RequestBody SignupRequest request){
        SignupResponse response = adminService.signup(request);

        return CommonResponseHandler.success(SuccessCode.ADMIN_SIGNUP, response);
        //return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    ResponseEntity<CommonResponseDTO<LoginResponse>> login(
            @Valid @RequestBody LoginRequest request,
            HttpSession session
    ){
        LoginResponse response= adminService.login(request);

        SessionAdmin sessionAdmin = new SessionAdmin(response);

        session.setAttribute("loginAdmin", sessionAdmin);
        session.setMaxInactiveInterval(120);
        return CommonResponseHandler.success(SuccessCode.LOGIN_SUCCESSFUL, response);
        //return ResponseEntity.status(HttpStatus.OK).body(response);
    }

}
