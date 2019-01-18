package me.jiangcai.payment.premier.bean;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.apachecommons.CommonsLog;
import me.jiangcai.payment.premier.event.CallBackOrderEvent;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.math.BigDecimal;

/**
 * 用于接受回调函数的对外接口
 *
 * @author lxf
 */
@Controller
@CommonsLog
public class PremierCallBackController {


    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    private final String key;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    public PremierCallBackController(Environment environment) {
        this.key = environment.getProperty("premier.mKey", "7692ecf5b63949337473755b062f2434");
    }

    @RequestMapping(value = "/premier/call_back", method = RequestMethod.POST)
    @ResponseBody
    public String callBack(HttpServletRequest request) throws IOException {
        //解析返回串
        String state = request.getParameter("state");
        String customerId = request.getParameter("customerId");
        String orderNum = request.getParameter("orderNum");
        String orderNo = request.getParameter("orderNo");
        String orderMoney = request.getParameter("orderMoney");
        String sign = request.getParameter("sign");
//        /**
//         * state           // 1:充值失败 2:充值成功
//         customerId  //商户注册的时候，分配的商户ID
//         orderNum   //网关系统的订单号
//         orderNo  //商户系统的流水号
//         orderMoney  //商户订单实际金额单位：（元）
//         sign    //网关系统签名字符串
//         */
        String md5str = "customerId=" + customerId + "&orderNum=" + orderNum + "&orderNo=" + orderNo + "&orderMoney=" + orderMoney + "&state=" + state + "&key=" + key;
        String sign2 = DigestUtils.md5Hex(md5str.getBytes("UTF-8")).toUpperCase();
        if (!sign.equals(sign2)) {
            //签名错误
            log.info("签名错误");
            return "failure";
        }
        if ("2".equals(state)) {
            //交易成功
            //发布成功事件
            applicationEventPublisher.publishEvent(new CallBackOrderEvent(orderNo, true));
            return "success";
        } else if ("1".equals(state)) {
            //交易失败
            //发布失败事件
            applicationEventPublisher.publishEvent(new CallBackOrderEvent(orderNo, false));
            return "success";
        } else {
            //意外的状态
            return "failure";
        }
    }
}
