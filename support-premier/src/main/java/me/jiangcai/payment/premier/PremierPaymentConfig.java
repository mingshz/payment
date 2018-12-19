package me.jiangcai.payment.premier;

import me.jiangcai.payment.PaymentConfig;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

/**
 * 易支付配置
 * JPA需增配 me.jiangcai.payment.premier.entity
 *
 * @author lxf
 */
@ComponentScan({"me.jiangcai.payment.premier.bean"})
@Import({PaymentConfig.class})
public class PremierPaymentConfig {
}
