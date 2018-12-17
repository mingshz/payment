package me.jiangcai.premier.project;

import com.paymax.model.Charge;
import com.paymax.spring.event.ChargeChangeEvent;
import me.jiangcai.payment.MockPaymentEvent;
import me.jiangcai.payment.PaymentConfig;
import me.jiangcai.payment.premier.PremierPaymentConfig;
import me.jiangcai.wx.standard.StandardWeixinConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.event.EventListener;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ViewResolverRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.extras.java8time.dialect.Java8TimeDialect;
import org.thymeleaf.extras.springsecurity4.dialect.SpringSecurityDialect;
import org.thymeleaf.spring4.SpringTemplateEngine;
import org.thymeleaf.spring4.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.spring4.view.ThymeleafViewResolver;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ITemplateResolver;

import java.io.IOException;

/**
 * @author lxf
 */
@Configuration
@Import({PaymentConfig.class, PremierPaymentConfig.class, PremierProjectConfig.ThymeleafConfig.class, StandardWeixinConfig.class})
@EnableWebMvc
@ComponentScan({"me.jiangcai.premier.project.bean"})
@EnableJpaRepositories("me.jiangcai.premier.project.repository")
public class PremierProjectConfig extends WebMvcConfigurerAdapter {

    @Autowired
    private ThymeleafViewResolver thymeleafViewResolver;
    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    @Override
    public void configureViewResolvers(ViewResolverRegistry registry) {
        super.configureViewResolvers(registry);
        registry.viewResolver(thymeleafViewResolver);
    }

    @EventListener
    public void event(MockPaymentEvent event) {
        ChargeChangeEvent tradeEvent = new ChargeChangeEvent();
        tradeEvent.setData(new Charge());
        if (event.isSuccess()) {
            tradeEvent.getData().setStatus("SUCCEED");
        } else
            tradeEvent.getData().setStatus("FAILED");

        tradeEvent.getData().setId(event.getId());
        applicationEventPublisher.publishEvent(tradeEvent);
    }

    @Import(ThymeleafConfig.ThymeleafTemplateConfig.class)
    static class ThymeleafConfig {
        @Autowired
        private TemplateEngine engine;

        @Bean
        private ThymeleafViewResolver thymeleafViewResolver() {
            ThymeleafViewResolver viewResolver = new ThymeleafViewResolver();
            viewResolver.setTemplateEngine(engine);
            viewResolver.setCharacterEncoding("UTF-8");
            viewResolver.setContentType("text/html;charset=UTF-8");
            return viewResolver;
        }

        @ComponentScan("me.jiangcai.dating.web.thymeleaf")
        static class ThymeleafTemplateConfig {

            @Autowired
            private WebApplicationContext webApplicationContext;

            @Bean
            public TemplateEngine templateEngine() throws IOException {
                SpringTemplateEngine engine = new SpringTemplateEngine();
                engine.setEnableSpringELCompiler(true);
                engine.setTemplateResolver(templateResolver());
                engine.addDialect(new Java8TimeDialect());
                engine.addDialect(new SpringSecurityDialect());
                return engine;
            }

            private ITemplateResolver templateResolver() throws IOException {
                SpringResourceTemplateResolver resolver = new SpringResourceTemplateResolver();
                resolver.setApplicationContext(webApplicationContext);
                resolver.setCharacterEncoding("UTF-8");
                resolver.setPrefix("classpath:/");
                resolver.setTemplateMode(TemplateMode.HTML);
                return resolver;
            }
        }

    }

}
