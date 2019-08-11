package me.jiangcai.demo.pay.bean;

import me.jiangcai.demo.pay.DemoPaymentForm;
import me.jiangcai.demo.pay.entity.DemoPayOrder;
import me.jiangcai.payment.MockPaymentEvent;
import me.jiangcai.payment.PayableOrder;
import me.jiangcai.payment.entity.PayOrder;
import me.jiangcai.payment.service.PaymentGatewayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.UUID;

/**
 * @author CJ
 */
@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
@Service
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class DemoPaymentFormImpl implements DemoPaymentForm {

    @Autowired
    private PaymentGatewayService paymentGatewayService;

    @Override
    public PayOrder newPayOrder(HttpServletRequest request, PayableOrder order, Map<String, Object> additionalParameters) {
        PayOrder payOrder = new DemoPayOrder();
        payOrder.setPlatformId(UUID.randomUUID().toString());
        return payOrder;
    }

    @Override
    public void orderMaintain() {

    }

    @Override
    @EventListener(MockPaymentEvent.class)
    public void event(MockPaymentEvent event) {
        // 我们很直接！
        final DemoPayOrder order = paymentGatewayService.getOrder(DemoPayOrder.class, event.getId());
        if (order == null)
            return;
        if (event.isSuccess())
            paymentGatewayService.paySuccess(order);
        else
            paymentGatewayService.payCancel(order);
    }
}
