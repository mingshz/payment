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
import me.jiangcai.premier.project.even.MockNotifyEven;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.HashMap;
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
        String type = additionalParameters.get("type").toString();

        sb.append("backUrl").append(backUrl).append("&");
        sb.append("customerId=").append(customerId).append("&");
        sb.append("mark=").append(order.getOrderProductName()).append("&");
        sb.append("notifyUrl=").append(notifyUrl).append("&");
        sb.append("orderNo=").append(order.getPayableOrderId()).append("&");
        sb.append("orderMoney=").append(order.getOrderDueAmount()).append("&");
        sb.append("remarks=").append(order.getOrderBody()).append("&");
        sb.append("payType=").append(type).append("$");
        String strMd5 = sb.toString() + key;
        String sign;
        try {
            sign = DigestUtils.md5Hex(strMd5.getBytes("UTF-8")).toUpperCase();
        } catch (Throwable ex) {
            throw new SystemMaintainException(ex);
        }
        HashMap<String, Object> stringStringHashMap = new HashMap<>();
        stringStringHashMap.put("customerId", customerId);
        stringStringHashMap.put("mark", order.getOrderProductName());
        stringStringHashMap.put("remarks", order.getOrderBody());
        stringStringHashMap.put("orderNo", order.getPayableOrderId());
        stringStringHashMap.put("orderMoney", order.getOrderDueAmount());
        stringStringHashMap.put("notifyUrl", notifyUrl);
        stringStringHashMap.put("backUrl", backUrl);
        stringStringHashMap.put("payType", type);
        stringStringHashMap.put("sign", sign);


        JSONObject responseMap;
        try {
            String s = objectMapper.writeValueAsString(stringStringHashMap);
            String responseStr = HttpsClientUtil.sendRequest(sendUrl, s);
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
                applicationEventPublisher.publishEvent(new MockNotifyEven(order.getPlatformId()));
            } else if ("FAILED".equals(event.getData().getStatus())) {
                paymentGatewayService.payCancel(order);
            }
        }
    }
}
