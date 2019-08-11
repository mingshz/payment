package me.jiangcai.payment.service.impl;

import me.jiangcai.payment.PaymentForm;
import me.jiangcai.payment.entity.PayOrder;
import me.jiangcai.payment.entity.PayOrder_;
import me.jiangcai.payment.event.OrderPayCancellation;
import me.jiangcai.payment.event.OrderPaySuccess;
import me.jiangcai.payment.event.PaymentEvent;
import me.jiangcai.payment.service.PayableSystemService;
import me.jiangcai.payment.service.PaymentGatewayService;
import me.jiangcai.payment.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.time.LocalDateTime;

/**
 * @author CJ
 */
@SuppressWarnings({"SpringJavaInjectionPointsAutowiringInspection", "SpringJavaAutowiredFieldsWarningInspection"})
@Service
public class PaymentGatewayServiceImpl implements PaymentGatewayService {

    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    private EntityManager entityManager;
    @Autowired
    private ApplicationContext applicationContext;
    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;
    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    private PayableSystemService payableSystemService;
    @Autowired
    private PaymentService paymentService;

    @Override
    public <T extends PayOrder> T getOrder(Class<T> type, String platformId) {
        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<T> criteriaQuery = criteriaBuilder.createQuery(type);
        Root<T> root = criteriaQuery.from(type);
        criteriaQuery = criteriaQuery.where(criteriaBuilder.equal(root.get(PayOrder_.platformId), platformId));
        TypedQuery<T> query = entityManager.createQuery(criteriaQuery);
        try {
            return query.getSingleResult();
        } catch (NoResultException ignored) {
            return null;
        }
    }

    @Override
    public <T extends PayOrder> T getOrderByMerchantOrderId(Class<T> type, String merchantOrderId) {
        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<T> criteriaQuery = criteriaBuilder.createQuery(type);
        Root<T> root = criteriaQuery.from(type);
        criteriaQuery = criteriaQuery.where(criteriaBuilder.equal(root.get(PayOrder_.merchantOrderId), merchantOrderId));
        TypedQuery<T> query = entityManager.createQuery(criteriaQuery);
        try {
            return query.getSingleResult();
        } catch (NoResultException ignored) {
            return null;
        }
    }

    @Override
    public void makeEvent(PaymentEvent event) {
        synchronized (event.makeKey()) {
            applicationEventPublisher.publishEvent(event);
        }
    }

    @Override
    public void paySuccess(PayOrder order) {
        order.setFinishTime(LocalDateTime.now());
        order.setSuccess(true);
        entityManager.merge(order);
        applicationContext.getBean(PaymentGatewayService.class)
                .makeEvent(new OrderPaySuccess(payableSystemService.getOrder(order.getPayableOrderId()), order));
    }

    @Override
    public void payCancel(PayOrder order) {
        order.setFinishTime(LocalDateTime.now());
        order.setCancel(true);
        entityManager.merge(order);
        applicationContext.getBean(PaymentGatewayService.class)
                .makeEvent(new OrderPayCancellation(payableSystemService.getOrder(order.getPayableOrderId()), order));
    }

    @Override
    public PayOrder getSuccessOrder(String payableOrderId) {
        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<PayOrder> criteriaQuery = criteriaBuilder.createQuery(PayOrder.class);
        Root<PayOrder> root = criteriaQuery.from(PayOrder.class);

        criteriaQuery = criteriaQuery
                .where(criteriaBuilder
                        .and(criteriaBuilder.equal(root.get("payableOrderId"), payableOrderId)
                                , criteriaBuilder.isTrue(root.get("success"))));
        TypedQuery<PayOrder> query = entityManager.createQuery(criteriaQuery);
        return query.getSingleResult();
    }

    @Override
    public PayOrder getLatestOrder(String payableOrderId) {
        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<PayOrder> criteriaQuery = criteriaBuilder.createQuery(PayOrder.class);
        Root<PayOrder> root = criteriaQuery.from(PayOrder.class);
        criteriaQuery = criteriaQuery
                .where(criteriaBuilder.equal(root.get("payableOrderId"), payableOrderId))
                .orderBy(criteriaBuilder.desc(root.get("startTime")));
        TypedQuery<PayOrder> query = entityManager.createQuery(criteriaQuery).setMaxResults(1);
        try {
            return query.getSingleResult();
        } catch (NoResultException ex) {
            return null;
        }
    }

    @Override
    public void queryPayStatus(PayOrder order) {
        PaymentForm form = paymentService.requestPaymentForm(order.getPaymentFormClass(), order.getIdentity());
        if (form.isSupportPayOrderStatusQuerying())
            form.queryPayStatus(order);
    }
}
