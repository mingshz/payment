package me.jiangcai.premier.test;

import me.jiangcai.premier.project.PremierProjectConfig;
import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;

/**
 * @author lxf
 */
@ContextConfiguration(classes = PremierProjectConfig.class)
public class PremierPayConfigTest extends PremierPaymentTest {

    @Test
    public void go() throws Exception {
        testOrderFor("易支付");
    }

}