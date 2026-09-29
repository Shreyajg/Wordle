package com.example.guessgame.service;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.guessgame.exception.GameException;
import com.example.guessgame.model.Game;
import com.example.guessgame.model.LetterResult;
import com.example.guessgame.model.Status;
import com.example.guessgame.model.Word;
import com.example.guessgame.repository.GameRepository;
import com.example.guessgame.repository.WordRepository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * NOTE: requires GameService.calculateResult to be `static` and package-private
 * (remove `private`) so it can be tested directly:
 *
 *     static LetterResult[] calculateResult(String target, String guess) { ... }
 */
@ExtendWith(MockitoExtension.class)
class GameServiceTest {

    private static final LetterResult G = LetterResult.GREEN;
    private static final LetterResult O = LetterResult.ORANGE;
    private static final LetterResult X = LetterResult.GREY;

    @Mock
    GameRepository gameRepository;

    @Mock
    WordRepository wordRepository;

    @InjectMocks
    GameService gameService;

    // ---------- Feedback logic ----------

    @Test
    void allGreenWhenGuessMatchesTarget() {
        assertArrayEquals(new LetterResult[]{G, G, G, G, G},
                GameService.calculateResult("APPLE", "APPLE"));
    }

    @Test
    void allGreyWhenNoLettersMatch() {
        assertArrayEquals(new LetterResult[]{X, X, X, X, X},
                GameService.calculateResult("APPLE", "BUNKY"));
    }

    @Test
    void duplicateLettersInGuess_onlyAsManyMarkedAsTargetHas() {
        // target has one L, one A: extra L and A in the guess must be grey
        assertArrayEquals(new LetterResult[]{O, X, O, X, X},
                GameService.calculateResult("APPLE", "LLAMA"));
    }

    @Test
    void greenTakesPriorityOverOrangeForDuplicates() {
        // P at index 2 is green; the other two P's/letters resolve against remaining target letters
        assertArrayEquals(new LetterResult[]{O, O, G, O, X},
                GameService.calculateResult("APPLE", "PAPER"));
    }

    // ---------- Daily limit / resume ----------

    @Test
    void startGame_resumesActiveGameWithoutCreatingNew() {
        Game active = new Game("p1", "APPLE", new ArrayList<>(), Status.IN_PROGRESS, LocalDateTime.now());
        when(gameRepository.findByPlayerIdAndStatus("p1", Status.IN_PROGRESS))
                .thenReturn(Optional.of(active));

        Game result = gameService.startGame("p1");

        assertSame(active, result);
        verify(gameRepository, never()).save(any());
    }

    @Test
    void startGame_rejectsFourthGameOfTheDay() {
        when(gameRepository.findByPlayerIdAndStatus("p1", Status.IN_PROGRESS))
                .thenReturn(Optional.empty());
        when(gameRepository.findByPlayerIdAndCreatedAtBetween(eq("p1"), any(), any()))
                .thenReturn(Collections.nCopies(3, mock(Game.class)));

        assertThrows(GameException.class, () -> gameService.startGame("p1"));
        verify(gameRepository, never()).save(any());
    }

    // ---------- Win / loss ----------

    @Test
    void correctGuessSetsStatusWon() {
        Game game = new Game("p1", "APPLE", new ArrayList<>(), Status.IN_PROGRESS, LocalDateTime.now());
        when(gameRepository.findByPlayerIdAndStatus("p1", Status.IN_PROGRESS))
                .thenReturn(Optional.of(game));
        when(wordRepository.findByWord("APPLE")).thenReturn(Optional.of(mock(Word.class)));

        gameService.makeGuessWord("p1", "apple");

        assertEquals(Status.WON, game.getStatus());
    }

    @Test
    void fifthWrongGuessSetsStatusLost() {
        List<String> fourGuesses = new ArrayList<>(List.of("CRANE", "SLATE", "MOIST", "BUNKY"));
        Game game = new Game("p1", "APPLE", fourGuesses, Status.IN_PROGRESS, LocalDateTime.now());
        when(gameRepository.findByPlayerIdAndStatus("p1", Status.IN_PROGRESS))
                .thenReturn(Optional.of(game));
        when(wordRepository.findByWord("PAPER")).thenReturn(Optional.of(mock(Word.class)));

        gameService.makeGuessWord("p1", "PAPER");

        assertEquals(Status.LOST, game.getStatus());
        assertEquals(5, game.getGuesses().size());
    }

    // ---------- Validation ----------

    @Test
    void wordNotInListIsRejectedAndNotCounted() {
        Game game = new Game("p1", "APPLE", new ArrayList<>(), Status.IN_PROGRESS, LocalDateTime.now());
        when(gameRepository.findByPlayerIdAndStatus("p1", Status.IN_PROGRESS))
                .thenReturn(Optional.of(game));
        when(wordRepository.findByWord("ZZZZZ")).thenReturn(Optional.empty());

        assertThrows(GameException.class, () -> gameService.makeGuessWord("p1", "ZZZZZ"));
        assertEquals(0, game.getGuesses().size());
        verify(gameRepository, never()).save(any());
    }

    @Test
    void wrongLengthGuessIsRejected() {
        Game game = new Game("p1", "APPLE", new ArrayList<>(), Status.IN_PROGRESS, LocalDateTime.now());
        when(gameRepository.findByPlayerIdAndStatus("p1", Status.IN_PROGRESS))
                .thenReturn(Optional.of(game));

        assertThrows(GameException.class, () -> gameService.makeGuessWord("p1", "APP"));
    }
}