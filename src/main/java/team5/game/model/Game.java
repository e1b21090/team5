package team5.game.model;

import java.util.Set;

public class Game {

  public int drawGame(Set<Integer> uniqueNumbers) {
    int randomNumber;
    // 生成したランダムな数値が既にSetに存在している場合はループが続く
    do {
      randomNumber = (int) (Math.random() * (6) + 1);
    } while (uniqueNumbers.contains(randomNumber));

    return randomNumber;
  }

}
