package team5.game.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CustomLoginController {

  @GetMapping("/custom-login")
  public String showCustomLoginPage() {
    return "custom-login";
  }
}
