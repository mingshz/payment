package me.jiangcai.payment.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import me.jiangcai.payment.PayableOrder;
import me.jiangcai.payment.entity.PayOrder;

/**
 * @author CJ
 */
@Data
@AllArgsConstructor
public class PaymentEvent {
    /**
     * 都保证在同一事务内
     */
    private PayableOrder payableOrder;
    /**
     * 都保证在同一事务内
     */
    private PayOrder payOrder;

    /**
     * @return 同步key
     */
    public String makeKey() {
        return (payableOrder.getClass().toString() + payableOrder.getPayableOrderId().toString()).intern();
    }
}
