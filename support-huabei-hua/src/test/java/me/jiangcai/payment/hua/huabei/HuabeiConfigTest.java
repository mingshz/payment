package me.jiangcai.payment.hua.huabei;

import me.jiangcai.payment.MockPaymentEvent;
import me.jiangcai.payment.hua.huabei.entity.HuaHuabeiPayOrder;
import me.jiangcai.payment.service.PaymentGatewayService;
import me.jiangcai.payment.test.PaymentTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

/**
 * @author CJ
 */
@ContextConfiguration(classes = {HuabeiConfig.class, HuabeiConfigTest.Config.class})
public class HuabeiConfigTest extends PaymentTest {

    @Test
    public void go() throws Exception {
        testOrderFor("华院花呗支付");
    }

    @Configuration
//    @PropertySource("classpath:/test_paymax.properties")
    static class Config {
        @Autowired
        private ApplicationEventPublisher applicationEventPublisher;
        @Autowired
        private PaymentGatewayService paymentGatewayService;
        @SuppressWarnings("SpringJavaAutowiringInspection")
        @Autowired
        private EntityManager entityManager;

        @EventListener
        @Transactional
        public void event(MockPaymentEvent event) {
            final CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            CriteriaQuery<HuaHuabeiPayOrder> cq = cb.createQuery(HuaHuabeiPayOrder.class);
            Root<HuaHuabeiPayOrder> root = cq.from(HuaHuabeiPayOrder.class);
            HuaHuabeiPayOrder payOrder = entityManager.createQuery(cq
                    .where(cb.equal(root.get("platformId"), event.getId()))
            )
                    .getSingleResult();
            if (event.isSuccess())
                paymentGatewayService.paySuccess(payOrder);
            else
                paymentGatewayService.payCancel(payOrder);
//
//            ChargeChangeEvent tradeEvent = new ChargeChangeEvent();
//            tradeEvent.setData(new Charge());
//            if (event.isSuccess()) {
//                tradeEvent.getData().setStatus("SUCCEED");
//            } else
//                tradeEvent.getData().setStatus("FAILED");
//
//            tradeEvent.getData().setId(event.getId());
////            tradeEvent.setSerialNumber(event.getId());
////            tradeEvent.setTradeTime(LocalDateTime.now());
//            applicationEventPublisher.publishEvent(tradeEvent);
        }
    }

}