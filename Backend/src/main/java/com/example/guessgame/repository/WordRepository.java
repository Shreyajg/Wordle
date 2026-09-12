package com.example.guessgame.repository;

import com.example.guessgame.model.Word;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface WordRepository extends MongoRepository<Word,String> {
    
}
