package team5.game.service;

import java.sql.Time;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import java.util.concurrent.TimeUnit;

@Service
public class AsyncStandbyRoom {
    
  @Async
  public void standby(SseEmitter emitter){
    try {
        while(true){
            if(false == joinNewPlayer){
                TimeUnit.MILLISECONDS.sleep(500);
                continue;
            }
        }
    }
  }  
}
