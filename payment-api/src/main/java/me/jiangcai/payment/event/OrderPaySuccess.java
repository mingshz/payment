package me.jiangcai.payment.event;

import me.jiangcai.payment.PayableOrder;
import me.jiangcai.payment.entity.PayOrder;

/**
 * 订单成功支付事件
 *
 * @author CJ
 */
public class OrderPaySuccess extends PaymentEvent {

    public OrderPaySuccess(PayableOrder payableOrder, PayOrder payOrder) {
        super(payableOrder, payOrder);
    }
}
