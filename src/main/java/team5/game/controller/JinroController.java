package team5.game.controller;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashSet;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
import team5.game.service.AsyncCheck;
import team5.game.service.AsyncKaigi;
import team5.game.service.AsyncToRoles;
import team5.game.service.AsyncVote;
import team5.game.service.AsyncVotePhase;

@Controller
@SessionAttributes("userinfo")
public class JinroController {

  // 既に生成された数値を格納
  Set<Integer> uniqueNumbers = new HashSet<>();

  Userinfo stealTarget;

  Userinfo thief;

  @Autowired
  private UserinfoMapper userinfoMapper;

  @Autowired
  private RolesMapper rolesMapper;

  @Autowired
  private AsyncStandbyRoom asyncStandbyRoom;

  @Autowired
  private AsyncCheck asyncCheck;

  @Autowired
  private AsyncKaigi asyncKaigi;

  @Autowired
  private AsyncToRoles asyncToRoles;

  @Autowired
  private AsyncVote asyncVote;

  @Autowired
  private AsyncVotePhase asyncVotePhase;

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

  @GetMapping("/checkcard")
  public SseEmitter checkcard() {
    final SseEmitter emitter = new SseEmitter();
    this.asyncCheck.check(emitter);
    return emitter;
  }

  @GetMapping("/check")
  public String check(Principal prin, ModelMap model) {
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
    return "check";
  }

  @GetMapping("/uranai")
  public String soothsayer(@RequestParam("target") String target, ModelMap model, Principal prin) {
    if (target.equals("graveyard")) {
      ArrayList<Roles> graveyard = rolesMapper.selectGraveyard();
      model.addAttribute("graveyard", graveyard);
    } else {
      Userinfo predictTarget = userinfoMapper.selectUserinfo(target);
      model.addAttribute("predictTarget", predictTarget);
    }
    Userinfo soothsayer = userinfoMapper.selectUserinfo(prin.getName());
    model.addAttribute("soothsayer", soothsayer);
    return "outcome";
  }

  @GetMapping("/kaito")
  public String thief(@RequestParam("target") String target, ModelMap model, Principal prin) {
    this.stealTarget = userinfoMapper.selectUserinfo(target);
    this.thief = userinfoMapper.selectUserinfo(prin.getName());
    model.addAttribute("thief", thief);
    model.addAttribute("stealTarget", stealTarget);
    return "outcome";
  }

  @GetMapping("/movekaigi")
  public SseEmitter movekaigi() {
    final SseEmitter emitter = new SseEmitter();
    this.asyncKaigi.kaigi(emitter);
    return emitter;
  }

  @GetMapping("/toRoles")
  public SseEmitter toRoles() {
    final SseEmitter emitter = new SseEmitter();
    this.asyncToRoles.toRoles(emitter);
    return emitter;
  }

  @GetMapping("/toVote")
  public SseEmitter toVote() {
    final SseEmitter emitter = new SseEmitter();
    this.asyncVote.vote(emitter);
    return emitter;
  }

  @GetMapping("/votePhase")
  public SseEmitter votePhase() {
    final SseEmitter emitter = new SseEmitter();
    this.asyncVotePhase.votePhase(emitter);
    return emitter;
  }

  @GetMapping("/vote")
  public String vote(Principal prin, ModelMap model) {
    if (this.stealTarget != null && this.thief.getRole().equals("怪盗")) {
      userinfoMapper.updateUserInfo(stealTarget.getRole(), thief.getUsername());
      userinfoMapper.updateUserInfo("怪盗", stealTarget.getUsername());
    }
    ArrayList<Userinfo> v_target = userinfoMapper.selectTarget(prin.getName());
    model.addAttribute("v_target", v_target);
    return "vote";
  }

  @GetMapping("/voteresult")
  public String voteresult(@RequestParam("selection") String selection, ModelMap model) {
    model.addAttribute("selection", selection);
    return "voteresult";
  }

}
