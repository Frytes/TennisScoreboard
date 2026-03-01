package model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@EqualsAndHashCode(of = "id")
public class Player {
     private Long id;
     private String name;

     public Player(String name) {
          this.name = name;
     }

}
