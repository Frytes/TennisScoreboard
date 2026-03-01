package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import service.MatchScoreCalculationService;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MatchTest {

    private Player player1;
    private Player player2;
    private Match match;
    private MatchScoreCalculationService matchScoreCalculationService;

    @BeforeEach
    void setUp() {
        player1 = new Player("Ivan");
        player2 = new Player("Petr");
        match = new Match(player1, player2);
        matchScoreCalculationService = new MatchScoreCalculationService();
    }

    @Test
    @DisplayName("Игрок 1 выигрывает первое очко (0 -> 15)")
    void shouldUpdateScoreWhenPlayer1WinsPoint() {
        matchScoreCalculationService.calculateScore(match,player1);

        // В нашей логике: 0="0", 1="15", 2="30", 3="40"
        assertEquals(1, match.getScore().getPlayer1Points(), "Очки игрока 1 должны стать 1 (15)");
        assertEquals(0, match.getScore().getPlayer2Points(), "Очки игрока 2 должны остаться 0");
    }
    @Test
    @DisplayName("Игрок 1 выигрывает гейм (40 -> 0, гейм +1)")
    void shouldWinGameWhenPlayer1WinsPointAt40() {
        match.getScore().setPlayer1Points(3);

        matchScoreCalculationService.calculateScore(match,player1);

        assertEquals(0, match.getScore().getPlayer1Points(), "Очки должны сброситься в 0");
        assertEquals(1, match.getScore().getPlayer1games(), "Геймы должны стать 1");
    }

    @Test
    @DisplayName("Возврат к Deuce: Если у P2 преимущество (4-3) и P1 выигрывает, счет становится 3-3")
    void shouldGameWhenPlayer1andPlayers2WinsPointAt40() {
        match.getScore().setPlayer1Points(3);
        match.getScore().setPlayer2Points(4);

        matchScoreCalculationService.calculateScore(match,player1);
        assertEquals(3, match.getScore().getPlayer1Points(), "Очки игрока 1 должны быть 3 (40)");
        assertEquals(3, match.getScore().getPlayer2Points(), "Очки игрока 2 должны сброситься к 3 (40)");
        assertEquals(0, match.getScore().getPlayer1games(), "Гейм должен остаться 0");
    }
    @Test
    @DisplayName("Игрок 1 выиграл гейм")
    void shouldUpdateGameWhenPlayer1WinsPoint() {
        match.getScore().setPlayer1Points(3);
        match.getScore().setPlayer2Points(2);

        matchScoreCalculationService.calculateScore(match,player1);

        assertEquals(0, match.getScore().getPlayer1Points(), "Очки игрока 1 очков должно стать 0 (0)");
        assertEquals(1, match.getScore().getPlayer1games(), "Гейм должен стать 1");
    }

    @Test
    @DisplayName("Геймы должны остаться 6-6 при розыгрыше тай брейка")
    void  shouldTreatGameAsTieBreakWhenScoreIs66() {
        match.getScore().setPlayer1games(6);
        match.getScore().setPlayer2games(6);

        matchScoreCalculationService.calculateScore(match,player1);

        assertEquals(1, match.getScore().getPlayer1Points(), "В тай-брейке очки идут 0->1->2...");
        assertEquals(6, match.getScore().getPlayer1games(), "Гейм у игрока 1 должен быть 6");
    }

    @Test
    @DisplayName("Есть Победитель Тай брейка")
    void shouldWinSetWhenTieBreakWon7_0() {
        match.getScore().setPlayer1games(6);
        match.getScore().setPlayer2games(6);
        match.getScore().setPlayer1Points(6);
        match.getScore().setPlayer2Points(0);

        matchScoreCalculationService.calculateScore(match,player1);

        assertEquals(0, match.getScore().getPlayer1games(), "Гейм у игрока 1 должен быть 0");
        assertEquals(1, match.getScore().getPlayer1sets(), "Сет у игрока 1 должен быть 1");
    }

    @Test
    @DisplayName("Игрок 1 выиграл сет")
    void shouldUpdateSetWhenPlayer1WinsGame() {
        match.getScore().setPlayer1games(5);
        match.getScore().setPlayer1Points(3);

        matchScoreCalculationService.calculateScore(match,player1);

        assertEquals(0, match.getScore().getPlayer1games(), "Гейм у игрока 1 должен быть 0");
        assertEquals(1, match.getScore().getPlayer1sets(), "Сет у игрока 1 должен быть 1");
    }
    @Test
    @DisplayName("Игрок 1 выиграл матч")
    void shouldSetWinnerWhenPlayerWinsSecondSet() {
        match.getScore().setPlayer1sets(1);
        match.getScore().setPlayer1games(5);
        match.getScore().setPlayer1Points(3);

        matchScoreCalculationService.calculateScore(match,player1);

        assertEquals(match.getPlayer1(), match.getWinner(), "Игрок 1 в победителях матча");
    }
    @Test
    @DisplayName("Ситуация 'Больше' -> 'Ровно'. Если у Игрока 1 'Больше' и Игрок 2 выигрывает , счет должен стать 'Ровно'")
    void shouldReturnToDeuceFromAdvantage() {
        match.getScore().setPlayer1Points(4);
        match.getScore().setPlayer2Points(3);

        matchScoreCalculationService.calculateScore(match, player2);

        assertEquals(3, match.getScore().getPlayer1Points(), "Очки игрока 1 должны вернуться к 3 (40)");
        assertEquals(3, match.getScore().getPlayer2Points(), "Очки игрока 2 должны быть 3 (40)");
    }
    @Test
    @DisplayName("При тай брейке верное отображения счета 4-4 ")
    void shouldNotResetTiebreakScoreToThreeThree() {
        match.getScore().setPlayer1games(6);
        match.getScore().setPlayer2games(6);
        match.getScore().setPlayer1Points(3);
        match.getScore().setPlayer2Points(4);

        matchScoreCalculationService.calculateScore(match, player1);

        assertEquals(4, match.getScore().getPlayer1Points(), "Очки при тай брейке игрока 1 должны быть 4");
        assertEquals(4, match.getScore().getPlayer2Points(), "Очки при тай брейке игрока 2 должны быть 4");
    }
}