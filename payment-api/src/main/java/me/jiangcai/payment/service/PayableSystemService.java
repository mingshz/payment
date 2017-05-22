package me.jiangcai.payment.service;

import me.jiangcai.payment.PayableOrder;
import me.jiangcai.payment.PaymentForm;
import me.jiangcai.payment.entity.PayOrder;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 可支付系统服务
 * 这个通常是由客户项目提供的
 *
 * @author CJ
 */
public interface PayableSystemService {

    /**
     * 默认将存在属性
     * <ul>
     * <li>payOrder {@link PayOrder}实例</li>
     * <li>PayableOrder {@link PayableOrder}</li>
     * </ul>
     *
     * @return 订单支付成功的视图
     */
    ModelAndView paySuccess(HttpServletRequest request, PayableOrder payableOrder, PayOrder payOrder);

    /**
     * 可选实现
     *
     * @param request              相关请求
     * @param order                交易订单
     * @param payOrder             支付订单
     * @param additionalParameters 传入startPay的额外参数；以及{@link PaymentService#startPay(HttpServletRequest, PayableOrder, PaymentForm, Map)}提及响应的几个参数
     * @return 正在支付的视图
     * @see PaymentService#startPay(javax.servlet.http.HttpServletRequest, PayableOrder, PaymentForm, Map)
     */
    ModelAndView pay(HttpServletRequest request, PayableOrder order, PayOrder payOrder, Map<String, Object> additionalParameters);

    /**
     * 客户项目在收到{@link me.jiangcai.payment.event.OrderPaySuccess}事件之后需要确保这个返回值将变成true
     *
     * @param id {@link PayableOrder#getPayableOrderId()}
     * @return 订单是否已经完成支付
     */
    boolean isPaySuccess(String id);

    /**
     * @param id {@link PayableOrder#getPayableOrderId()}
     * @return 订单最新状态
     */
    PayableOrder getOrder(String id);
}
