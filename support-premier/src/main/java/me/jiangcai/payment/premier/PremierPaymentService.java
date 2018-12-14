package me.jiangcai.payment.premier;

import com.fasterxml.jackson.core.JsonProcessingException;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map;

/**
 * 对接易支付的服务
 *
 * @author lxf
 */
public interface PremierPaymentService {

    /**
     * 支付订单
     * @param  orderInfo 暂时用map代替请求的订单数据
     */
    public void pay(Map<String, Object> orderInfo) throws IOException;
}
