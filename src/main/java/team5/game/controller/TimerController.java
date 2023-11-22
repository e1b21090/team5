package team5.game.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import team5.game.service.AsyncCount;

@Controller
@RequestMapping("/timer")
public class TimerController {
  private final Logger logger = LoggerFactory.getLogger(TimerController.class);

  @Autowired
  private AsyncCount ac56;

  @GetMapping("seconds")
  public SseEmitter pushCount() {
    logger.info("pushCount");
    final SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);

    try {
      this.ac56.countDown(emitter);
    } catch (IOException e) {
      logger.warn("Exception:" + e.getClass().getName() + ":" + e.getMessage());
      emitter.complete();
    }
    return emitter;
  }

}
