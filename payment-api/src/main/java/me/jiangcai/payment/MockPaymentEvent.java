package me.jiangcai.payment;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 模拟订单支付事件
 * 子支付系统在测试领域应当提供对该事件的支持：在处理该情况时 模拟真实事件的产生。
 * 应该认可它是作为一种反向驱动；是该事件反向驱动子支付系统的真实事件；所以它虽然处于测试或者模拟的领域，但应该是核心功能
 * 子支付系统在线上作业时不应该支持该事件。
 *
 * @author CJ
 */
@Data
@AllArgsConstructor
public class MockPaymentEvent {
    /**
     * 支付系统的订单号
     */
    private String id;
    private boolean success;
}
