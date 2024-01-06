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
import team5.game.service.AsyncVotePhase;
import team5.game.service.BulletinBoardService;
import team5.game.service.AsyncVoteFinish;
import team5.game.model.Gamelog;
import team5.game.model.GamelogMapper;

@Controller
@SessionAttributes("userinfo")
public class JinroController {

  // 既に生成された数値を格納
  Set<Integer> uniqueNumbers = new HashSet<>();

  // 怪盗が盗む対象
  Userinfo stealTarget;

  // 怪盗
  Userinfo thief;

  // 各プレイヤーの投票数
  int voteCount0; // 吊らない
  int voteCount1; // user1
  int voteCount2; // user2
  int voteCount3; // user3
  int voteCount4; // user4

  int game_num = 0;

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
  private AsyncVotePhase asyncVotePhase;

  @Autowired
  private AsyncVoteFinish asyncVoteFinish;

  @Autowired
  private BulletinBoardService bulletinBoardService;

  @Autowired
  private GamelogMapper gamelogMapper;

  // タイトル画面
  @GetMapping("/title")
  public String title() {
    return "title";
  }

  // ルール画面
  @GetMapping("/rules")
  public String rules() {
    return "rules";
  }

  // 待機画面(非同期)
  @GetMapping("/standbyroom")
  public SseEmitter standbyroom() {
    final SseEmitter emitter = new SseEmitter();
    this.asyncStandbyRoom.standby(emitter);
    return emitter;
  }

  // 待機画面
  @GetMapping("/standby")
  public String standby(Principal prin, ModelMap model, SessionStatus SessionStatus) {
    if (!model.containsAttribute("userinfo")) {
      Game game = new Game();
      int num = game.drawGame(uniqueNumbers);
      uniqueNumbers.add(num); // 数値の重複を防ぐためSetにランダム生成した値を記憶

      Roles roles = rolesMapper.selectRoles(num); // 役職を取得
      userinfoMapper.updateUserInfo(roles.getName(), prin.getName()); // DBに役職を登録
      Userinfo userinfo = userinfoMapper.selectUserinfo(prin.getName()); // DBからユーザ情報を取得
      rolesMapper.updateUserInfo(num); // DBに役職を登録

      model.addAttribute("userinfo", userinfo);
    }
    return "standby";
  }

  // 役職確認画面(非同期)
  @GetMapping("/checkcard")
  public SseEmitter checkcard() {
    final SseEmitter emitter = new SseEmitter();
    this.asyncCheck.check(emitter);
    return emitter;
  }

  // 役職確認画面
  @GetMapping("/check")
  public String check(Principal prin, ModelMap model) {
    Userinfo userinfo = userinfoMapper.selectUserinfo(prin.getName()); // DBからユーザ情報を取得
    // 人狼の場合
    if (userinfo.getRole().equals("人狼")) {
      boolean isJinro = true;
      String jinro = userinfoMapper.selectJinro(prin.getName());
      model.addAttribute("jinro", jinro);
      model.addAttribute("isJinro", isJinro);
    }
    // 占い師の場合
    if (userinfo.getRole().equals("占い師")) {
      ArrayList<Userinfo> uranai = userinfoMapper.selectTarget(prin.getName());
      model.addAttribute("uranai", uranai);
    }
    // 怪盗の場合
    if (userinfo.getRole().equals("怪盗")) {
      ArrayList<Userinfo> kaito = userinfoMapper.selectTarget(prin.getName());
      model.addAttribute("kaito", kaito);
    }
    // 市民の場合
    if (userinfo.getRole().equals("市民")) {
      ArrayList<Userinfo> simin = userinfoMapper.selectTarget(prin.getName());
      model.addAttribute("simin", simin);
    }

    model.addAttribute("userinfo", userinfo);
    return "check";
  }

  // 夜の画面
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

