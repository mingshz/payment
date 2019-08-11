package me.jiangcai.payment.chanpay;

import me.jiangcai.chanpay.config.ChanpayConfig;
import me.jiangcai.payment.PaymentConfig;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

/**
 * 欢迎使用畅捷支付！
 * 载入后{@link me.jiangcai.payment.chanpay.service.ChanpayPaymentForm}可用
 * me.jiangcai.payment.chanpay.entity 需增配到JPA
 *
 * @author CJ
 */
@SuppressWarnings("WeakerAccess")
@ComponentScan("me.jiangcai.payment.chanpay.service")
@Import({ChanpayConfig.class, PaymentConfig.class})
public class PaymentChanpayConfig {
}
