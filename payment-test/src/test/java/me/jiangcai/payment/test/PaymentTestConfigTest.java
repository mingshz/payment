package me.jiangcai.payment.test;

import me.jiangcai.demo.pay.DemoPayConfig;
import me.jiangcai.payment.test.service.MockPayToggle;
import org.junit.Test;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;

/**
 * @author CJ
 */
@ContextConfiguration(classes = {PaymentTestConfig.class, DemoPayConfig.class, PaymentTestConfigTest.Config.class})
public class PaymentTestConfigTest extends PaymentTest {

    @Configuration
    static class Config {
        @Bean
        public MockPayToggle mockPayToggle() {
            return (payableOrder, payOrder) -> 1;
        }
    }

    @Test
    public void go() {
        makeOrderFor("Demo");
        assertPaySuccess();
    }

}