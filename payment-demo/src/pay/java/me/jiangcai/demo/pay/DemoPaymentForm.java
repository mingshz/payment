package me.jiangcai.demo.pay;

import me.jiangcai.demo.project.MockPaymentEvent;
import me.jiangcai.payment.PaymentForm;
import org.springframework.context.event.EventListener;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author CJ
 */
public interface DemoPaymentForm extends PaymentForm {

    @Transactional
    @EventListener(MockPaymentEvent.class)
    void event(MockPaymentEvent event);
}
