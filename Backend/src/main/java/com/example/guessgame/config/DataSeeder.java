package com.example.guessgame.config;

import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import com.example.guessgame.repository.WordRepository;
import com.example.guessgame.model.Word;
import com.example.guessgame.repository.UserRepository;
import com.example.guessgame.model.User;
@Component
public class DataSeeder implements CommandLineRunner {

    private final WordRepository wordRepository;

    public DataSeeder(WordRepository wordRepository) {
        this.wordRepository = wordRepository;
    }

    @Override
    public void run(String... args) {
        if (wordRepository.count() == 0) {

        List<String> words = List.of(
            "APPLE",
            "BRAVE",
            "CLOUD",
            "DREAM",
            "EARTH",
            "FLAME",
            "GRAPE",
            "HOUSE",
            "LIGHT",
            "MUSIC",
            "NIGHT",
            "OCEAN",
            "PIANO",
            "QUEEN",
            "RIVER",
            "SMILE",
            "STONE",
            "TABLE",
            "TRAIN",
            "WORLD"
        );

        for (String word : words) {
            wordRepository.save(new Word(word));
        }
    }
    if (userRepository.findByUsername("AdminUser").isEmpty()) {
            User admin = new User(
                "AdminUser",
                passwordEncoder.encode("Admin1$"),
                Role.ADMIN
            );

            userRepository.save(admin);
        }
    }
}