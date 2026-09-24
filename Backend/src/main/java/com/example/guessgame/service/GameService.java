package com.example.guessgame.service;

import com.example.guessgame.repository.GameRepository;
import com.example.guessgame.repository.WordRepository;
import com.example.guessgame.model.Status;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;
import com.example.guessgame.model.Game;
import com.example.guessgame.model.Word;
import java.util.List;
import java.util.Random;
import com.example.guessgame.controller.CurrentGameResponse;
import org.springframework.stereotype.Service;
import com.example.guessgame.controller.GameResponse;
import java.util.ArrayList;
import com.example.guessgame.model.LetterResult;
import com.example.guessgame.exception.GameException;

@Service 
public class GameService {
    private final GameRepository gameRepository;
    private final WordRepository wordRepository;

    public GameService(GameRepository gameRepository,WordRepository wordRepository)
    {
        this.gameRepository=gameRepository;
        this.wordRepository=wordRepository;
    }

    public Game startGame(String playerId)
    {
        LocalDate today=LocalDate.now();
        LocalDateTime startOfDay=today.atStartOfDay();
        LocalDateTime endOfDay=today.atTime(LocalTime.MAX);
        Optional<Game> activeGame=gameRepository.findByPlayerIdAndStatus(playerId,Status.IN_PROGRESS);
        if(activeGame.isPresent())
        {
            return activeGame.get();
        }
        else if(gameRepository.findByPlayerIdAndCreatedAtBetween(playerId,startOfDay,endOfDay).size()<3)
        {
            List<Word> words = wordRepository.findAll();
            if (words.isEmpty()) {
                throw new GameException("No words in the database");
            }
            Random random=new Random();
            int index=random.nextInt(words.size());
            Word word=words.get(index);
            Game game=new Game(playerId,word.getWord(),new ArrayList<String>(),Status.IN_PROGRESS,LocalDateTime.now());
            return gameRepository.save(game);
        }
        else{
            throw new GameException("Limits exceeded ! try again tomorrow!");
        }
        
    }

    public GameResponse makeGuessWord(String playerId,String guess)
    {
        Optional<Game> activeGame=gameRepository.findByPlayerIdAndStatus(playerId, Status.IN_PROGRESS);
        
        if(!activeGame.isPresent())
        {
            throw new GameException("Please start a game");
        }
        Game currGame=activeGame.get();
        System.out.println("TARGET: " + currGame.getTargetWord());
        System.out.println("GUESS: " + guess);
        //validate guess
        guess=guess.trim().toUpperCase();
        if(guess.length()!=5) throw new GameException("Word length should be equal to 5");
        if (wordRepository.findByWord(guess).isEmpty()) {
            throw new GameException("Word is not in the word list");
        }
        for(int i=0;i<guess.length();i++)
        {
            if (guess.charAt(i) < 'A' || guess.charAt(i) > 'Z')
            {
                throw new GameException("please enter a valid guess");
            }
        }
        List<String> currGuesses=currGame.getGuesses();
        String targetWord=currGame.getTargetWord().trim().toUpperCase();
        LetterResult[] result=new LetterResult[5];

        result=calculateResult(targetWord, guess);
        currGuesses.add(guess);
        boolean won=true;
        for(int i=0;i<5;i++)
        {
            if(result[i]!=LetterResult.GREEN) 
            {
                won=false;
                break;
            }
        }

        if(won)
        {
            currGame.setStatus(Status.WON);
        }

        else if(currGuesses.size()==5)
        {
            currGame.setStatus(Status.LOST);
        }
        gameRepository.save(currGame);
        return new GameResponse(result,currGame.getStatus());
    }
    private LetterResult[] calculateResult(String target, String guess) {

        LetterResult[] result = new LetterResult[5];
        boolean[] used = new boolean[5];

        // First pass: GREEN
        for (int i = 0; i < 5; i++) {
            if (guess.charAt(i) == target.charAt(i)) {
                result[i] = LetterResult.GREEN;
                used[i] = true;
            }
        }

        // Second pass: ORANGE / GREY
        for (int i = 0; i < 5; i++) {

            if (result[i] != null) {
                continue;
            }

            for (int j = 0; j < 5; j++) {

                if (!used[j] && guess.charAt(i) == target.charAt(j)) {
                    result[i] = LetterResult.ORANGE;
                    used[j] = true;
                    break;
                }
            }

            if (result[i] == null) {
                result[i] = LetterResult.GREY;
            }
        }

        return result;
    }
    public CurrentGameResponse getCurrentGame(String playerId) {

        Optional<Game> activeGame =
                gameRepository.findByPlayerIdAndStatus(
                        playerId,
                        Status.IN_PROGRESS
                );

        if (activeGame.isEmpty()) {
            return new CurrentGameResponse(
                    List.of(),
                    List.of(),
                    Status.IN_PROGRESS
            );
        }

        Game game = activeGame.get();

        List<String> guesses = game.getGuesses();

        List<LetterResult[]> results = guesses.stream()
                .map(guess ->
                        calculateResult(game.getTargetWord(), guess)
                )
                .toList();

        return new CurrentGameResponse(
                guesses,
                results,
                game.getStatus()
        );
    }
    public CurrentGameResponse getGameResponse(Game game) {

    List<LetterResult[]> results = game.getGuesses()
            .stream()
            .map(guess -> calculateResult(
                    game.getTargetWord(),
                    guess
            ))
            .toList();

    return new CurrentGameResponse(
            game.getGuesses(),
            results,
            game.getStatus()
    );
}
}
