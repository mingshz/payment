package me.jiangcai.payment.hua.huabei.entity;

import lombok.Getter;
import lombok.Setter;
import me.jiangcai.payment.PaymentForm;
import me.jiangcai.payment.entity.PayOrder;
import me.jiangcai.payment.hua.huabei.HuabeiPaymentForm;

import javax.persistence.Column;
import javax.persistence.Entity;
import java.math.BigDecimal;

/**
 * @author CJ
 */
@Entity
@Setter
@Getter
public class HuaHuabeiPayOrder extends PayOrder {

    /**
     * 支付宝支付的URL
     */
    @Column(length = 100)
    private String aliPayCodeUrl;
    /**
     * 手续费金额
     */
    @Column(scale = 2, precision = 10)
    private BigDecimal poundageAmount;
    /**
     * 分期本金
     */
    @Column(scale = 2, precision = 10)
    private BigDecimal capitalAmount;

    @Override
    public String toString() {
        return "HuaHuabeiPayOrder{" +
                "aliPayCodeUrl='" + aliPayCodeUrl + '\'' +
                ", poundageAmount=" + poundageAmount +
                ", capitalAmount=" + capitalAmount +
                "} " + super.toString();
    }

    @Override
    public Class<? extends PaymentForm> getPaymentFormClass() {
        return HuabeiPaymentForm.class;
    }
}
