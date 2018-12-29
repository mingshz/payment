package me.jiangcai.premier.test.page;

import me.jiangcai.lib.test.page.AbstractPage;
import org.openqa.selenium.WebDriver;

import static org.assertj.core.api.Assertions.assertThat;

public class PayCancelPage extends AbstractPage {
    public PayCancelPage(WebDriver webDriver) {
        super(webDriver);
    }

    @Override
    public void validatePage() {
        assertThat(webDriver.getTitle())
                .isEqualTo("订单取消");
    }
}
