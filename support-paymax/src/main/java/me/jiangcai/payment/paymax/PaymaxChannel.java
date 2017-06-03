package me.jiangcai.payment.paymax;

/**
 * @author CJ
 */
public enum PaymaxChannel {
    /**
     * PC网关
     * 需要额外参数userId(Number),returnUrl
     */
    pcWeb,
    /**
     * 微信扫码
     * 需要额外参数openId
     */
    wechatScan
}
