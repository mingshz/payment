package me.jiangcai.payment.premier.bean;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.jiangcai.lib.ee.ServletUtils;
import me.jiangcai.payment.PayableOrder;
import me.jiangcai.payment.entity.PayOrder;
import me.jiangcai.payment.exception.SystemMaintainException;
import me.jiangcai.payment.premier.HttpsClientUtil;
import me.jiangcai.payment.premier.PremierPaymentForm;
import me.jiangcai.payment.premier.entity.PremierPayOrder;
import me.jiangcai.payment.premier.exception.PlaceOrderException;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PremierPaymentFormImpl implements PremierPaymentForm {
    private static final Log log = LogFactory.getLog(PremierPaymentFormImpl.class);

    private String customerId;
    private String notifyUrlPrefix;
    private String backUrlPro;
    private ObjectMapper objectMapper = new ObjectMapper();


    private final String sendUrl;
    private final String key;

    @Autowired
    public PremierPaymentFormImpl(Environment environment) {
        customerId = environment.getProperty("premier.customerId", "1535535402498");
        notifyUrlPrefix = environment.getProperty("premier.notifyUrl", "");
        backUrlPro = environment.getProperty("premier.backUrl", "");

        this.sendUrl = environment.getProperty("premier.transferUrl", "https://api.aisaepay.com/companypay/easyPay/recharge");
        this.key = environment.getProperty("premier.mKey", "7692ecf5b63949337473755b062f2434");
    }

    @Override
    public PayOrder newPayOrder(HttpServletRequest request, PayableOrder order, Map<String, Object> additionalParameters) throws SystemMaintainException {
        PremierPayOrder payOrder = new PremierPayOrder();
        String merchantOrderId = payOrder.getMerchantOrderId();
//        StringBuilder sb = new StringBuilder();
        String backUrl = backUrlPro;
        String notifyUrl = notifyUrlPrefix + "/premier/call_back";
        String mark = order.getOrderProductName();
        String remarks = order.getOrderProductModel();
        BigDecimal orderMoney = order.getOrderDueAmount();
        String payType = additionalParameters.get("type").toString();
        payOrder.setPayType(payType);

//        sb.append("backUrl").append(backUrl).append("&");
//        sb.append("customerId=").append(customerId).append("&");
//        sb.append("mark=").append(mark).append("&");
//        sb.append("notifyUrl=").append(notifyUrl).append("&");
//        sb.append("orderMoney=").append(orderMoney).append("&");
//        sb.append("orderNo=").append(merchantOrderId).append("&");
//        sb.append("payType=").append(payType).append("$");
//        sb.append("remarks=").append(remarks).append("&");
        String Md5str = "customerId=" + customerId + "&orderNo=" + merchantOrderId + "&orderMoney=" + orderMoney
                + "&payType=" + payType + "&notifyUrl=" + notifyUrl + "&backUrl=" + backUrl + key;
        String sign;
        try {
            sign = DigestUtils.md5Hex(Md5str.getBytes(StandardCharsets.UTF_8)).toUpperCase();
        } catch (Throwable ex) {
            throw new SystemMaintainException(ex);
        }
        String requestUrl = sendUrl + "?customerId=" + customerId + "&orderNo=" + merchantOrderId + "&orderMoney="
                + orderMoney + "&payType=" + payType + "&notifyUrl=" + notifyUrl + "&backUrl="
                + backUrl + "&sign=" + sign + "&mark=" + mark + "&remarks=" + remarks + "&userIp=" + ServletUtils.clientIpAddress(request);
//        bankCardNo	银行卡号	String	否	输入后限制必须使用这个卡号支付
//        idCardNo	身份证号	String	否	输入后限制必须是这个身份证的人的卡支付
//        cardName	姓名	String	否	输入后限制必须是这个姓名的人的卡支付
//        isBindCard   0.不绑定  1.绑定     不传默认为不绑定
        if (additionalParameters.containsKey("additional")) {
            @SuppressWarnings("unchecked")
            Map<String, Object> additional = (Map<String, Object>) additionalParameters.get("additional");
            String more = additional.entrySet().stream()
                    .map((stringObjectEntry -> "&" + stringObjectEntry.getKey() + "=" + stringObjectEntry.getValue()))
                    .collect(Collectors.joining());
            requestUrl = requestUrl + more;
        }

        try {
            String responseStr = HttpsClientUtil.sendRequest(requestUrl, null);
            JsonNode root = objectMapper.readTree(responseStr);
            JsonNode data = root.get("data");
            if ("1".equals(root.get("status").asText())) {
                //通信成功
                log.debug("易支付,通信成功");
                if ("2".equals(payType)) {
                    payOrder.setAliPayCodeUrl(root.get("payInfo").asText());
                } else if ("1".equals(data.get("state").asText())) {
                    // 业务成功
                    log.debug("业务成功");
                    if ("9".equals(payType)) {
                        payOrder.setAliPayCodeUrl(root.get("url").asText());
                    } else if ("13".equals(payType) || "16".equals(payType)) {
                        payOrder.setAliPayCodeUrl(data.get("url").asText());
                    } else {
                        payOrder.setAliPayCodeUrl(data.get("url").asText());
                    }
                    payOrder.setPayableOrderId(order.getPayableOrderId().toString());
                    return payOrder;
                } else {
                    // 业务失败
                    log.warn("业务失败");
                    // 记录全部 requestUrl
                    throw new PlaceOrderException(data.get("msg").asText(), requestUrl, payType);
                }
            } else {
                log.warn("易支付,通信失败");
                //
                throw new PlaceOrderException(root.get("msg").asText(), requestUrl, payType);
                //通信失败
            }
        } catch (Throwable ex) {
            throw new SystemMaintainException(ex);
        }

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


}
