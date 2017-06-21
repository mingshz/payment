package me.jiangcai.payment.test.service;

import me.jiangcai.demo.pay.DemoPaymentForm;
import me.jiangcai.payment.PayableOrder;
import me.jiangcai.payment.PaymentForm;
import me.jiangcai.payment.entity.PayOrder;
import me.jiangcai.payment.exception.SystemMaintainException;
import me.jiangcai.payment.service.PaymentGatewayService;
import me.jiangcai.payment.service.impl.PaymentServiceImpl;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.PreDestroy;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author CJ
 */
@Service
@Primary
public class TestPaymentService extends PaymentServiceImpl {

    private static final Log log = LogFactory.getLog(TestPaymentService.class);
    private final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
    @Autowired
    private MockPayToggle mockPayToggle;
    @Autowired
    private PaymentGatewayService paymentGatewayService;
    @Autowired
    private DemoPaymentForm demoPaymentForm;

    @Override
    public ModelAndView startPay(HttpServletRequest request, PayableOrder order, PaymentForm form, Map<String, Object> additionalParameters)
            throws SystemMaintainException {
        log.info("使用测试支付方式");
        ModelAndView modelAndView = super.startPay(request, order, demoPaymentForm, additionalParameters);
        PayOrder payOrder = (PayOrder) modelAndView.getModel().get("payOrder");
        PayableOrder payableOrder = (PayableOrder) modelAndView.getModel().get("order");

        try {
            Integer paySeconds = mockPayToggle.autoPaySeconds(payableOrder, payOrder);
            if (paySeconds != null) {
                executorService.schedule(()
                        -> paymentGatewayService.paySuccess(payOrder), paySeconds, TimeUnit.SECONDS);
            }
        } catch (Exception e) {
            executorService.schedule(()
                    -> paymentGatewayService.payCancel(payOrder), 1, TimeUnit.SECONDS);
        }

        return modelAndView;
    }

    @PreDestroy
    public void destroy() {
        executorService.shutdown();
    }
}
