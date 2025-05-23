package com.example.checkscam.rest;

import com.example.checkscam.dto.LoginDTO;
import com.example.checkscam.dto.ResLoginDTO;
import com.example.checkscam.dto.ResCreateUserDTO;
import com.example.checkscam.dto.request.UserRegistrationDTO;
import com.example.checkscam.dto.response.ApiResponse;
import com.example.checkscam.entity.User;
import com.example.checkscam.service.UserService;
import com.example.checkscam.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;


@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class RestAuthController {
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final UserService userService;
    private final SecurityUtil securityUtil;

    @Value("${checkscam.jwt.refresh-token-validity-in-seconds}")
    private long refreshTokenExpiration;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<ResCreateUserDTO>> register(@Valid @RequestBody UserRegistrationDTO registrationDTO) {
        try {
            ResCreateUserDTO newUser = userService.registerUser(registrationDTO);
            return ResponseEntity.status(201)
                    .body(ApiResponse.success("User registered successfully", newUser));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Registration failed: " + e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<ResLoginDTO>> login(@Valid @RequestBody LoginDTO loginDto) {
        try {
            System.out.println("Login attempt for user: " + loginDto.getUsername());
            
            // Nạp input gồm username/password vào Security
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                    loginDto.getUsername(), loginDto.getPassword());

            // xác thực người dùng => cần viết hàm loadUserByUsername
            Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
            System.out.println("Authentication successful for: " + authentication.getName());

            // create a token
            String access_token = this.securityUtil.createAccessToken(authentication);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            System.out.println("Access token created: " + access_token.substring(0, 20) + "...");

            ResLoginDTO res = new ResLoginDTO();
            User currentUserDB = this.userService.handleGetUserByUsername(loginDto.getUsername());
            if (currentUserDB != null) {
                String userRole = "USER"; // Default role
                if (currentUserDB.getRoles() != null && !currentUserDB.getRoles().isEmpty()) {
                    userRole = currentUserDB.getRoles().iterator().next().getName().toString();
                }
                
                ResLoginDTO.UserLogin userLogin = new ResLoginDTO.UserLogin(
                        currentUserDB.getId(),
                        currentUserDB.getEmail(),
                        currentUserDB.getName(),
                        userRole
                );
                res.setUser(userLogin);
            }
            res.setAccessToken(access_token);

            // create refresh token
            String refresh_token = this.securityUtil.createRefreshToken(loginDto.getUsername(), res);

            // update user
            this.userService.updateUserToken(refresh_token, loginDto.getUsername());

            // set cookies
            ResponseCookie resCookies = ResponseCookie
                    .from("refresh_token", refresh_token)
                    .httpOnly(true)
                    .secure(false) // Set to false for localhost
                    .path("/")
                    .maxAge(refreshTokenExpiration)
                    .build();

            System.out.println("Login successful for user: " + loginDto.getUsername());
            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, resCookies.toString())
                    .body(ApiResponse.success("Login successful", res));
                    
        } catch (Exception e) {
            System.out.println("Login failed for user: " + loginDto.getUsername() + ", error: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Login failed: " + e.getMessage()));
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<ResLoginDTO>> refreshToken(@CookieValue(name = "refresh_token", defaultValue = "fake-token") String refreshToken) {
        try {
            if (refreshToken.equals("fake-token")) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("Refresh token not found"));
            }

            // TODO: Add refresh token validation logic here
            // For now, return error
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Refresh token validation not implemented"));
                    
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Token refresh failed: " + e.getMessage()));
        }
    }

    @GetMapping("/account")
    public ResponseEntity<ApiResponse<ResLoginDTO.UserLogin>> getAccount() {
        try {
            String email = SecurityUtil.getCurrentUserLogin().orElse("");

            if (email.isEmpty()) {
                return ResponseEntity.status(401)
                        .body(ApiResponse.error("User not authenticated"));
            }

            User currentUserDB = this.userService.handleGetUserByUsername(email);
            if (currentUserDB == null) {
                return ResponseEntity.status(401)
                        .body(ApiResponse.error("User not found"));
            }

            String userRole = "USER"; // Default role
            if (currentUserDB.getRoles() != null && !currentUserDB.getRoles().isEmpty()) {
                userRole = currentUserDB.getRoles().iterator().next().getName().toString();
            }

            ResLoginDTO.UserLogin userLogin = new ResLoginDTO.UserLogin(
                    currentUserDB.getId(),
                    currentUserDB.getEmail(),
                    currentUserDB.getName(),
                    userRole);

            return ResponseEntity.ok()
                    .body(ApiResponse.success("User account retrieved", userLogin));
                    
        } catch (Exception e) {
            return ResponseEntity.status(401)
                    .body(ApiResponse.error("Failed to get account: " + e.getMessage()));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<String>> logout() {
        try {
            String email = SecurityUtil.getCurrentUserLogin().orElse("");

            if (email.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("Access Token không hợp lệ"));
            }

            // update refresh token = null
            this.userService.updateUserToken(null, email);

            // remove refresh token cookie
            ResponseCookie deleteSpringCookie = ResponseCookie.from("refresh_token", null)
                    .httpOnly(true)
                    .secure(true)
                    .path("/")
                    .maxAge(0)
                    .build();

            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, deleteSpringCookie.toString())
                    .body(ApiResponse.success("Logout successful", null));
                    
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Logout failed: " + e.getMessage()));
        }
    }
}
