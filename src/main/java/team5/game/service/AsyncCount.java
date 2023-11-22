package team5.game.service;

import org.springframework.scheduling.annotation.Async;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Service
public class AsyncCount {
  int count = 20;
  private final Logger logger = LoggerFactory.getLogger(AsyncCount.class);

  @Async
  public void countDown(SseEmitter emitter) throws IOException {
    logger.info("countDown start");
    try {
      while (count >= 0) {
        logger.info("send:" + count);
        emitter.send(count);
        count--;
        TimeUnit.SECONDS.sleep(1);
      }
      emitter.send("20秒立ちました");
      emitter.complete();
    } catch (InterruptedException e) {
      logger.warn("Exception:" + e.getClass().getName() + ":" + e.getMessage());
    }
  }
}
