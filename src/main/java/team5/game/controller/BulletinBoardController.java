
package team5.game.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import team5.game.service.BulletinBoardService;

@Controller
public class BulletinBoardController {

  @Autowired
  private BulletinBoardService bulletinBoardService;

  @GetMapping("/kaigi")
  public String getAllMessages(Model model) {
    model.addAttribute("kaigi", bulletinBoardService.getAllMessages());
    return "kaigi";
  }

  @PostMapping("/post")
  public String postMessage(@RequestParam("message") String message) {
    bulletinBoardService.postMessage(message);
    return "redirect:/kaigi";
  }
}
