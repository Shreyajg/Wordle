package com.example.guessgame.controller;

import com.example.guessgame.model.LetterResult;
import com.example.guessgame.model.Status;

public record GameResponse(LetterResult[] results,Status status) {
    
}