  // 夜の画面(占い師)
  @GetMapping("/uranai")
  public String soothsayer(@RequestParam("target") String target, ModelMap model, Principal prin) {
    // 墓場の場合
    if (target.equals("graveyard")) {
      ArrayList<Roles> graveyard = rolesMapper.selectGraveyard();
      model.addAttribute("graveyard", graveyard);
    } else { // 墓場以外の場合
      Userinfo predictTarget = userinfoMapper.selectUserinfo(target);
      model.addAttribute("predictTarget", predictTarget);
    }
    Userinfo soothsayer = userinfoMapper.selectUserinfo(prin.getName()); // DBからユーザ情報を取得
    model.addAttribute("soothsayer", soothsayer);
    return "outcome";
  }

  // 夜の画面(怪盗)
  @GetMapping("/kaito")
  public String thief(@RequestParam("target") String target, ModelMap model, Principal prin) {
    this.stealTarget = userinfoMapper.selectUserinfo(target);
    this.thief = userinfoMapper.selectUserinfo(prin.getName());
    model.addAttribute("thief", thief);
    model.addAttribute("stealTarget", stealTarget);
    return "outcome";
  }

  // 夜の画面(非同期)
  @GetMapping("/movekaigi")
  public SseEmitter movekaigi() {
    final SseEmitter emitter = new SseEmitter();
    this.asyncKaigi.kaigi(emitter);
    return emitter;
  }

  // 夜の画面-役職有(非同期)
  @GetMapping("/toRoles")
  public SseEmitter toRoles() {
    final SseEmitter emitter = new SseEmitter();
    this.asyncToRoles.toRoles(emitter);
    return emitter;
  }

  // 昼の画面-投票
  @GetMapping("/vote")
  public String vote(Principal prin, ModelMap model) {
    if (this.stealTarget != null && this.thief.getRole().equals("怪盗")) { // 怪盗が盗んだ場合
      userinfoMapper.updateUserInfo(stealTarget.getRole(), thief.getUsername());
      userinfoMapper.updateUserInfo("怪盗", stealTarget.getUsername());
    }
    ArrayList<Userinfo> v_target = userinfoMapper.selectTarget(prin.getName()); // 投票対象を取得
    model.addAttribute("v_target", v_target);
    return "vote";
  }

  // 昼の画面-投票(非同期)
  @GetMapping("/votePhase")
  public SseEmitter votePhase() {
    final SseEmitter emitter = new SseEmitter();
    this.asyncVotePhase.votePhase(emitter);
    return emitter;
  }

  // 昼の画面-投票結果待機
  @GetMapping("/waitingvoteresult")
  public String waitingvoteresult(@RequestParam("selection") String selection, ModelMap model, Principal prin) {
    if (selection.equals("吊らない")) { // 吊らない場合
      voteCount0++; // 吊らないの投票数をカウント
    } else if (selection.equals("user1")) { // user1に投票した場合
      voteCount1++; // user1の投票数をカウント
    } else if (selection.equals("user2")) { // user2を投票した場合
      voteCount2++; // user2の投票数をカウント
    } else if (selection.equals("user3")) { // user3を投票した場合
      voteCount3++; // user3の投票数をカウント
    } else if (selection.equals("user4")) { // user4を投票した場合
      voteCount4++; // user4の投票数をカウント
    }
    model.addAttribute("selection", selection);
    userinfoMapper.updateSelectedTrue(prin.getName()); // 投票済みに変更
    Userinfo userinfo = userinfoMapper.selectUserinfo(prin.getName());
    model.addAttribute("userinfo", userinfo);
    return "waitingvoteresult";
  }

  // 投票結果画面
  @GetMapping("/voteresult")
  public String voteresult(ModelMap model) {
    if (voteCount1 > voteCount0 && voteCount1 > voteCount2 && voteCount1 > voteCount3 && voteCount1 > voteCount4) { // user1を吊る場合
      model.addAttribute("selection", "user1");
    } else if (voteCount2 > voteCount0 && voteCount2 > voteCount1 && voteCount2 > voteCount3
        && voteCount2 > voteCount4) { // user2を吊る場合
      model.addAttribute("selection", "user2");
    } else if (voteCount3 > voteCount0 && voteCount3 > voteCount1 && voteCount3 > voteCount2
        && voteCount3 > voteCount4) { // user3を吊る場合
      model.addAttribute("selection", "user3");
    } else if (voteCount4 > voteCount0 && voteCount4 > voteCount1 && voteCount4 > voteCount2
        && voteCount4 > voteCount3) { // user4を吊る場合
      model.addAttribute("selection", "user4");
    } else { // 吊らない場合
      model.addAttribute("selection", "吊らない");
    }
    model.addAttribute("count_0", voteCount0);
    model.addAttribute("count_1", voteCount1);
    model.addAttribute("count_2", voteCount2);
    model.addAttribute("count_3", voteCount3);
    model.addAttribute("count_4", voteCount4);
    return "voteresult";
  }

