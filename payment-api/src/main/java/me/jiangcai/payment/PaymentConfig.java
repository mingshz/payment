package me.jiangcai.payment;

import me.jiangcai.payment.service.PayableSystemService;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * 支付系统配置
 * 载入该配置需要提供实现{@link PayableSystemService}
 * 同时将me.jiangcai.payment.entity 加入JPA
 *
 * @author CJ
 */
@Configuration
@ComponentScan({
        "me.jiangcai.payment.service"
        , "me.jiangcai.payment.controller"
})
public class PaymentConfig {
}
