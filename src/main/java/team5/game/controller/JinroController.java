package team5.game.controller;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashSet;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.springframework.web.bind.support.SessionStatus;
import team5.game.model.Game;
import team5.game.model.Roles;
import team5.game.model.RolesMapper;
import team5.game.model.Userinfo;
import team5.game.model.UserinfoMapper;
import java.util.Set;
import team5.game.service.AsyncStandbyRoom;

@Controller
@SessionAttributes("userinfo")
public class JinroController {

  // 既に生成された数値を格納
  Set<Integer> uniqueNumbers = new HashSet<>();

  @Autowired
  private UserinfoMapper userinfoMapper;

  @Autowired
  private RolesMapper rolesMapper;

  @Autowired
  private AsyncStandbyRoom asyncStandbyRoom;

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
    Userinfo userinfo = userinfoMapper.selectUserinfo(prin.getName());

    if (userinfo.getRole().equals("人狼")) {
      boolean isJinro = true;
      String jinro = userinfoMapper.selectJinro(prin.getName());
      model.addAttribute("jinro", jinro);
      model.addAttribute("isJinro", isJinro);
    }
    if (userinfo.getRole().equals("占い師")) {
      ArrayList<Userinfo> uranai = userinfoMapper.selectTarget(prin.getName());
      model.addAttribute("uranai", uranai);
    }
    if (userinfo.getRole().equals("怪盗")) {
      ArrayList<Userinfo> kaito = userinfoMapper.selectTarget(prin.getName());
      model.addAttribute("kaito", kaito);
    }
    if (userinfo.getRole().equals("市民")) {
      ArrayList<Userinfo> simin = userinfoMapper.selectTarget(prin.getName());
      model.addAttribute("simin", simin);
    }

    model.addAttribute("userinfo", userinfo);
    return "game";
  }

  @GetMapping("/standbyroom")
  public SseEmitter standbyroom() {
    final SseEmitter emitter = new SseEmitter();
    this.asyncStandbyRoom.standby(emitter);
    return emitter;
  }

  @GetMapping("/standby")
  public String standby(Principal prin, ModelMap model, SessionStatus SessionStatus) {
    if (!model.containsAttribute("userinfo")) {
      Game game = new Game();
      int num = game.drawGame(uniqueNumbers);
      uniqueNumbers.add(num); // 数値の重複を防ぐためSetにランダム生成した値を記憶

      Roles roles = rolesMapper.selectRoles(num);
      userinfoMapper.updateUserInfo(roles.getName(), prin.getName());
      Userinfo userinfo = userinfoMapper.selectUserinfo(prin.getName());
      rolesMapper.updateUserInfo(num);

      model.addAttribute("userinfo", userinfo);
    }
    return "standby";
  }

}
