package me.jiangcai.payment.premier;

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

    private PayType(String type, int Code) {

    }

}
