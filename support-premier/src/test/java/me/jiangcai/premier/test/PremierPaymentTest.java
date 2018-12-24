package me.jiangcai.premier.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Predicate;
import me.jiangcai.lib.test.SpringWebTest;
import me.jiangcai.payment.PaymentForm;
import me.jiangcai.payment.service.PaymentService;
import me.jiangcai.premier.project.PremierDatasourceConfig;
import me.jiangcai.premier.project.PremierProjectConfig;
import me.jiangcai.premier.project.entity.PremierPayableOrder;
import me.jiangcai.premier.project.repository.PremierPayableOrderRepository;
import me.jiangcai.premier.test.page.IndexPage;
import me.jiangcai.premier.test.page.PayPage;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

/**
 * @author lxf
 */
@ContextConfiguration(classes = {PremierDatasourceConfig.class, PremierProjectConfig.class, PremierPayConfigTest.class})
@WebAppConfiguration
public abstract class PremierPaymentTest extends SpringWebTest {

    private ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    private PaymentService paymentService;
    @Autowired
    private PremierPayableOrderRepository premierPayableOrderRepository;
    private int waitSeconds;

    /**
     * 测试订单流程
     *
     * @param formName 支付方式名称
     */
    protected void testOrderFor(String formName) throws Exception {
        testOrderFor(formName, true, 10);
    }

    /**
     * 测试订单流程
     *
     * @param formName    支付方式名称
     * @param mockPay     是否模拟支付
     * @param waitSeconds 等待支付完成最长时间
     */
    protected void testOrderFor(String formName, boolean mockPay, int waitSeconds) throws Exception {
        PayPage payPage = makeOrderFor(formName, mockPay);
        this.waitSeconds = waitSeconds;

        // 点击支付 然后等待 即可进入支付成功页面
        if (mockPay)
            payPage.makePay();

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("state", "2");
        requestBody.put("customerId", "1");
        requestBody.put("orderNum", "1");
        //不够健全. 不能从页面获取id
        requestBody.put("orderNo", "1");
        requestBody.put("orderMoney", new BigDecimal("1.00"));
        String md5str = "customerId=" + "1" + "&orderNum=" + "1" + "&orderNo=" + "1" + "&orderMoney=" + new BigDecimal("1.00") + "&state=" + "2" + "&key=" + "";
        String sign1 = DigestUtils.md5Hex(md5str.getBytes("UTF-8")).toUpperCase();
        requestBody.put("sign", sign1);
        System.out.println(objectMapper.writeValueAsString(requestBody));
        mockMvc.perform(post("/premier/call_back")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody)));

        assertPaySuccess();

        payPage = makeOrderFor(formName, mockPay);

        // 模拟支付
        PremierPayableOrder order = premierPayableOrderRepository.getOne(payPage.orderId());
        if (mockPay)
            paymentService.mockPay(order);

        assertPaySuccess();
    }


    protected void testOrderFor(Class<? extends PaymentForm> form) {

    }

    protected void assertPaySuccess() {
        new WebDriverWait(driver, waitSeconds)
                .until((Predicate<WebDriver>) input
                        -> input != null && input.getTitle().equals("订单支付成功"));
    }

    protected PayPage makeOrderFor(String formName) {
        return makeOrderFor(formName, true);
    }

    protected PayPage makeOrderFor(String formName, boolean mockPay) {
        waitSeconds = 15;
        driver.get("http://localhost/");
        IndexPage indexPage = initPage(IndexPage.class);

        double value = 100d + (double) random.nextInt(200) + random.nextDouble();
        // 便宜点……
        if (!mockPay)
            value = 1d + random.nextDouble();
        BigDecimal amount = BigDecimal.valueOf(value);
        String name = "模拟商品" + RandomStringUtils.randomAlphabetic(6);

        return indexPage.makeOrder(name, amount, formName);
    }

}
