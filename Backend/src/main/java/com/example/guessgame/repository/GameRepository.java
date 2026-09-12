package com.example.guessgame.repository;

import com.example.guessgame.model.Game;
import com.example.guessgame.model.Status;
import java.util.List;
import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface GameRepository extends MongoRepository<Game,String>{
    
    List<Game> findByPlayerIdAndCreatedAtBetween(String playerId,LocalDateTime start,LocalDateTime end);
    Optional<Game> findByPlayerIdAndStatus(String playerId,Status status);
    List<Game> findByStatusAndCreatedAtBetween(Status status,LocalDateTime start,LocalDateTime end);
    List<Game> findByPlayerIdAndStatusAndCreatedAtBetween(String playerId,Status status,LocalDateTime start,LocalDateTime end);
}
