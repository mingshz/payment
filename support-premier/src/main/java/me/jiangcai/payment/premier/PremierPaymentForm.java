package me.jiangcai.payment.premier;

import com.paymax.spring.event.ChargeChangeEvent;
import me.jiangcai.payment.PaymentForm;
import org.springframework.context.event.EventListener;

public interface PremierPaymentForm extends PaymentForm {

    @EventListener(ChargeChangeEvent.class)
    void chargeChange(ChargeChangeEvent event);

}
