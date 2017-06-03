package me.jiangcai.payment.paymax;

import com.paymax.spring.event.ChargeChangeEvent;
import me.jiangcai.payment.PaymentForm;
import org.springframework.context.event.EventListener;
import org.springframework.transaction.annotation.Transactional;

/**
 * 参数中支持channel {@link PaymaxChannel}
 *
 * @author CJ
 */
public interface PaymaxPaymentForm extends PaymentForm {

    @Transactional
    @EventListener(ChargeChangeEvent.class)
    void chargeChange(ChargeChangeEvent event);


}
