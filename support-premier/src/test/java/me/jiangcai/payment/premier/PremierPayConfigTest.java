package me.jiangcai.payment.premier;

import org.springframework.context.annotation.*;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author lxf
 */

@Configuration
@EnableTransactionManagement(mode = AdviceMode.PROXY)
@EnableAspectJAutoProxy
@ImportResource("classpath:/datasource_local.xml")
public class PremierPayConfigTest {
}