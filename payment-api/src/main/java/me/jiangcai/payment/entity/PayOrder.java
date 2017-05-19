package me.jiangcai.payment.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import java.time.LocalDateTime;

/**
 * 支付订单
 *
 * @author CJ
 */
@Setter
@Getter
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public class PayOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    /**
     * 平台方提供的主键
     */
    @Column(length = 36)
    private String platformId;

    /**
     * 项目方订单主键
     */
    @Column(length = 36)
    private String payableOrderId;

    /**
     * 最后变化时间
     */
    @Column(columnDefinition = "timestamp")
    private LocalDateTime eventTime;

    /**
     * 完成时间
     * 这个属性作用非常重要；一旦状态完成则不可再行更变或者散发事件
     */
    @Column(columnDefinition = "timestamp")
    private LocalDateTime finishTime;

    /**
     * 完成时间
     */
    @Column(columnDefinition = "timestamp")
    private LocalDateTime startTime;

    /**
     * 是否成功的标记位
     */
    private boolean success;

    @Override
    public String toString() {
        return "PayOrder{" +
                "id=" + id +
                ", platformId='" + platformId + '\'' +
                ", payableOrderId='" + payableOrderId + '\'' +
                ", finishTime=" + finishTime +
                ", startTime=" + startTime +
                ", success=" + success +
                '}';
    }
}
