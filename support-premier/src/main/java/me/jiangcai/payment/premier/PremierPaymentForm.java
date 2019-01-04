package me.jiangcai.payment.premier;

import me.jiangcai.payment.PaymentForm;
import me.jiangcai.payment.premier.event.CallBackOrderEvent;
import org.springframework.context.event.EventListener;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

public interface PremierPaymentForm extends PaymentForm {



    /**
     * 回调事件处理
     *
     * @param event 事件
     */
    @EventListener
    void callBackEvent(CallBackOrderEvent event);
}
