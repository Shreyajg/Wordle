package com.example.guessgame.repository;

import com.example.guessgame.model.Word;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

public interface WordRepository extends MongoRepository<Word,String> {
   Optional<Word> findByWord(String word);
}
