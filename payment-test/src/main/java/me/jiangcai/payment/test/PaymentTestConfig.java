package me.jiangcai.payment.test;

import me.jiangcai.demo.pay.DemoPayConfig;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * 需要依赖{@link me.jiangcai.payment.test.service.MockPayToggle}
 *
 * @author CJ
 */
@Configuration
@ComponentScan("me.jiangcai.payment.test.service")
@Import(DemoPayConfig.class)
public class PaymentTestConfig {

}
