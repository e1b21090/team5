package team5.game.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import team5.game.service.AsyncDate;

@Controller
public class DateController {
    @Autowired
    private AsyncDate asyncDate;
    
    @GetMapping("/date")
    public SseEmitter date(){
        final SseEmitter sseEmmiter = new SseEmitter();
        this.asyncDate.printDate(sseEmmiter);
        return sseEmmiter;
    }

    
}
