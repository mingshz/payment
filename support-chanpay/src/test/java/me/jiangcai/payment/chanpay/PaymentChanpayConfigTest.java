package me.jiangcai.payment.chanpay;

import me.jiangcai.chanpay.event.TradeEvent;
import me.jiangcai.chanpay.model.TradeStatus;
import me.jiangcai.chanpay.test.ChanpayTestSpringConfig;
import me.jiangcai.demo.project.MockPaymentEvent;
import me.jiangcai.payment.test.PaymentTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.test.context.ContextConfiguration;

import java.time.LocalDateTime;

/**
 * @author CJ
 */
@ContextConfiguration(classes = {PaymentChanpayConfig.class, ChanpayTestSpringConfig.class, PaymentChanpayConfigTest.Config.class})
public class PaymentChanpayConfigTest extends PaymentTest {

    static class Config {
        @Autowired
        private ApplicationEventPublisher applicationEventPublisher;

        @EventListener
        public void event(MockPaymentEvent event) {
            TradeEvent tradeEvent;
            if (event.isSuccess()) {
                tradeEvent = new TradeEvent(TradeStatus.TRADE_SUCCESS);
            } else
                tradeEvent = new TradeEvent(TradeStatus.TRADE_CLOSED);

            tradeEvent.setSerialNumber(event.getId());
            tradeEvent.setTradeTime(LocalDateTime.now());
            applicationEventPublisher.publishEvent(tradeEvent);
        }
    }

    @Test
    public void go() throws Exception {
        testOrderFor("畅捷支付");
    }


}