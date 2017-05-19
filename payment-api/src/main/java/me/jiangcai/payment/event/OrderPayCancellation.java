package me.jiangcai.payment.event;

import me.jiangcai.payment.PayableOrder;
import me.jiangcai.payment.entity.PayOrder;

/**
 * 订单支付取消事件
 *
 * @author CJ
 */
public class OrderPayCancellation extends PaymentEvent{
    public OrderPayCancellation(PayableOrder payableOrder, PayOrder payOrder) {
        super(payableOrder, payOrder);
    }
}
