package me.jiangcai.payment.test;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * 需要依赖{@link me.jiangcai.payment.test.service.MockPayToggle}
 *
 * @author CJ
 */
@Configuration
@ComponentScan("me.jiangcai.payment.test.service")
public class PaymentTestConfig {

}
