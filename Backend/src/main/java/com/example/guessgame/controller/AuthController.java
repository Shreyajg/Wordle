package com.example.guessgame.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.guessgame.service.JwtService;
import com.example.guessgame.service.UserService;
import com.example.guessgame.model.User;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;

@RestController
@RequestMapping("/auth")
public class AuthController {
    
    private final UserService userService;
    private final JwtService jwtService;

    public AuthController(UserService userService,JwtService jwtService)
    {
        this.userService=userService;
        this.jwtService=jwtService;
    }
    @GetMapping("/me")
    public ResponseEntity<AuthResponse> getCurrentUser(Authentication authentication) {

        String username = authentication.getName();

        User user = userService.findByUsername(username);

        return ResponseEntity.ok(
            new AuthResponse(
                null,
                user.getUsername(),
                user.getRole().name()
            )
        );
    }
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody AuthRequest request)
    {
        User user= userService.register(request.username(), request.password(),request.confirmPassword());
        String token =jwtService.generateToken(user.getUsername());

        ResponseCookie cookie = ResponseCookie.from("token", token)
            .httpOnly(true)
            .secure(false) 
            .path("/")
            .maxAge(60 * 60)
            .sameSite("Lax")
            .build();

        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString()).body(new AuthResponse(
            null,
            user.getUsername(),
            user.getRole().name()
        ));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {

        User user = userService.login(
            request.username(),
            request.password()
        );

        if (user == null) {
            return ResponseEntity.status(401).build();
        }

        String token = jwtService.generateToken(user.getUsername());
        
        ResponseCookie cookie = ResponseCookie.from("token", token)
            .httpOnly(true)
            .secure(false) 
            .path("/")
            .maxAge(60 * 60)
            .sameSite("Lax")
            .build();

        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString()).body(new AuthResponse(
            null,
            user.getUsername(),
            user.getRole().name()
        ));
    }
    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {

        ResponseCookie cookie = ResponseCookie.from("token", "")
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(0)
                .sameSite("Lax")
                .build();

        return ResponseEntity.noContent()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .build();
    }

}
