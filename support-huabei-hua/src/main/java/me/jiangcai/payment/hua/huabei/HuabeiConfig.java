package me.jiangcai.payment.hua.huabei;

import me.jiangcai.payment.PaymentConfig;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * JPA需增配 me.jiangcai.payment.hua.huabei.entity
 *
 * @author CJ
 */
@Configuration
@Import(PaymentConfig.class)
@ComponentScan("me.jiangcai.payment.hua.huabei.service")
public class HuabeiConfig {
}
