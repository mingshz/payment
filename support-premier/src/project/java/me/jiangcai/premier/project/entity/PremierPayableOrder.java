package me.jiangcai.premier.project.entity;

import lombok.Data;
import me.jiangcai.payment.PayableOrder;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;
import java.math.BigDecimal;

@Entity
@Data
public class PremierPayableOrder implements PayableOrder {
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
        return orderProductName + "的详情";
    }

    @Override
    public String getOrderProductModel() {
        return orderProductName;
    }

    @Override
    public String getOrderProductCode() {
        return orderProductName;
    }

    @Override
    public String getOrderProductBrand() {
        return orderProductName;
    }

    @Override
    public String getOrderedName() {
        return "匿名";
    }

    @Override
    public String getOrderedMobile() {
        return "18812341234";
    }
}
