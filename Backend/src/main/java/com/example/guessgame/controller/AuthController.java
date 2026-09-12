package com.example.guessgame.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.guessgame.service.JwtService;
import com.example.guessgame.service.UserService;
import com.example.guessgame.model.User;


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

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody AuthRequest request)
    {
        User user= userService.register(request.username(), request.password(),request.confirmPassword());
        String token =jwtService.generateToken(user.getUsername());

        return ResponseEntity.ok(new AuthResponse(
            token,
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

        return ResponseEntity.ok(new AuthResponse(
            token,
            user.getUsername(),
            user.getRole().name()
        ));
    }

}
