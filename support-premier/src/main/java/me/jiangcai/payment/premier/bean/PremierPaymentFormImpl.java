package me.jiangcai.payment.premier.bean;

import com.paymax.spring.event.ChargeChangeEvent;
import me.jiangcai.payment.PayableOrder;
import me.jiangcai.payment.entity.PayOrder;
import me.jiangcai.payment.exception.SystemMaintainException;
import me.jiangcai.payment.premier.PayType;
import me.jiangcai.payment.premier.PremierPaymentForm;
import me.jiangcai.payment.premier.entity.PremierPayOrder;
import me.jiangcai.payment.service.PaymentGatewayService;
import me.jiangcai.premier.project.even.MockNotifyEven;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Map;

@Service
public class PremierPaymentFormImpl implements PremierPaymentForm {

    private String customerId;
    private String notifyUrl;
    private String backUrl;
    @Autowired
    private PaymentGatewayService paymentGatewayService;
    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    @Autowired
    public PremierPaymentFormImpl(Environment environment) {
        customerId = environment.getProperty("premier.customerId", "");
        notifyUrl = environment.getProperty("premier.notifyUrl", "");
        backUrl = environment.getProperty("premier.backUrl", "");
    }

    @Override
    public PayOrder newPayOrder(HttpServletRequest request, PayableOrder order, Map<String, Object> additionalParameters) throws SystemMaintainException {
        PremierPayOrder payOrder = new PremierPayOrder();
        payOrder.setAmount(order.getOrderDueAmount());
        payOrder.setMark(order.getOrderProductName());
        payOrder.setRemarks(order.getOrderBody());
        payOrder.setPayableOrderId(order.getPayableOrderId().toString());
        payOrder.setCustomerId(customerId);
        payOrder.setNotifyUrl(notifyUrl);
        payOrder.setBackUrl(backUrl);
        payOrder.setPlatformId(order.getPayableOrderId().toString());
        int type = (int) additionalParameters.get("type");
        PayType payType = PayType.byCode(type);
        payOrder.setPayType(payType);
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
        order.setOrderStatus(event.getData().getStatus());
        if (!order.isCancel()) {
            if ("SUCCEED".equals(order.getOrderStatus())) {
//                paymentGatewayService.paySuccess(order);
                //这里改成异步回调去修改订单状态,改场景就发布一个新的事件模拟异步回调
                applicationEventPublisher.publishEvent(new MockNotifyEven(order.getPlatformId()));
            } else if ("FAILED".equals(order.getOrderStatus())) {
                paymentGatewayService.payCancel(order);
            }
        }
    }
}
