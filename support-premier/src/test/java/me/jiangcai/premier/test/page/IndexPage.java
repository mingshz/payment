package me.jiangcai.premier.test.page;

import me.jiangcai.lib.test.page.AbstractPage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author CJ
 */
public class IndexPage extends AbstractPage {

    private WebElement amount;
    private WebElement name;
    @FindBy(css = "input[type=submit]")
    private WebElement submit;
//    private WebElement type;

    public IndexPage(WebDriver webDriver) {
        super(webDriver);
    }

    @Override
    public void validatePage() {
        assertThat(webDriver.getTitle())
                .isEqualTo("首页");
    }

    public PayPage makeOrder(String inputName, BigDecimal inputAmount, String formName) {
        name.clear();
        name.sendKeys(inputName);
        amount.clear();
        amount.sendKeys(inputAmount.toString());
        //
        inputSelect(webDriver.findElement(By.tagName("form")), "type", formName);

        submit.click();
        return initPage(PayPage.class);
    }
}
