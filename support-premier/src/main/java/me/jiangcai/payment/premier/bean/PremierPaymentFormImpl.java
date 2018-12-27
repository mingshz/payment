package me.jiangcai.payment.premier.bean;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.paymax.spring.event.ChargeChangeEvent;
import me.jiangcai.payment.PayableOrder;
import me.jiangcai.payment.entity.PayOrder;
import me.jiangcai.payment.exception.SystemMaintainException;
import me.jiangcai.payment.premier.HttpsClientUtil;
import me.jiangcai.payment.premier.PremierPaymentForm;
import me.jiangcai.payment.premier.entity.PremierPayOrder;
import me.jiangcai.payment.premier.exception.PlaceOrderException;
import me.jiangcai.payment.service.PaymentGatewayService;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

@Service
public class PremierPaymentFormImpl implements PremierPaymentForm {
    private static final Log log = LogFactory.getLog(PremierPaymentFormImpl.class);

    private String customerId;
    private String notifyUrl;
    private String backUrl;
    @Autowired
    private PaymentGatewayService paymentGatewayService;
    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;
    ObjectMapper objectMapper = new ObjectMapper();


    private final String sendUrl;
    private final String key;

    @Autowired
    public PremierPaymentFormImpl(Environment environment) {
        customerId = environment.getProperty("premier.customerId", "");
        notifyUrl = environment.getProperty("premier.notifyUrl", "");
        backUrl = environment.getProperty("premier.backUrl", "");

        this.sendUrl = environment.getProperty("premier.transferUrl", "");
        this.key = environment.getProperty("premier.mKey", "");
    }

    @Override
    public PayOrder newPayOrder(HttpServletRequest request, PayableOrder order, Map<String, Object> additionalParameters) throws SystemMaintainException {
        StringBuilder sb = new StringBuilder();
        String customerId = "1535535402498";
        String backUrl = "www.baidu.com";
        String notifyUrl = "www.baidu.com";
        String mark = "测试订单";
        String remarks = "测试订单详情";
        BigDecimal orderMoney = new BigDecimal("0.01");
        String orderNo = "1";
        String type = additionalParameters.get("type").toString();
        String key = "7692ecf5b63949337473755b062f2434";

        sb.append("backUrl").append(backUrl).append("&");
        sb.append("customerId=").append(customerId).append("&");
        sb.append("mark=").append(mark).append("&");
        sb.append("notifyUrl=").append(notifyUrl).append("&");
        sb.append("orderMoney=").append(orderMoney).append("&");
        sb.append("orderNo=").append(orderNo).append("&");
        sb.append("payType=").append(type).append("$");
        sb.append("remarks=").append(remarks).append("&");
        String strMd5 = sb.toString() + key;
        String sign;
        try {
            sign = DigestUtils.md5Hex(strMd5.getBytes("UTF-8")).toUpperCase();
        } catch (Throwable ex) {
            throw new SystemMaintainException(ex);
        }

        String requestUrl = "https://api.aisaepay.com/companypay/easyPay/recharge" + "?customerId=" + customerId + "&orderNo=" + orderNo + "&orderMoney=" + orderMoney + "&payType=" + type + "&notifyUrl=" + notifyUrl + "&backUrl=" + backUrl + "&sign=" + sign + "&mark=" + mark + "&remarks=" + remarks;
        JSONObject responseMap;
        try {
            String responseStr = HttpsClientUtil.sendRequest(requestUrl, null);
            responseMap = JSON.parseObject(responseStr);
            String status = responseMap.getString("status");
            if ("1".equals(status)) {
                //通信成功
                log.info("易支付,通信成功");
                if ("1".equals(responseMap.getJSONObject("data").getString("state"))) {
                    // 业务成功
                    log.info("业务成功");
                } else {
                    // 业务失败
                    log.info("业务失败");
                    throw new PlaceOrderException("业务失败" + responseMap.getJSONObject("data").getString("msg"));
                }
            } else {
                log.info("易支付,通信失败");
                throw new PlaceOrderException("支付失败" + responseMap.getString("msg"));
                //通信失败
            }
        } catch (Throwable ex) {
            throw new SystemMaintainException(ex);
        }
        PremierPayOrder payOrder = new PremierPayOrder();
        payOrder.setAliPayCodeUrl(responseMap.getString("url"));
        payOrder.setPayableOrderId(order.getPayableOrderId().toString());
        payOrder.setPlatformId(order.getPayableOrderId().toString());
        return payOrder;
    }

    @Override
    public void orderMaintain() {

    }

    @Override
    public boolean isSupportPayOrderStatusQuerying() {
        return false;
    }

    @Override
    public void queryPayStatus(PayOrder order) {
        System.out.println("不支持订单状态查询");
    }

    @Override
    public void chargeChange(ChargeChangeEvent event) {
        PremierPayOrder order = paymentGatewayService.getOrder(PremierPayOrder.class, event.getData().getId());
        if (order == null) {
            return;
        }
        order.setEventTime(LocalDateTime.now());
        if (!order.isCancel()) {
            if ("SUCCEED".equals(event.getData().getStatus())) {
                paymentGatewayService.paySuccess(order);
            } else if ("FAILED".equals(event.getData().getStatus())) {
                paymentGatewayService.payCancel(order);
            }
        }
    }
}
