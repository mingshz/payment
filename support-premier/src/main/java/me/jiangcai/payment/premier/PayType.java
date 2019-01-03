package me.jiangcai.payment.premier;

import lombok.Getter;

import java.util.LinkedList;
import java.util.List;

/**
 * 支付类型
 *
 * @author lxf
 */
@Getter
public enum PayType {

    /**
     * alipay	支付宝企业APP支付	2
     * aliWAPpay	支付宝手机WAP支付	88
     * aliPCWAPpay	支付宝电脑网站支付	66
     * aggAlipay	支付宝转账H5	8
     * jhAlipay	支付宝原生H5	10
     */
    Alipay("支付宝支付", 0),

    WechatPay("微信支付", 1),

//    AliPCWAPpay("支付宝电脑网站支付", 2),

//    AggAlipay("支付宝转账H5", 3),
//
//    jhAlipay("支付宝原生H5", 4),

    URLpay("返回URL自行支付", 3);

    /**
     * 类型
     */
    private String type;

    /**
     * 对应code
     */
    private int code;

    private PayType(String type, int code) {
        this.type = type;
        this.code = code;
    }

}
