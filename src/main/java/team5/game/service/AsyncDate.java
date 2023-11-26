package team5.game.service;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import java.util.concurrent.TimeUnit;
import java.text.SimpleDateFormat;

@Service
public class AsyncDate {
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH時mm分ss秒");
    private final Logger logger = LoggerFactory.getLogger(AsyncDate.class);

    @Async
    public void printDate(SseEmitter emitter){
        Date date;
        String formatedDate;

        for(int i=0; i<10; i++){
            try{
                date=new Date();
                formatedDate = simpleDateFormat.format(date);
                logger.info("date: " + date);
                emitter.send(formatedDate);
                TimeUnit.MILLISECONDS.sleep(1000);
            }catch(Exception e){
                logger.warn("Exception: " + e.getClass().getName() + ":" + e.getMessage());
            }
        }
        emitter.complete();
    }
}
