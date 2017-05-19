package me.jiangcai.demo.project;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 模拟订单支付事件
 * 子支付系统在测试领域应当提供对该事件的支持：在处理该情况时 模拟真实事件的产生。
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
