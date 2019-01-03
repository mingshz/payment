package me.jiangcai.payment.premier;

import com.paymax.spring.event.ChargeChangeEvent;
import me.jiangcai.payment.PaymentForm;
import me.jiangcai.payment.premier.event.CallBackOrderEvent;
import org.springframework.context.event.EventListener;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

public interface PremierPaymentForm extends PaymentForm {

    @EventListener(ChargeChangeEvent.class)
    void chargeChange(ChargeChangeEvent event);


    /**
     * 回调事件处理
     *
     * @param event 事件
     */
    @EventListener
    void callBackEvent(CallBackOrderEvent event);
}
