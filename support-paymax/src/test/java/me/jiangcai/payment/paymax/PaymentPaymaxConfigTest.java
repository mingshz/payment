package me.jiangcai.payment.paymax;

import com.paymax.model.Charge;
import com.paymax.spring.event.ChargeChangeEvent;
import me.jiangcai.payment.MockPaymentEvent;
import me.jiangcai.payment.test.PaymentTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.event.EventListener;
import org.springframework.test.context.ContextConfiguration;

/**
 * @author CJ
 */
@ContextConfiguration(classes = {PaymentPaymaxConfig.class, PaymentPaymaxConfigTest.Config.class})
public class PaymentPaymaxConfigTest extends PaymentTest {

    @Test
    public void go() throws Exception {
        testOrderFor("拉卡拉微信扫码支付");
    }

    @Configuration
    @PropertySource("classpath:/test_paymax.properties")
    static class Config {
        @Autowired
        private ApplicationEventPublisher applicationEventPublisher;

        @EventListener
        public void event(MockPaymentEvent event) {
            ChargeChangeEvent tradeEvent = new ChargeChangeEvent();
            tradeEvent.setData(new Charge());
            if (event.isSuccess()) {
                tradeEvent.getData().setStatus("SUCCEED");
            } else
                tradeEvent.getData().setStatus("FAILED");

            tradeEvent.getData().setId(event.getId());
//            tradeEvent.setSerialNumber(event.getId());
//            tradeEvent.setTradeTime(LocalDateTime.now());
            applicationEventPublisher.publishEvent(tradeEvent);
        }
    }

}