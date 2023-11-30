package team5.game.controller;

import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import team5.game.service.BulletinBoardService;
import org.springframework.beans.factory.annotation.Autowired;

@RestController
public class BulletinBoardApiController {

  @Autowired
  private BulletinBoardService bulletinBoardService;

  @GetMapping("/api/kaigi")
  public List<String> getMessages() {
    return bulletinBoardService.getAllMessages();
  }
}
