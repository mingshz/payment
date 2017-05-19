package me.jiangcai.payment;

import me.jiangcai.payment.entity.PayOrder;
import me.jiangcai.payment.exception.SystemMaintainException;

import java.util.Map;

/**
 * 支付方式
 * 通常是由支付供应商提供的
 *
 * @author CJ
 */
public interface PaymentForm {

    /**
     * 新建支付订单，并不需要将其持久化
     * 此后该订单将托付与支付系统
     * 支付系统需要以下情况发生时回调{@link me.jiangcai.payment.service.PaymentGatewayService 网关}
     * <ul>
     * <li>成功支付 {@link me.jiangcai.payment.service.PaymentGatewayService#paySuccess(PayOrder)}</li>
     * <li>成功取消 {@link me.jiangcai.payment.service.PaymentGatewayService#payCancel(PayOrder)}</li>
     * </ul>
     *
     * @param order                需要支付的交易订单
     * @param additionalParameters 可选的附加参数；具体规格参考实现说明
     * @return 新的支付订单
     * @throws SystemMaintainException 支付系统维护中
     */
    PayOrder newPayOrder(PayableOrder order, Map<String, Object> additionalParameters) throws SystemMaintainException;
}
