package me.jiangcai.payment.test;

import com.google.common.base.Predicate;
import me.jiangcai.demo.project.DatasourceConfig;
import me.jiangcai.demo.project.DemoProjectConfig;
import me.jiangcai.lib.test.SpringWebTest;
import me.jiangcai.payment.test.page.IndexPage;
import me.jiangcai.payment.test.page.PayPage;
import org.apache.commons.lang.RandomStringUtils;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;

import javax.annotation.Nullable;
import java.math.BigDecimal;

/**
 * @author CJ
 */
@ContextConfiguration(classes = {DatasourceConfig.class, DemoProjectConfig.class})
@WebAppConfiguration
public abstract class PaymentTest extends SpringWebTest {

    /**
     * 测试订单流程
     *
     * @param formName 支付方式名称
     */
    protected void testOrderFor(String formName) throws Exception {
        PayPage payPage = makeOrderFor(formName);

        // 点击支付 然后等待 即可进入支付成功页面
        payPage.makePay();

        assertPaySuccess();
    }

    protected void assertPaySuccess() {
        new WebDriverWait(driver, 10)
                .until(new Predicate<WebDriver>() {
                    @Override
                    public boolean apply(@Nullable WebDriver input) {
                        return input != null && input.getTitle().equals("订单支付成功");
                    }
                });
    }

    protected PayPage makeOrderFor(String formName) {
        driver.get("http://localhost/");
        IndexPage indexPage = initPage(IndexPage.class);

        double value = 100d + (double) random.nextInt(200) + random.nextDouble();
        BigDecimal amount = BigDecimal.valueOf(value);
        String name = "模拟商品" + RandomStringUtils.randomAlphabetic(6);

        return indexPage.makeOrder(name, amount, formName);
    }

}
