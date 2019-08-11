package me.jiangcai.demo.pay;

import me.jiangcai.demo.pay.bean.DebugPublicAccount;
import me.jiangcai.payment.service.PaymentService;
import me.jiangcai.wx.PublicAccountSupplier;
import me.jiangcai.wx.classics.SinglePublicAccountSupplier;
import me.jiangcai.wx.pay.WeixinPayHookConfig;
import me.jiangcai.wx.pay.model.WeixinPayUrl;
import me.jiangcai.wx.web.WeixinWebSpringConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;

import javax.annotation.PostConstruct;

/**
 * 一个demo支付
 * JPA增配
 * me.jiangcai.demo.pay.entity
 *
 * @author CJ
 */
@Configuration
@ComponentScan("me.jiangcai.demo.pay.bean")
@Import({WeixinWebSpringConfig.class, WeixinPayHookConfig.class})
public class DemoPayConfig {

    private final Environment environment;
    private final PaymentService paymentService;

    public DemoPayConfig(Environment environment
            , @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection") PaymentService paymentService) {
        this.environment = environment;
        this.paymentService = paymentService;
    }

    @Bean
    public PublicAccountSupplier publicAccountSupplier() {
        final DebugPublicAccount publicAccount = new DebugPublicAccount(environment.getProperty("account.url", "http://localhost/"));

        final SinglePublicAccountSupplier singlePublicAccountSupplier = new SinglePublicAccountSupplier
                (publicAccount);
        publicAccount.setSupplier(singlePublicAccountSupplier);

        singlePublicAccountSupplier.getTokens(publicAccount);

        return singlePublicAccountSupplier;
    }

    @Bean
    public WeixinPayUrl weixinPayUrl() {
        WeixinPayUrl weixinPayUrl = new WeixinPayUrl();
        weixinPayUrl.setAbsUrl("http://localhost:8009" + WeixinPayUrl.relUrl);
        return weixinPayUrl;
    }

    @PostConstruct
    public void init() {
        paymentService.registerPaymentForm(DemoPaymentForm.class, "demo");
    }
}
