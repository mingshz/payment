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
     * 支付或取消支付订单
     *
     * @param request
     * @param id      订单id
     * @param success 是否支付
     * @param payUrl  跳转的支付链接
     * @return 视图
     */
    @Transactional
    ModelAndView payOrCancel(HttpServletRequest request, String id, boolean success, String payUrl);


    /**
     * 回调事件处理
     *
     * @param event 事件
     */
    @EventListener
    void callBackEvent(CallBackOrderEvent event);
}
