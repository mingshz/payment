package me.jiangcai.demo.project.entity;

import lombok.Data;
import me.jiangcai.payment.PayableOrder;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author CJ
 */
@Data
@Entity
public class DemoTradeOrder implements PayableOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private boolean done;

    private BigDecimal orderDueAmount;

    private String orderProductName;

    @Override
    public Serializable getPayableOrderId() {
        return id;
    }

    @Override
    public String getOrderBody() {
        return orderProductName+"的详情";
    }
}
