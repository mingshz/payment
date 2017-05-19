package me.jiangcai.payment.chanpay.service;

import me.jiangcai.chanpay.event.TradeEvent;
import me.jiangcai.payment.PaymentForm;
import org.springframework.context.event.EventListener;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.security.SignatureException;

/**
 * 畅捷支付通道
 * <p>它可以提供额外参数</p>
 * <ul>
 * <li>desktop 桌面支付，支持boolean如果不传入默认微信支付</li>
 * </ul>
 *
 * @author CJ
 */
public interface ChanpayPaymentForm extends PaymentForm {
    /**
     * 用以接受实际支付事件
     *
     * @param event 实际支付事件
     */
    @EventListener(TradeEvent.class)
    @Transactional
    void tradeUpdate(TradeEvent event) throws IOException, SignatureException;
}
