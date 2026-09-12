package com.example.guessgame.controller;

import java.time.LocalDate;

public record AdminUserResponse(LocalDate date,
    long noOfWordsTried,
    long noOfCorrectGuesses) {
    
}
