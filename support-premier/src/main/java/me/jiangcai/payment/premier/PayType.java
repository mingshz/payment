package me.jiangcai.payment.premier;

import java.util.LinkedList;
import java.util.List;

/**
 * 支付类型
 *
 * @author lxf
 */
public enum PayType {

    /**
     * alipay	支付宝企业APP支付	2
     * aliWAPpay	支付宝手机WAP支付	88
     * aliPCWAPpay	支付宝电脑网站支付	66
     * aggAlipay	支付宝转账H5	8
     * jhAlipay	支付宝原生H5	10
     */
    Alipay("支付宝企业app支付", 2),

    AliWAPpay("支付宝手机Wap支付", 88),

    AliPCWAPpay("支付宝电脑网站支付", 66),

    AggAlipay("支付宝转账H5", 8),

    jhAlipay("支付宝原生H5", 10);

    /**
     * 类型
     */
    private String type;

    /**
     * 对应code
     */
    private int code;

    private static final LinkedList<PayType> payTypes = new LinkedList<>();

    private PayType(String type, int Code) {
    }

    public static PayType byCode(int code) {
        if (payTypes.isEmpty()) {
            payTypes.add(PayType.AggAlipay);
            payTypes.add(PayType.Alipay);
            payTypes.add(PayType.AliPCWAPpay);
            payTypes.add(PayType.AliWAPpay);
            payTypes.add(PayType.jhAlipay);
        }
        for (PayType payType : payTypes) {
            if (payType.code == code) {
                return payType;
            } else {
                return null;
            }
        }
        return null;
    }

}
