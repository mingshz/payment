package me.jiangcai.payment.paymax;

import com.paymax.spring.PaymaxSpringConfig;
import me.jiangcai.payment.PaymentConfig;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

/**
 * 欢迎使用畅捷支付！
 * 载入后{@link me.jiangcai.payment.paymax.PaymaxPaymentForm}可用
 * me.jiangcai.payment.paymax.entity 需增配到JPA
 *
 * @author CJ
 */
@ComponentScan("me.jiangcai.payment.paymax.service")
@Import({PaymaxSpringConfig.class, PaymentConfig.class})
public class PaymentPaymaxConfig {
}
