package me.jiangcai.payment;

/**
 * 支付偏好
 *
 * @author CJ
 */
public enum PayPreference {
    AliPay,
    WechatPay,
    UnionPay,
    DebitCard,
    CreditCard,
    /**
     * 快捷支付
     */
    QuickUnionPay,
    /**
     * 云闪付, 银联支付APP
     */
    UnionPayApp,
}
