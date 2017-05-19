package me.jiangcai.payment.chanpay.entity;

import lombok.Getter;
import lombok.Setter;
import me.jiangcai.chanpay.model.TradeStatus;
import me.jiangcai.payment.entity.PayOrder;

import javax.persistence.Column;
import javax.persistence.Entity;

/**
 * @author CJ
 */
@Entity
@Setter
@Getter
public class ChanpayPayOrder extends PayOrder {
    /**
     * 支付平台提供的支付链接
     */
    @Column(length = 120)
    private String url;
    /**
     * 是微信扫码支付
     */
    private boolean wechat;
    private TradeStatus tradeStatus;

    @Override
    public String toString() {
        return "ChanpayPayOrder{" +
                "url='" + url + '\'' +
                ", wechat=" + wechat +
                ", tradeStatus=" + tradeStatus +
                "} " + super.toString();
    }
}
