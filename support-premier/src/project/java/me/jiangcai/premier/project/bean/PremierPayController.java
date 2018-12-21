package me.jiangcai.premier.project.bean;

import me.jiangcai.payment.MockPaymentEvent;
import me.jiangcai.payment.premier.PremierPaymentService;
import me.jiangcai.payment.premier.entity.PremierPayOrder;
import me.jiangcai.payment.service.PaymentGatewayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * @author CJ
 */
@Controller
public class PremierPayController {

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;
    @Autowired
    private PremierPaymentService premierPaymentService;
    @Autowired
    private PaymentGatewayService paymentGatewayService;

    @RequestMapping(method = RequestMethod.PUT, value = "/pay/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void pay(@PathVariable String id, @RequestBody boolean success) {
        // 对于这个玩意儿 是直接调用 推荐是事件！
        applicationEventPublisher.publishEvent(new MockPaymentEvent(id, success));
    }

}
