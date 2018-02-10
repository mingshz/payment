package me.jiangcai.payment.service;

import me.jiangcai.payment.entity.PayOrder;
import me.jiangcai.payment.event.PaymentEvent;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Query;

/**
 * 支付网关服务
 * 通常是被支付子系统调用
 *
 * @author CJ
 */
public interface PaymentGatewayService {

    /**
     * @param type       订单类型
     * @param platformId 支付平台订单号
     * @param <T>        订单类型
     * @return null 或 特定支付订单
     */
    @Transactional(readOnly = true)
    <T extends PayOrder> T getOrder(Class<T> type, String platformId);

    /**
     * 根据 商户订单号 查找订单。如果有提供 platform 则优先使用{@link #getOrder(Class, String)}
     *
     * @param type           订单类型
     * @param merchantOrderId 商户
     * @param <T>            订单类型
     * @return null 或 特定支付订单
     */
    @Transactional(readOnly = true)
    <T extends PayOrder> T getOrderByMerchantOrderId(Class<T> type, String merchantOrderId);

    /**
     * 内部方法！！切勿调用！！
     *
     * @param event
     */
//    @Transactional(propagation = Propagation.REQUIRES_NEW, isolation = Isolation.SERIALIZABLE)
//    @Transactional(propagation = Propagation.NESTED)
    @Transactional
    void makeEvent(PaymentEvent event);
    // 应当尽量谨慎处理事务，因为我们需要确保先保存支付原始信息，再开启事务让客户项目介入（即使之后发生问题，客户项目也可以根据历史记录手动完成其他流程）

    /**
     * 订单支付完成
     *
     * @param order 同一事务内的支付订单
     */
    @Transactional
    void paySuccess(PayOrder order);

    /**
     * 订单支付取消
     *
     * @param order 同一事务内的支付订单
     */
    @Transactional
    void payCancel(PayOrder order);

    /**
     * @param payableOrderId 客户项目的订单主键
     * @return 成功支付的支付订单
     * @see Query#getSingleResult()
     */
    @Transactional(readOnly = true)
    PayOrder getSuccessOrder(String payableOrderId);

    /**
     * @param payableOrderId 客户项目的订单主键
     * @return 刚刚准备的支付订单
     */
    @Transactional(readOnly = true)
    PayOrder getLatestOrder(String payableOrderId);

    /**
     * 检查支付状态；如果供应商不支持可以跳过
     *
     * @param order 事务内支付订单
     */
    @Transactional
    void queryPayStatus(PayOrder order);
}
