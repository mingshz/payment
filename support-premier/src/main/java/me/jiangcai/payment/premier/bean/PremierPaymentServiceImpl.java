package me.jiangcai.payment.premier.bean;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.jiangcai.payment.premier.HttpsClientUtil;
import me.jiangcai.payment.premier.PremierPaymentService;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author lxf
 */
@Service
public class PremierPaymentServiceImpl implements PremierPaymentService {

    ObjectMapper objectMapper = new ObjectMapper();

    private static final Log log = LogFactory.getLog(PremierPaymentServiceImpl.class);

    private final String sendUrl;
    private final String key;
    private final String mchId;
    //回调地址前缀
    private final String URLPrefix;

    @Autowired
    public PremierPaymentServiceImpl(Environment environment) {
        this.sendUrl = environment.getProperty("premier.transferUrl", "");
        this.key = environment.getProperty("premier.mKey", "");
        this.mchId = environment.getProperty("premier.mchId", "");
        this.URLPrefix = environment.getProperty("premier.URLPrefix", "");
    }

    @Override
    public void pay(Map<String, Object> orderInfo) throws IOException {
        //首先是签名
        /**
         * 商户号	customerId	是	String(32)	由平台分配的商户号
         签名	sign	是	String(32)	签名，详见签名生成算法
         商品标题	mark	是	String(128)	商品或支付标题
         商品描述	remarks	是	String(128)	商品或支付单简要描述
         商户订单号	orderNo	是	String(64)	商户系统内部的订单号，可包含字母, 确保在商户系统唯一
         交易金额	orderMoney	是	Bigdecimal	单位为元，小数两位
         后台异步回调地址	notifyUrl	是	String(256)	支付完成后结果通知url（post方法）
         前台同步回调地址	backUrl	是	String(256)	支付成功跳转路径；
         */
        HashMap<String, Object> stringStringHashMap = new HashMap<>(orderInfo);

        StringBuilder sb = new StringBuilder();
        String customerId = stringStringHashMap.get("customerId").toString();
        sb.append("customerId=").append(customerId).append("&");
        sb.append("mark").append(stringStringHashMap.get("mark")).append("&");
        sb.append("remarks").append(stringStringHashMap.get("remarks")).append("&");
        sb.append("orderNo").append(stringStringHashMap.get("orderNo")).append("&");
        sb.append("orderMoney").append(stringStringHashMap.get("orderMoney")).append("&");
        sb.append("notifyUrl").append(stringStringHashMap.get("notifyUrl")).append("&");
        sb.append("backUrl").append(stringStringHashMap.get("backUrl")).append("&");
        String strMd5 = sb.toString() + key;
        String sign = DigestUtils.md5Hex(strMd5.getBytes("UTF-8")).toUpperCase();
        stringStringHashMap.put("sign", sign);

        String s = objectMapper.writeValueAsString(stringStringHashMap);
        String responseStr = HttpsClientUtil.sendRequest(sendUrl, s);
        JSONObject responseMap = JSON.parseObject(responseStr);
        String status = responseMap.getString("status");
        if ("1".equals(status)) {
            //通信成功
            log.info("易支付,通信成功");
            if ("1".equals(responseMap.getString("state"))) {
                // 业务成功
                log.info("支付成功");
            } else {
                // 业务失败
                log.info("支付失败");
            }
        } else {
            log.info("易支付,通信失败");
            //通信失败
        }

    }

    private CloseableHttpClient newClient() {
        return null;
    }
}
