package com.example.guessgame.model;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document (collection = "games")
public class Game {
    
    @Id 
    private String id;
    private String playerId;
    private String targetWord;
    private List<String> guesses;
    private Status status;
    private LocalDateTime createdAt;

    public Game() {
    }

    public Game(String playerId, String targetWord,
                List<String> guesses, Status status,
                LocalDateTime createdAt) {
        this.playerId = playerId;
        this.targetWord = targetWord;
        this.guesses = guesses;
        this.status = status;
        this.createdAt = createdAt;
    }

    public List<String> getGuesses()
    {
        return this.guesses;
    }

    public String getTargetWord()
    {
        return this.targetWord;
    }

    public void setStatus(Status status)
    {
        this.status=status;
    }

    public Status getStatus()
    {
        return this.status;
    }
}
