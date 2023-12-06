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
import team5.game.service.AsyncVoteFinish;

@Controller
@SessionAttributes("userinfo")
public class JinroController {

  // 既に生成された数値を格納
  Set<Integer> uniqueNumbers = new HashSet<>();

  Userinfo stealTarget;

  Userinfo thief;

  int voteCount0 = 0;
  int voteCount1 = 0;
  int voteCount2 = 0;
  int voteCount3 = 0;
  int voteCount4 = 0;

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

  @Autowired
  private AsyncVoteFinish asyncVoteFinish;

  @GetMapping("/entry")
  public String entry() {
    return "entry";
  }

  @GetMapping("/rules")
  public String rules() {
    return "rules";
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

  @GetMapping("/waitingvoteresult")
  public String waitingvoteresult(@RequestParam("selection") String selection, ModelMap model, Principal prin) {
    if (selection.equals("吊らない")) {
      voteCount0++;
    } else if (selection.equals("user1")) {
      voteCount1++;
    } else if (selection.equals("user2")) {
      voteCount2++;
    } else if (selection.equals("user3")) {
      voteCount3++;
    } else if (selection.equals("user4")) {
      voteCount4++;
    }
    model.addAttribute("selection", selection);
    userinfoMapper.updateSelectedTrue(prin.getName());
    Userinfo userinfo = userinfoMapper.selectUserinfo(prin.getName());
    model.addAttribute("userinfo", userinfo);
    return "waitingvoteresult";
  }

  @GetMapping("/voteresult")
  public String voteresult(ModelMap model) {
    if (voteCount1 > voteCount0 && voteCount1 > voteCount2 && voteCount1 > voteCount3 && voteCount1 > voteCount4) {
      model.addAttribute("selection", "user1");
    } else if (voteCount2 > voteCount0 && voteCount2 > voteCount1 && voteCount2 > voteCount3
        && voteCount2 > voteCount4) {
      model.addAttribute("selection", "user2");
    } else if (voteCount3 > voteCount0 && voteCount3 > voteCount1 && voteCount3 > voteCount2
        && voteCount3 > voteCount4) {
      model.addAttribute("selection", "user3");
    } else if (voteCount4 > voteCount0 && voteCount4 > voteCount1 && voteCount4 > voteCount2
        && voteCount4 > voteCount3) {
      model.addAttribute("selection", "user4");
    } else {
      model.addAttribute("selection", "吊らない");
    }
    model.addAttribute("count_0", voteCount0);
    model.addAttribute("count_1", voteCount1);
    model.addAttribute("count_2", voteCount2);
    model.addAttribute("count_3", voteCount3);
    model.addAttribute("count_4", voteCount4);
    return "voteresult";
  }

  @GetMapping("/voteFinish")
  public SseEmitter voteFinish() {
    final SseEmitter emitter = new SseEmitter();
    this.asyncVoteFinish.voteFinish(emitter);
    return emitter;
  }

  @GetMapping("/gameresult")
  public String gameresult(@RequestParam("selection") String selection, ModelMap model) {
    if (selection.equals("吊らない")) {
      ArrayList<String> all = userinfoMapper.selectWolf();
      if (all.contains("人狼")) {
        model.addAttribute("result", "人狼側の勝利");
      } else {
        model.addAttribute("result", "市民側の勝利");
      }
    } else {
      Userinfo userinfo = userinfoMapper.selectUserinfo(selection);
      if (userinfo.getRole().equals("人狼")) {
        model.addAttribute("result", "市民側の勝利");
      } else {
        model.addAttribute("result", "人狼側の勝利");
      }
    }
    return "gameresult";
  }

  @GetMapping("/resume")
  public String resume(Principal prin, SessionStatus SessionStatus) {
    userinfoMapper.updateSelectedFalse(prin.getName());
    userinfoMapper.updateUserInfoNull(prin.getName());
    rolesMapper.updateUserInfoNull();
    SessionStatus.setComplete();
    return "entry";
  }
}
