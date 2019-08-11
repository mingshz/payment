package me.jiangcai.payment.hua.huabei.service;

import me.jiangcai.payment.PayableOrder;
import me.jiangcai.payment.entity.PayOrder;
import me.jiangcai.payment.exception.SystemMaintainException;
import me.jiangcai.payment.hua.huabei.HuabeiPaymentForm;
import me.jiangcai.payment.hua.huabei.entity.HuaHuabeiPayOrder;
import me.jiangcai.payment.service.PaymentGatewayService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.EntityBuilder;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * @author CJ
 */
@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
@Service
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class HuabeiPaymentFormImpl implements HuabeiPaymentForm {

    private static final Log log = LogFactory.getLog(HuabeiPaymentFormImpl.class);

    /**
     * 通讯URL
     */
    private final String rootUrl;
    /**
     * 商户编码
     */
    private final String businessID;
    /**
     * 收款门店编码
     */
    private final String shopID;
    /**
     * 收款支付宝PID
     */
    private final String aliPid;
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyMMddHHmmssSSS");
    private final Random random = new Random();
    @Autowired
    private PaymentGatewayService paymentGatewayService;
    @Autowired
    private Environment environment;

    public HuabeiPaymentFormImpl() {
        rootUrl = environment.getProperty("huabei.url", "http://hbfq.huaat.com");
        businessID = environment.getProperty("huabei.businessID", "HBCD2040");
        shopID = environment.getProperty("huabei.shopID", "HBCD20400001");
        aliPid = environment.getProperty("huabei.aliPid", "2088721181145781");
    }

    private String createOrderId() {
//        17012512562347261774
        return String.format("%s%05d", LocalDateTime.now().format(dateTimeFormatter), Math.abs(random.nextInt()))
                .substring(0, 20);
    }

    @Override
    public PayOrder newPayOrder(HttpServletRequest request, PayableOrder order
            , Map<String, Object> additionalParameters) throws SystemMaintainException {
        // 商户预创建订单时候的订单号，将作为本订单的唯一标识格式为:yyMMddHHmmssSSS+5位随机数
        Map<String, Object> parameters = new HashMap<>();

        parameters.put("businessID", businessID);
        parameters.put("shopID", shopID);
        parameters.put("aliPid", aliPid);
        parameters.put("periods", additionalParameters.getOrDefault(PERIODS, 12));
        parameters.put("customerName", order.getOrderedName());
        parameters.put("telNo", order.getOrderedMobile());
        parameters.put("price", order.getOrderDueAmount().setScale(2, BigDecimal.ROUND_HALF_UP));
        parameters.put("hbMoney", order.getOrderDueAmount().multiply(new BigDecimal("1.5"))
                .setScale(2, BigDecimal.ROUND_HALF_UP));
//        parameters.put("productName", order.getOrderProductName());
        parameters.put("brandName", order.getOrderProductBrand());
        parameters.put("modelName", order.getOrderProductModel());
        parameters.put("goodsID", order.getOrderProductCode());

        while (true) {
            String orderID = createOrderId();
            parameters.put("orderID", orderID);
            log.debug("试图以" + orderID + "创建华院花呗支付订单。");

            try {
                Map<String, Object> result = postJSON("/payAPI/createOrder", parameters);
                Object code = result.get("code");
                if (!"T".equals(result.get("status"))) {
                    if ("D03".equals(code)) {
                        log.debug("订单号重复，尝试重新创建。");
                        continue;
                    }
                    throw new SystemMaintainException(orderID + "> code:" + code);
                }
                HuaHuabeiPayOrder payOrder = new HuaHuabeiPayOrder();
                payOrder.setPlatformId(result.get("orderID").toString());
                payOrder.setAliPayCodeUrl(result.get("payCode").toString());
                payOrder.setPoundageAmount(new BigDecimal(result.get("poundageAmount").toString()));
                payOrder.setCapitalAmount(new BigDecimal(result.get("capitalAmount").toString()));
                log.debug("成功创建订单" + payOrder.getPlatformId() + ":" + payOrder);
                return payOrder;
            } catch (IOException e) {
                throw new SystemMaintainException(e);
            }
        }


    }

    private CloseableHttpClient requestClient() {
        HttpClientBuilder builder = HttpClientBuilder.create();
        builder.setDefaultRequestConfig(RequestConfig.custom()
                .setConnectTimeout(30000)
                .setConnectionRequestTimeout(30000)
                .setSocketTimeout(300000)
                .build());
//        if (environment.acceptsProfiles("test")) {
//            builder.setSSLHostnameVerifier(new NoopHostnameVerifier());
//        }

        return builder.build();
    }


    private Map<String, Object> postJSON(String uri, Map<String, Object> parameters) throws IOException {
        try (CloseableHttpClient client = requestClient()) {
            HttpPost post = new HttpPost(rootUrl + uri);
            final String value = JsonHandler.objectMapper.writeValueAsString(parameters);
            log.debug("欲提交数据:" + value);
            post.setEntity(
                    EntityBuilder.create()
                            .setContentType(ContentType.APPLICATION_FORM_URLENCODED.withCharset("UTF-8"))
                            .setParameters(new BasicNameValuePair("orderDetail"
                                    , value))
                            .build()
            );
            return client.execute(post, new JsonHandler());
        }
    }

    @Override
    public boolean isSupportPayOrderStatusQuerying() {
        return true;
    }

    @Override
    public void queryPayStatus(PayOrder order) {
//        if (order.isSuccess())
//            return;
        Map<String, Object> parameters = new HashMap<>();

        parameters.put("businessID", businessID);
        parameters.put("shopID", shopID);
        parameters.put("aliPid", aliPid);
        parameters.put("orderID", order.getPlatformId());

        try {
            final Map<String, Object> result = postJSON("/payAPI/queryOrder", parameters);
            String code = result.get("code").toString();
            if (!"T".equals(result.get("status"))) {
                throw new IllegalStateException(order.getPlatformId() + " 查询 > code:" + code);
            }

            // TRADE_CLOSED	T	未付款交易超时关闭，或支付完成后全额退款;
            // TRADE_SUCCESS	T	交易支付成功;
            // TRADE_FINISHED	T	交易结束，不可退款;
            if (!order.isSuccess()) {
                if ("TRADE_SUCCESS".equals(code)
                        || "TRADE_FINISHED".equals(code)) {
                    paymentGatewayService.paySuccess(order);
                }
            } else {
                if ("TRADE_CLOSED".equals(code))
                    paymentGatewayService.payCancel(order);
            }

        } catch (IOException e) {
            throw new IllegalStateException(e);
        }


    }

    @Override
    public void orderMaintain() {

    }
}
