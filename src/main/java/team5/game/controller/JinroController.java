package team5.game.controller;

import java.security.Principal;
import java.util.ArrayList;

import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import team5.game.model.Game;
import team5.game.model.Roles;
import team5.game.model.RolesMapper;
import team5.game.model.Userinfo;
import team5.game.model.UserinfoMapper;

@Controller
public class JinroController {
  @Autowired
  private UserinfoMapper userinfoMapper;

  @Autowired
  private RolesMapper rolesMapper;

  @GetMapping("/entry")
  public String entry() {
    return "entry";
  }

  @GetMapping("/game")
  public String game(Principal prin) {
    Game game = new Game();
    int num;
    num = game.DrawGame();
    Roles roles = rolesMapper.selectRoles(num);
    userinfoMapper.updateUserInfo(roles.getName(),prin.getName());
    return "game";
  }

}
