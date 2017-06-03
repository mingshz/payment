package me.jiangcai.payment.paymax.entity;

import lombok.Getter;
import lombok.Setter;
import me.jiangcai.payment.entity.PayOrder;

import javax.persistence.Column;
import javax.persistence.Entity;

/**
 * @author CJ
 */
@Entity
@Setter
@Getter
public class PaymaxPayOrder extends PayOrder {

    /**
     * 扫码支付的URL
     */
    @Column(length = 100)
    private String scanUrl;
    /**
     * 我们自己产生的编码UUID
     * @see com.paymax.model.Charge#orderNo
     */
    @Column(length = 32)
    private String orderNumber;

    /**
     * @see com.paymax.model.Charge#status
     */
    @Column(length = 15)
    private String orderStatus;


}
