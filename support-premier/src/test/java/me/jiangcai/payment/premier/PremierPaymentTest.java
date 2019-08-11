package me.jiangcai.payment.premier;

import me.jiangcai.lib.test.SpringWebTest;
import me.jiangcai.payment.PayableOrder;
import me.jiangcai.payment.entity.PayOrder;
import org.apache.commons.codec.digest.DigestUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.Serializable;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author lxf
 */
@ContextConfiguration(classes = {PremierPayConfigTest.class, PremierPaymentConfig.class})
@WebAppConfiguration
public abstract class PremierPaymentTest extends SpringWebTest {

    @Autowired
    private PremierPaymentForm premierPaymentForm;
    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    @Test
    public void callBack() throws Exception {
        HashMap<String, Object> parameter = new HashMap<>();
        parameter.put("type", "13");

        PayOrder payOrder = premierPaymentForm.newPayOrder(new MockHttpServletRequest(), new PayableOrder() {
            @Override
            public Serializable getPayableOrderId() {
                return 1L;
            }

            @Override
            public BigDecimal getOrderDueAmount() {
                return new BigDecimal(0.01);
            }

            @Override
            public String getOrderProductName() {
                return "测试订单";
            }

            @Override
            public String getOrderBody() {
                return "测试订单";
            }

            @Override
            public String getOrderProductModel() {
                return "测试订单";
            }

            @Override
            public String getOrderProductCode() {
                return "测试订单";
            }

            @Override
            public String getOrderProductBrand() {
                return "测试订单";
            }

            @Override
            public String getOrderedName() {
                return "测试订单";
            }

            @Override
            public String getOrderedMobile() {
                return "18899876653";
            }
        }, parameter);
        entityManager.persist(payOrder);
        String md5str = "customerId=" + "1" + "&orderNum=" + "1" + "&orderNo=" + payOrder.getPlatformId() + "&orderMoney=0.01" + "&state=" + "2" + "&key=" + "7692ecf5b63949337473755b062f2434";
        String sign1 = DigestUtils.md5Hex(md5str.getBytes(StandardCharsets.UTF_8)).toUpperCase();
        mockMvc.perform(post("/premier/call_back")
                .param("state", "2")
                .param("customerId", "1")
                .param("orderNum", "1")
                .param("orderNo", payOrder.getPlatformId())
                .param("orderMoney", "0.01")
                .param("sign", sign1))
                .andExpect(status().isOk());
    }

}
