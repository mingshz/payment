package me.jiangcai.payment.entity;

import lombok.Getter;
import lombok.Setter;
import me.jiangcai.payment.PaymentForm;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 支付订单
 *
 * @author CJ
 */
@Setter
@Getter
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class PayOrder {
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
     * 商户订单号
     * 为了解决项目方订单号主键会重复的情况，特意加了这么个字段。
     * 应该把这个字段作为‘外部订单号’传给平台方
     */
    @Column(length = 36)
    private String merchantOrderId = UUID.randomUUID().toString().replaceAll("-", "");

    /**
     * 最后变化时间
     */
    @Column(columnDefinition = "timestamp")
    private LocalDateTime eventTime;

    /**
     * 完成时间
     */
    @Column(columnDefinition = "timestamp")
    private LocalDateTime finishTime;

    /**
     * 开始支付时间
     */
    @Column(columnDefinition = "timestamp")
    private LocalDateTime startTime;

    /**
     * 是否已取消
     * 这个属性作用非常重要；一旦状态完成则不可再行更变或者散发事件
     */
    private boolean cancel;

    /**
     * 是否成功的标记位
     */
    private boolean success;

    /**
     * @param from            from
     * @param criteriaBuilder cb
     * @return 是否成功的谓语
     */
    public static Predicate Success(From<?, ? extends PayOrder> from, CriteriaBuilder criteriaBuilder) {
        return criteriaBuilder.isTrue(from.get("success"));
    }

    public static Predicate Cancel(From<?, ? extends PayOrder> from, CriteriaBuilder criteriaBuilder) {
        return criteriaBuilder.isTrue(from.get("cancel"));
    }

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

    /**
     * @return 测试用订单
     */
    public boolean isTestOrder() {
        return false;
    }

    /**
     * @return 支持该订单的支付方式
     */
    public abstract Class<? extends PaymentForm> getPaymentFormClass();
}
