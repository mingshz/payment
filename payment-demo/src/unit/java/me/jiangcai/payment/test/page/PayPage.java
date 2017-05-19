package me.jiangcai.payment.test.page;

import me.jiangcai.lib.test.page.AbstractPage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author CJ
 */
public class PayPage extends AbstractPage {

    public PayPage(WebDriver webDriver) {
        super(webDriver);
    }

    @Override
    public void validatePage() {
        assertThat(webDriver.getTitle())
                .isEqualTo("支付中……");
    }

    public void makePay() throws InterruptedException {
        Thread.sleep(1000L);
        printThisPage();
        webDriver.findElement(By.id("pay"))
                .click();
    }
}
