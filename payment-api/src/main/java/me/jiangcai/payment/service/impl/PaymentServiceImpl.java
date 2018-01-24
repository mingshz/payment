package me.jiangcai.payment.service.impl;

import me.jiangcai.payment.PayableOrder;
import me.jiangcai.payment.PaymentForm;
import me.jiangcai.payment.entity.PayOrder;
import me.jiangcai.payment.exception.SystemMaintainException;
import me.jiangcai.payment.service.PayableSystemService;
import me.jiangcai.payment.service.PaymentGatewayService;
import me.jiangcai.payment.service.PaymentService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * @author CJ
 */
@Service
public class PaymentServiceImpl implements PaymentService {

    private static final Log log = LogFactory.getLog(PaymentServiceImpl.class);
    @Autowired
    private PayableSystemService payableSystemService;
    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    private EntityManager entityManager;
    @Autowired
    private PaymentGatewayService paymentGatewayService;

    @Override
    public ModelAndView startPay(HttpServletRequest request, PayableOrder order, PaymentForm form, Map<String, Object> additionalParameters) throws SystemMaintainException {
        // 我们并不需要告诉order 我们的支付细节！
        additionalParameters.put("notifyUrl","/_payment/notify");
        PayOrder payOrder = form.newPayOrder(request, order, additionalParameters);
        payOrder.setStartTime(LocalDateTime.now());
        payOrder.setPayableOrderId(order.getPayableOrderId().toString());
        entityManager.persist(payOrder);
        entityManager.flush();

        if (additionalParameters == null) {
            additionalParameters = new HashMap<>();
        }
        additionalParameters.put("checkUri", "/_payment/completed/" + order.getPayableOrderId());
        additionalParameters.put("successUri", "/_payment/success/" + order.getPayableOrderId());
        additionalParameters.put("order", order);
        additionalParameters.put("payOrder", payOrder);

        ModelAndView modelAndView = payableSystemService.pay(request, order, payOrder, additionalParameters);
        // 还将对它进行处理 给它几个控制地址
        modelAndView.addObject("order", order);
        modelAndView.addObject("payOrder", payOrder);
        modelAndView.addObject("checkUri", "/_payment/completed/" + order.getPayableOrderId());
        modelAndView.addObject("successUri", "/_payment/success/" + order.getPayableOrderId());
        // 提供默认实现
        return modelAndView;
    }

    @Override
    public PayOrder payOrder(long id) {
        return entityManager.getReference(PayOrder.class, id);
    }

    @Override
    public boolean mockPay(PayableOrder order) {
        try {
            log.info("准备模拟支付" + order);
            final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
            CriteriaQuery<PayOrder> orderCriteriaQuery = criteriaBuilder.createQuery(PayOrder.class);
            Root<PayOrder> root = orderCriteriaQuery.from(PayOrder.class);
            PayOrder payOrder = entityManager.createQuery(orderCriteriaQuery
                    .where(criteriaBuilder.equal(root.get("payableOrderId"), order.getPayableOrderId().toString())))
                    .getResultList().stream()
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("找不到付款订单"));

            if (payOrder.isSuccess())
                return true;

            paymentGatewayService.paySuccess(payOrder);
            return true;
        } catch (Throwable ex) {
            log.error("模拟支付时出现问题", ex);
            return false;
        }
    }
}
