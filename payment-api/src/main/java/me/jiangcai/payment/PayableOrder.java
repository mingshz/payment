package me.jiangcai.payment;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 可支付订单
 *
 * @author CJ
 */
public interface PayableOrder {

    /**
     * 虽然客户系统可以拥有多种主键，但支付系统仅仅将储存它的{@link Object#toString()}值
     * 所以它的{@link Object#toString()}值长度不应该超过36
     *
     * @return 订单主键
     */
    Serializable getPayableOrderId();


    /**
     * 指定货币目前仅指人民币
     *
     * @return 订单总应付款金额，单位指定货币元
     */
    BigDecimal getOrderDueAmount();

    /**
     * @return 订单商品名称
     */
    String getOrderProductName();

    /**
     * @return 详细的商品描述
     */
    String getOrderBody();

    /**
     * @return 订单商品型号
     */
    String getOrderProductModel();

    /**
     * @return 订单商品编号
     */
    String getOrderProductCode();

    /**
     * @return 订单商品品牌
     */
    String getOrderProductBrand();

    /**
     * @return 下单者姓名
     */
    String getOrderedName();

    /**
     * @return 下单者手机
     */
    String getOrderedMobile();
}
