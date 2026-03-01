package service;

import model.Match;
import model.MatchScore;
import model.Player;

public class MatchScoreCalculationService {

    private static final int MIN_GAMES_TO_WIN_TIEBREAK = 7;
    private static final int MIN_GAMES_TO_WIN_SET = 6;
    private static final int GAMES_FOR_TIEBREAK = 6;
    private static final int MIN_POINT_WIN_GAME = 4;
    private static final int POINTS_FOR_ADVANTAGE = 4;
    private static final int DEUCE_RESET_POINTS = 3;
    private static final int SETS_TO_WIN_MATCH = 2;
    private static final int REQUIRED_DIFFERENCE = 2;


    public void calculateScore(Match match, Player winningPlayer) {
        if (match.getWinner() != null) {
            return;
        }
        MatchScore score = match.getScore();
        boolean winnerIsP1 = match.getPlayer1().equals(winningPlayer);

        if (checkTieBreak(score)) {
            awardPoint(score, winnerIsP1);
            checkTieBreakWin(score);
        } else {
            awardPoint(score, winnerIsP1);

            if (checkAdvantage(score)) {
            score.setPlayer1Points(DEUCE_RESET_POINTS);
            score.setPlayer2Points(DEUCE_RESET_POINTS);
            }
            checkGameWin(score);
            checkSetWin(score);
        }

        checkMatchWin(match);
        }

    private void awardPoint(MatchScore score, boolean winnerIsP1){
        if (score.getPlayer1Points() < 0 || score.getPlayer2Points() < 0) {
            throw new IllegalStateException("Счет не может быть отрицательным");
        }
        if(winnerIsP1){
            score.setPlayer1Points(score.getPlayer1Points() + 1);
        } else
            score.setPlayer2Points(score.getPlayer2Points() + 1);
        }


    private void checkTieBreakWin(MatchScore score) {
        int player1Points = score.getPlayer1Points();
        int player2Points = score.getPlayer2Points();

        int winnerPoints = Math.max(player1Points, player2Points);
        int loserPoints = Math.min(player1Points, player2Points);

        if(winnerPoints >= MIN_GAMES_TO_WIN_TIEBREAK && winnerPoints - loserPoints >= REQUIRED_DIFFERENCE){
            if(player1Points > player2Points){
                score.setPlayer1sets(score.getPlayer1sets() + 1);
            } else {
                score.setPlayer2sets(score.getPlayer2sets() + 1);
            }
            score.setPlayer1games(0);
            score.setPlayer2games(0);
            score.setPlayer1Points(0);
            score.setPlayer2Points(0);
        }
    }

    private void checkGameWin(MatchScore score){
        int player1Points = score.getPlayer1Points();
        int player2Points = score.getPlayer2Points();

        int winnerPoints = Math.max(player1Points, player2Points);
        int loserPoints = Math.min(player1Points, player2Points);

        if(winnerPoints >= MIN_POINT_WIN_GAME && winnerPoints - loserPoints >= REQUIRED_DIFFERENCE){
            if(player1Points > player2Points){
                score.setPlayer1games(score.getPlayer1games() + 1);
            } else {
                score.setPlayer2games(score.getPlayer2games() + 1);
            }
            score.setPlayer1Points(0);
            score.setPlayer2Points(0);
        }
    }

    private void checkSetWin(MatchScore score){
        int player1Game = score.getPlayer1games();
        int player2Game = score.getPlayer2games();

        int winnerGame = Math.max(player1Game, player2Game);
        int loserGame = Math.min(player1Game, player2Game);

        if(winnerGame >= MIN_GAMES_TO_WIN_SET && winnerGame - loserGame >= REQUIRED_DIFFERENCE){
            if(player1Game > player2Game){
                score.setPlayer1sets(score.getPlayer1sets() + 1);
            } else {
                score.setPlayer2sets(score.getPlayer2sets() + 1);
            }
            score.setPlayer1games(0);
            score.setPlayer2games(0);
            score.setPlayer1Points(0);
            score.setPlayer2Points(0);
        }
    }

    private void checkMatchWin(Match match){
        int player1Set = match.getScore().getPlayer1sets();
        int player2Set = match.getScore().getPlayer2sets();
        if (player1Set == SETS_TO_WIN_MATCH){
            match.setWinner(match.getPlayer1());
        }else if(player2Set == SETS_TO_WIN_MATCH){
            match.setWinner(match.getPlayer2());
        }
    }

    private boolean checkTieBreak(MatchScore score){
        return score.getPlayer1games() == GAMES_FOR_TIEBREAK && score.getPlayer2games() == GAMES_FOR_TIEBREAK;
    }

    private boolean checkAdvantage(MatchScore score){
        return score.getPlayer1Points() == POINTS_FOR_ADVANTAGE && score.getPlayer2Points() == POINTS_FOR_ADVANTAGE;
    }
}
