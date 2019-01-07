package me.jiangcai.payment.premier;

import me.jiangcai.payment.PaymentConfig;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

/**
 * 易支付配置
 * JPA需增配 me.jiangcai.payment.premier.entity
 * 环境变量:
 * 商户号"premier.customerId"
 * 后台回调地址前缀  "premier.notifyUrl"
 * 前台跳转地址"premier.backUrl"  该参数目前没有意义可以不用配置
 * 支付请求地址 "premier.transferUrl"
 * key "premier.mKey"
 *
 * @author lxf
 */
@ComponentScan({"me.jiangcai.payment.premier.bean"})
@Import({PaymentConfig.class})
public class PremierPaymentConfig {
}
