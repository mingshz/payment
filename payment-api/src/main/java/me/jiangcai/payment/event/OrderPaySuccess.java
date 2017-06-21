package me.jiangcai.payment.event;

import me.jiangcai.payment.PayableOrder;
import me.jiangcai.payment.entity.PayOrder;

/**
 * 订单成功支付事件，该事件触发时线程处于安全情况，所以不必设置线程壁垒
 *
 * @author CJ
 */
public class OrderPaySuccess extends PaymentEvent {

    /**
     * @param payableOrder 统一事务内需支付商城订单
     * @param payOrder     统一事务内的支付订单
     */
    public OrderPaySuccess(PayableOrder payableOrder, PayOrder payOrder) {
        super(payableOrder, payOrder);
    }
}
