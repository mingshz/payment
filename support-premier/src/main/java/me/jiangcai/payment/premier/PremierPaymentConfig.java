package me.jiangcai.payment.premier;

import com.paymax.spring.PaymaxSpringConfig;
import me.jiangcai.payment.PaymentConfig;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

/**
 * 易支付配置
 * JPA需增配 me.jiangcai.payment.hua.huabei.entity
 *
 * @author lxf
 */
@ComponentScan({"me.jiangcai.payment.premier.service", "me.jiangcai.payment.service"})
@Import({PaymentConfig.class})
public class PremierPaymentConfig {
}
