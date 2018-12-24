package me.jiangcai.premier.test;

import org.junit.Test;

public class RunTest extends PremierPaymentTest {

    @Test
    public void go() throws Exception {
        testOrderFor("易支付");
    }

}
