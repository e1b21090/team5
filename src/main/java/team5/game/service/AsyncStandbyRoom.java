package team5.game.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import ch.qos.logback.classic.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;
import java.util.ArrayList;
import team5.game.model.UserinfoMapper;

@Service
public class AsyncStandbyRoom {

  private boolean max = false;

  private final Logger logger = (Logger) LoggerFactory.getLogger(this.getClass());

  @Autowired
  private UserinfoMapper userinfoMapper;

  @Async
  public void standby(SseEmitter emitter) {
    try {
      while (true) {
        ArrayList<String> joinUserList = userinfoMapper.selectNotnullUses();
        emitter.send(joinUserList);
        logger.info("joinUserList:" + joinUserList);
        TimeUnit.MILLISECONDS.sleep(500);
        if (joinUserList.size() == 4) {
          max = true;
          break;
        }
      }
    } catch (Exception e) {
      logger.warn("Exception:" + e.getClass().getName() + ":" + e.getMessage());
    } finally {
      emitter.complete();
    }
  }
}
