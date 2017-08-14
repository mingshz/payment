package me.jiangcai.demo.pay.entity;

import me.jiangcai.payment.PaymentForm;
import me.jiangcai.payment.entity.PayOrder;

import javax.persistence.Entity;

/**
 * @author CJ
 */
@Entity
public class DemoPayOrder extends PayOrder {
    @Override
    public boolean isTestOrder() {
        return true;
    }

    @Override
    public Class<? extends PaymentForm> getPaymentFormClass() {
        return PaymentForm.class;
    }
}
