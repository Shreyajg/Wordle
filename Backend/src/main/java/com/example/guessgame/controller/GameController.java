package com.example.guessgame.controller;


import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.guessgame.service.GameService;
import com.example.guessgame.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import com.example.guessgame.model.User;
import com.example.guessgame.model.Game;
@RestController
@RequestMapping("/games")

public class GameController {
    
    private final UserRepository userRepository;
    private final GameService gameService;

    public GameController(UserRepository userRepository,GameService gameService)
    {
        this.gameService=gameService;
        this.userRepository=userRepository;
    }

    @PostMapping("/start")
    public Game startGame()
    {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userRepository.findByUsername(username).orElseThrow();
        return gameService.startGame(user.getId());
    }
    
    @PostMapping("/guess")
    public GameResponse makeGuessWord(@RequestBody GuessRequest request)
    {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userRepository.findByUsername(username).orElseThrow();
        return gameService.makeGuessWord(user.getId(),request.guess());
        
    }
}
