package me.jiangcai.payment.service;

import me.jiangcai.payment.PayableOrder;
import me.jiangcai.payment.PaymentForm;
import me.jiangcai.payment.entity.PayOrder;
import me.jiangcai.payment.exception.SystemMaintainException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 供客户项目调用的支付服务
 *
 * @author CJ
 */
public interface PaymentService {

    // MVC 层次
    /**
     * 支持在额外参数中明示当前的上下文URL
     */
    String ContextURLNAME = "me.jiangcai.payment.contextUrl";

    /**
     * 引导用户支付
     * 在支付完成之后发布{@link me.jiangcai.payment.event.OrderPaySuccess}事件并引导至
     * {@link PayableSystemService#paySuccess(HttpServletRequest, PayableOrder, PayOrder)}。
     * 如果支付方式支持的话可能会发布{@link me.jiangcai.payment.event.OrderPayCancellation}事件。
     * <p>
     * <p>最终结果Model中将包括以下几个元素:</p>
     * <ul>
     * <li>order 客户项目的订单{@link PayableOrder}</li>
     * <li>payOrder 支付订单{@link PayOrder}</li>
     * <li>checkUri 校验是否成功支付的URI，它的响应正文将只有true和false(JSON)</li>
     * <li>successUri 成功之后将跳转的uri 通常是一个html响应</li>
     * </ul>
     *
     * @param request              相关的HTTP请求
     * @param order                需支付的订单，需要确保{@link PayableOrder#getPayableOrderId()}已经可用了
     * @param form                 支付方式
     * @param additionalParameters 可选的附加参数；具体规格参考实现说明,支持{@link #ContextURLNAME}
     * @return 用户支付界面的视图
     * @throws SystemMaintainException 支付系统维护中
     */
    @Transactional
    ModelAndView startPay(HttpServletRequest request, PayableOrder order, PaymentForm form
            , Map<String, Object> additionalParameters) throws SystemMaintainException;
    // 之后用户或者客户项目会产生什么期待呢？
    // 用户的期待是看到支付成功的页面 或者失败，取消
    // 客户项目则是希望收获成功支付的事件 或者失败，取消

    /**
     * @param id 订单id
     * @return 获取支付订单
     */
    @Transactional(readOnly = true)
    PayOrder payOrder(long id);

    /**
     * 模拟支付该订单
     * 前提是已经成功创建了相关的支付订单；
     *
     * @param order 需支付的订单，需要确保{@link PayableOrder#getPayableOrderId()}已经可用了
     */
    @Transactional
    void mockPay(PayableOrder order);

}
