package me.jiangcai.demo.pay;

import me.jiangcai.payment.test.PaymentTest;
import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;

/**
 * @author CJ
 */
@ContextConfiguration(classes = DemoPayConfig.class)
public class DemoPayConfigTest extends PaymentTest {

    @Test
    public void go() throws Exception {
        testOrderFor("Demo");
    }

}