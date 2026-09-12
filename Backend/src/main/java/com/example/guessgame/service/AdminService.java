package com.example.guessgame.service;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import org.springframework.stereotype.Service;

import com.example.guessgame.model.Status;
import com.example.guessgame.repository.*;
import com.example.guessgame.controller.AdminResponse;
import com.example.guessgame.controller.AdminUserResponse;

@Service 
public class AdminService {
    private final GameRepository gameRepository;
    private final UserRepository userRepository;

    public AdminService(GameRepository gameRepository,
                        UserRepository userRepository) {
        this.gameRepository = gameRepository;
        this.userRepository = userRepository;
    }

    public AdminResponse getDailyReport(LocalDate date)
    {
        LocalDateTime startOfDay=date.atStartOfDay();
        LocalDateTime endOfDay=date.atTime(LocalTime.MAX);
        long noOfUsers=userRepository.count();
        long noOfCorrectGuesses=gameRepository.findByStatusAndCreatedAtBetween(Status.WON,startOfDay,endOfDay).size();

        return new AdminResponse(noOfUsers,noOfCorrectGuesses);
    }

    public AdminUserResponse getUserReport(String playerId,LocalDate date)
    {
        LocalDateTime startOfDay=date.atStartOfDay();
        LocalDateTime endOfDay=date.atTime(LocalTime.MAX);
        long noOfGames = gameRepository.findByPlayerIdAndCreatedAtBetween(playerId, startOfDay, endOfDay).size();
        long noOfGamesWon = gameRepository.findByPlayerIdAndStatusAndCreatedAtBetween(playerId,Status.WON,startOfDay, endOfDay).size();

        return new AdminUserResponse(
            date,noOfGames,noOfGamesWon
        );
    }
}
