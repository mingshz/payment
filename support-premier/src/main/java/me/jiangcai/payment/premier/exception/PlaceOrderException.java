package me.jiangcai.payment.premier.exception;

import lombok.Getter;

/**
 * 支付返回的错误状态是,抛出的异常
 *
 * @author lxf
 */
@Getter
public class PlaceOrderException extends RuntimeException {

    private final String urlInfo;
    private final String payType;

    public PlaceOrderException(String s, String urlInfo, String payType) {
        super(s);
        this.urlInfo = urlInfo;
        this.payType = payType;
    }

}
