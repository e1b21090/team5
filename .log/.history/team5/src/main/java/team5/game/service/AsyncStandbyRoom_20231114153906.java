package team5.game.service;

import java.sql.Time;

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
    boolean joinNewPlayer = false;

    boolean max = false;

    private final Logger logger = (Logger) LoggerFactory.getLogger(this.getClass());

    @Autowired
    private UserinfoMapper userinfoMapper;

    @Async
    public boolean standby(SseEmitter emitter){
        try {
            while(true){
                if(false == joinNewPlayer){
                    TimeUnit.MILLISECONDS.sleep(500);
                    continue;
                }
                ArrayList<String> joinUserList = userinfoMapper.selectNotnullUses();
                emitter.send(joinUserList);
                TimeUnit.MILLISECONDS.sleep(500);
                joinNewPlayer = false;
                if(joinUserList.size() == 4){
                    max=true;
                    break;
                }
            }
        }catch (Exception e) {
            logger.warn("Exception:" + e.getClass().getName() + ":" + e.getMessage());
        }finally{
            emitter.complete();
        }
        return max;
    }  
}
