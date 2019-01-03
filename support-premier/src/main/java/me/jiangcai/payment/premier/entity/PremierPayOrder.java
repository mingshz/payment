package me.jiangcai.payment.premier.entity;

import lombok.Data;
import me.jiangcai.payment.PaymentForm;
import me.jiangcai.payment.entity.PayOrder;
import me.jiangcai.payment.premier.PayType;
import me.jiangcai.payment.premier.PremierOrderStatus;
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

    /**
     * 易支付选择的支付方式
     */
    private PayType premierType;

    /**
     * 订单的状态
     */
    private PremierOrderStatus status = PremierOrderStatus.wait;

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
