package me.jiangcai.payment.premier.exception;

/**
 * 支付返回的错误状态是,抛出的异常
 *
 * @author lxf
 */
public class PlaceOrderException extends RuntimeException {

    public PlaceOrderException() {

    }

    public PlaceOrderException(String s) {
        super(s);
    }

    public PlaceOrderException(Exception e) {
        super(e);
    }
}
