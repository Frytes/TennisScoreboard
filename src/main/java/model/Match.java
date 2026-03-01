package model;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class Match {
  private UUID uuid;
  private Long id;
  private Player player1;
  private Player player2;
  private Player winner;
  private MatchScore score;

    public Match(Player player1, Player player2) {
        this.uuid = UUID.randomUUID();
        this.player1 = player1;
        this.player2 = player2;
        this.winner = null;
        this.score = new MatchScore();
    }






}
