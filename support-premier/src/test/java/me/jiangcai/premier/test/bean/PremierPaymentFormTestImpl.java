package me.jiangcai.premier.test.bean;

import com.paymax.spring.event.ChargeChangeEvent;
import me.jiangcai.payment.PayableOrder;
import me.jiangcai.payment.entity.PayOrder;
import me.jiangcai.payment.exception.SystemMaintainException;
import me.jiangcai.payment.premier.PremierPaymentForm;
import me.jiangcai.payment.premier.entity.PremierPayOrder;
import me.jiangcai.payment.service.PaymentGatewayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Map;

@Component
@Primary
public class PremierPaymentFormTestImpl implements PremierPaymentForm {
    @Autowired
    private PaymentGatewayService paymentGatewayService;
    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    @Override
    public PayOrder newPayOrder(HttpServletRequest request, PayableOrder order, Map<String, Object> additionalParameters) throws SystemMaintainException {
        PremierPayOrder payOrder = new PremierPayOrder();
        payOrder.setAliPayCodeUrl("http://www.baidu.com");
        payOrder.setPayableOrderId(order.getPayableOrderId().toString());
        payOrder.setPlatformId(order.getPayableOrderId().toString());
        return payOrder;
    }

    @Override
    public void orderMaintain() {

    }

    @Override
    public boolean isSupportPayOrderStatusQuerying() {
        return false;
    }

    @Override
    public void queryPayStatus(PayOrder order) {
        System.out.println("不支持订单状态查询");
    }

    @Override
    public void chargeChange(ChargeChangeEvent event) {
        PremierPayOrder order = paymentGatewayService.getOrder(PremierPayOrder.class, event.getData().getId());
        if (order == null) {
            return;
        }
        order.setEventTime(LocalDateTime.now());
        if (!order.isCancel()) {
            if ("SUCCEED".equals(event.getData().getStatus())) {
                paymentGatewayService.paySuccess(order);
            } else if ("FAILED".equals(event.getData().getStatus())) {
                paymentGatewayService.payCancel(order);
            }
        }
    }
}
