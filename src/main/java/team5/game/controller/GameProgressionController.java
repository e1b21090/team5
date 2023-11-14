package team5.game.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import team5.game.model.RolesMapper;
import team5.game.model.UserinfoMapper;
import org.springframework.ui.ModelMap;
import team5.game.model.Userinfo;

@Controller
public class GameProgressionController {
    @Autowired
    private UserinfoMapper userinfoMapper;

    @Autowired
    private RolesMapper rolesMapper;

    @GetMapping("/uranai")
    public String soothsayer(@RequestParam("target") String target, ModelMap model, Principal prin){
        Userinfo predictTarget = userinfoMapper.selectUserinfo(target);
        model.addAttribute("predictTarget", predictTarget);
        Userinfo userinfo = userinfoMapper.selectUserinfo(prin.getName());
        model.addAttribute("userinfo", userinfo);
        return "game";
    }

    @GetMapping("/kaito")
    public String thief(@RequestParam("target") String target, ModelMap model, Principal prin){
        Userinfo stealTarget = userinfoMapper.selectUserinfo(target);
        userinfoMapper.updateUserInfo(stealTarget.getRole(), prin.getName());
        userinfoMapper.updateUserInfo("怪盗", target);
        Userinfo userinfo = userinfoMapper.selectUserinfo(prin.getName());
        model.addAttribute("userinfo", userinfo);
        return "game";
    }
}
