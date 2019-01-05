package me.jiangcai.payment.premier.entity;

import lombok.Data;
import me.jiangcai.payment.PaymentForm;
import me.jiangcai.payment.entity.PayOrder;
import me.jiangcai.payment.premier.PremierPaymentForm;

import javax.persistence.Column;
import javax.persistence.Entity;

/**
 * @author lxf
 */
@Entity
@Data
public class PremierPayOrder extends PayOrder {

    /**
     * 支付宝支付链接
     */
    @Column(length = 100)
    private String aliPayCodeUrl;

    @Override
    public Class<? extends PaymentForm> getPaymentFormClass() {
        return PremierPaymentForm.class;
    }

    @Override
    public String toString() {
        return "PremierPayOrder{" +
                "aliPayCodeUrl='" + aliPayCodeUrl +
                '}';
    }
}
