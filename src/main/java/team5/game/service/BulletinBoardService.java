package team5.game.service;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class BulletinBoardService {

  private List<String> messageList = new ArrayList<>();

  @Async
  public CompletableFuture<String> postMessage(String message) {
    // メッセージを配列に保存する
    messageList.add(message);

    // 保存されたメッセージ一覧を表示するなどの追加の処理も可能

    return CompletableFuture.completedFuture("投稿が完了しました: " + message);
  }

  public List<String> getAllMessages() {
    // すべてのメッセージを取得する
    return new ArrayList<>(messageList);
  }

  public void resetMessages() {
    messageList.clear();
  }
}
