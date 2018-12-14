package me.jiangcai.premier.project;

import org.springframework.context.annotation.AdviceMode;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.ImportResource;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author CJ
 */
@Configuration
@EnableTransactionManagement(mode = AdviceMode.PROXY)
@EnableAspectJAutoProxy
@ImportResource("classpath:/datasource_localp.xml")
public class PremierDatasourceConfig {
}
