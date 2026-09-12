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

import org.springframework.stereotype.Service;
import com.example.guessgame.controller.GameResponse;
import java.util.ArrayList;
import java.util.HashMap;
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

        for (int i = 0; i < 5; i++) {
            if (guess.charAt(i) == targetWord.charAt(i)) {
                result[i]=LetterResult.GREEN;
            } else {
                result[i]=null; 
            }
        }
        HashMap<Character,Integer> hm=new HashMap<>();
        for(int i=0;i<5;i++)
        {
            if(result[i]==null)
            {
                char c=targetWord.charAt(i);
                hm.put(c,hm.getOrDefault(c,0)+1);
            }
        }
        for(int i=0;i<5;i++)
        {
            if(result[i] != null) {
                continue;
            }

            if(hm.getOrDefault(guess.charAt(i), 0) > 0 && result[i]==null)
            {
                result[i]=LetterResult.ORANGE;
                hm.put(guess.charAt(i),hm.get(guess.charAt(i))-1);
            }
            else{
                result[i]=LetterResult.GREY;
            }
        }
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
}
