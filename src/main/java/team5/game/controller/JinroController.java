package team5.game.controller;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashSet;

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
import java.util.Set;

@Controller
public class JinroController {

  // 既に生成された数値を格納
  Set<Integer> uniqueNumbers = new HashSet<>();

  @Autowired
  private UserinfoMapper userinfoMapper;

  @Autowired
  private RolesMapper rolesMapper;

  @GetMapping("/entry")
  public String entry() {
    return "entry";
  }

  @GetMapping("/rules")
  public String rules() {
    return "rules";
  }

  @GetMapping("/game")
  public String game(Principal prin, ModelMap model) {
    Game game = new Game();
    int num = game.drawGame(uniqueNumbers);
    uniqueNumbers.add(num); // 数値の重複を防ぐためSetにランダム生成した値を記憶

    Roles roles = rolesMapper.selectRoles(num);
    userinfoMapper.updateUserInfo(roles.getName(), prin.getName());
    Userinfo userinfo = userinfoMapper.selectUserinfo(prin.getName());
    rolesMapper.updateUserInfo(num);
    model.addAttribute("userinfo", userinfo);
    return "game";
  }

  @GetMapping("fin")
  public String fin() {
    return "entry.html";
  }
}
