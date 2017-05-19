package me.jiangcai.payment.exception;

/**
 * 系统维护异常
 *
 * @author CJ
 */
public class SystemMaintainException extends Exception {
    public SystemMaintainException() {
        super();
    }

    public SystemMaintainException(String message) {
        super(message);
    }

    public SystemMaintainException(String message, Throwable cause) {
        super(message, cause);
    }

    public SystemMaintainException(Throwable cause) {
        super(cause);
    }

    protected SystemMaintainException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
