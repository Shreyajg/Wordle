package com.example.guessgame.controller;
import com.example.guessgame.model.LetterResult;
import com.example.guessgame.model.Status;
import java.util.List;

public record CurrentGameResponse(
        List<String> guesses,
        List<LetterResult[]> results,
        Status status
) {
}