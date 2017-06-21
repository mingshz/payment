package me.jiangcai.demo.project.bean;

import me.jiangcai.payment.MockPaymentEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author CJ
 */
@Controller
public class DemoPayController {

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;


    @RequestMapping(method = RequestMethod.PUT, value = "/pay/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void pay(@PathVariable String id, @RequestBody boolean success) {
        // 对于这个玩意儿 是直接调用 推荐是事件！
        applicationEventPublisher.publishEvent(new MockPaymentEvent(id, success));
    }

}
