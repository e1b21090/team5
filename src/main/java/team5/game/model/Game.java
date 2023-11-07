package team5.game.model;

import java.util.Random;

public class Game {

  public int DrawGame() {
    int num = (int) (Math.random() * 10) % 6 + 1;
    return num;
  }

  


}
