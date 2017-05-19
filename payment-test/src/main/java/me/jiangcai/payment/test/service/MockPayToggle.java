package me.jiangcai.payment.test.service;

import me.jiangcai.payment.PayableOrder;
import me.jiangcai.payment.entity.PayOrder;

/**
 * 模拟支付的开关
 *
 * @author CJ
 */
public interface MockPayToggle {

    /**
     * @param payableOrder 系统订单
     * @param payOrder     支付订单
     * @return 延迟支付描述；如果是null表示不自动支付
     * @throws Exception 如果打算停止支付就来一个异常吧；这个时候系统会在1秒后发起停止支付
     */
    Integer autoPaySeconds(PayableOrder payableOrder, PayOrder payOrder) throws Exception;
}
