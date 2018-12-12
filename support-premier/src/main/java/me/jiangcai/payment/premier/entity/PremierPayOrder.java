package me.jiangcai.payment.premier.entity;

import lombok.Getter;
import lombok.Setter;
import me.jiangcai.payment.PaymentForm;
import me.jiangcai.payment.entity.PayOrder;
import me.jiangcai.payment.premier.PayType;
import me.jiangcai.payment.premier.PremierPaymentForm;

import javax.persistence.Column;
import javax.persistence.Entity;
import java.math.BigDecimal;

/**
 * @author lxf
 */
@Entity
@Setter
@Getter
public class PremierPayOrder extends PayOrder {

    /**
     * 支付金额
     */
    @Column(scale = 2, precision = 12)
    private BigDecimal amount;

    /**
     * 回调地址
     */
    @Column(length = 100)
    private String notifyUrl;

    /**
     * 跳转url
     */
    @Column(length = 100)
    private String backUrl;

    /**
     * 商品标题
     */
    @Column(length = 20)
    private String mark;

    /**
     * 商品描述
     */
    @Column(length = 50)
    private String remarks;

    /**
     * 支付类型
     */
    private PayType payType;

    @Override
    public Class<? extends PaymentForm> getPaymentFormClass() {
        return PremierPaymentForm.class;
    }

    @Override
    public String toString() {
        return "PremierPayOrder{" +
                "amount=" + amount +
                ", notifyUrl='" + notifyUrl + '\'' +
                ", backUrl='" + backUrl + '\'' +
                ", mark='" + mark + '\'' +
                ", remarks='" + remarks + '\'' +
                ", payType=" + payType +
                '}';
    }
}