  // 投票結果画面(非同期)
  @GetMapping("/voteFinish")
  public SseEmitter voteFinish() {
    final SseEmitter emitter = new SseEmitter();
    this.asyncVoteFinish.voteFinish(emitter);
    return emitter;
  }

  // ゲーム結果画面
  @GetMapping("/gameresult")
  public String gameresult(@RequestParam("selection") String selection, ModelMap model, Principal prin) {
    String result = null;
    ArrayList<String> winner;
    if (selection.equals("吊らない")) { // 吊らない場合
      ArrayList<String> all = userinfoMapper.selectWolf(); // 全員の役職を取得
      if (all.contains("人狼")) { // 人狼がいる場合
        model.addAttribute("result", "人狼側の勝利");
        result = "人狼側の勝利";
      } else { // 人狼がいない場合
        model.addAttribute("result", "市民側の勝利");
        result = "市民側の勝利";
      }
    } else { // 吊る場合
      Userinfo userinfo = userinfoMapper.selectUserinfo(selection); // 吊る対象のユーザ情報を取得
      if (userinfo.getRole().equals("人狼")) { // 人狼を吊った場合
        model.addAttribute("result", "市民側の勝利");
        result = "市民側の勝利";
      } else { // 人狼以外を吊った場合
        model.addAttribute("result", "人狼側の勝利");
        result = "人狼側の勝利";
      }
    }
    // if (result.equals("人狼側の勝利")) {
    //   winner = userinfoMapper.select_Jinro();
    // } else {
    //   winner = userinfoMapper.selectNotJinro();
    // }
    // model.addAttribute("winner", winner);
    // Userinfo userinfo = userinfoMapper.selectUserinfo(prin.getName());
    // model.addAttribute("role", userinfo.getRole());
    if (prin.getName().equals("user1")){
      Gamelog gamelog = new Gamelog();
      gamelog.setName1(userinfoMapper.selectUserinfo("user1").getUsername());
      gamelog.setRole1(userinfoMapper.selectUserinfo("user1").getRole());
      gamelog.setName2(userinfoMapper.selectUserinfo("user2").getUsername());
      gamelog.setRole2(userinfoMapper.selectUserinfo("user2").getRole());
      gamelog.setName3(userinfoMapper.selectUserinfo("user3").getUsername());
      gamelog.setRole3(userinfoMapper.selectUserinfo("user3").getRole());
      gamelog.setName4(userinfoMapper.selectUserinfo("user4").getUsername());
      gamelog.setRole4(userinfoMapper.selectUserinfo("user4").getRole());
      gamelog.setResult(result);
      gamelogMapper.insertGamelog(gamelog);
    }
    ArrayList<Userinfo> userinfo = userinfoMapper.select4userinfo(game_num);
    model.addAttribute("finalUserinfo", userinfo);
    return "gameresult";
  }

  // タイトルに戻る際の処理
  @GetMapping("/resume")
  public String resume(Principal prin, SessionStatus SessionStatus) {
    userinfoMapper.updateSelectedFalse(prin.getName()); // 投票済みをリセット
    userinfoMapper.updateUserInfoNull(prin.getName()); // DBの役職をリセット
    rolesMapper.updateUserInfoNull(); // DBの役職をリセット
    SessionStatus.setComplete(); // セッションを破棄
    uniqueNumbers.clear(); // 重複防止用のSetをリセット
    bulletinBoardService.resetMessages(); // 掲示板をリセット
    voteCount0 = 0;
    voteCount1 = 0;
    voteCount2 = 0;
    voteCount3 = 0;
    voteCount4 = 0; // 投票数をリセット
    game_num += 4;
    return "title";
  }
}
