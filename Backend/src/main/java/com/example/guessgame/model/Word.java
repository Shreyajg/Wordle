package com.example.guessgame.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document (collection = "words")
public class Word {
    
    @Id
    private String id;
    private String word;
    
    public Word(String word) {
        this.word = word;
    }
    public String getWord()
    {
        return this.word;
    }
}
