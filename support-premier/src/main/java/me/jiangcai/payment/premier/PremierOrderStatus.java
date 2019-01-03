package me.jiangcai.payment.premier;

import lombok.Getter;

/**
 * 订单状态
 */
@Getter
public enum PremierOrderStatus {
    wait("等待确认中", 0),
    success("支付成功", 1),
    failer("支付失败", 2);

    private String name;
    private int code;

    PremierOrderStatus(String name, int code) {

    }
}
