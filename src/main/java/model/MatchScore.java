package model;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class MatchScore {
   private int player1sets;
   private int player2sets;

   private int player1games;
   private int player2games;

   private int player1Points;
   private int player2Points;

   public String getPlayer1PointsDisplay() {
      return translatePoints(player1Points, player2Points);
   }

   public String getPlayer2PointsDisplay() {
      return translatePoints(player2Points, player1Points);
   }

   private String translatePoints(int playerPoints, int opponentPoints) {
      if (player1games == 6 && player2games == 6) {
         return String.valueOf(playerPoints);
      }

      if (playerPoints >= 3 && opponentPoints >= 3) {
         if (playerPoints == opponentPoints) {
            return "40";
         } else if (playerPoints > opponentPoints) {
            return "AD";
         } else {
            return "40";
         }
      } else {
          return switch (playerPoints) {
              case 0 -> "0";
              case 1 -> "15";
              case 2 -> "30";
              case 3 -> "40";
              default -> String.valueOf(playerPoints);
          };
      }
   }
}
