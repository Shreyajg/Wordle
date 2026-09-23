package com.example.guessgame.controller;


import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.guessgame.service.GameService;
import com.example.guessgame.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
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
    public CurrentGameResponse startGame() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        String username = authentication.getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow();

        Game game = gameService.startGame(user.getId());

        return gameService.getGameResponse(game);
    }
    
    @PostMapping("/guess")
    public GameResponse makeGuessWord(@RequestBody GuessRequest request)
    {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userRepository.findByUsername(username).orElseThrow();
        return gameService.makeGuessWord(user.getId(),request.guess());
        
    }
    @GetMapping("/current")
    public CurrentGameResponse getCurrentGame(Authentication authentication) {

        String username = authentication.getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow();

        return gameService.getCurrentGame(user.getId());
    }
}
