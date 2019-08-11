package me.jiangcai.payment.premier.bean;

import lombok.extern.apachecommons.CommonsLog;
import me.jiangcai.payment.premier.PremierPaymentForm;
import me.jiangcai.payment.premier.entity.PremierPayOrder;
import me.jiangcai.payment.service.PaymentGatewayService;
import me.jiangcai.payment.service.PaymentService;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;

/**
 * 用于接受回调函数的对外接口
 *
 * @author lxf
 */
@Controller
@CommonsLog
public class PremierCallBackController {

    private final PaymentGatewayService paymentGatewayService;
    private final PaymentService paymentService;

    public PremierCallBackController(PaymentGatewayService paymentGatewayService, PaymentService paymentService) {
        this.paymentGatewayService = paymentGatewayService;
        this.paymentService = paymentService;
    }

    @RequestMapping(value = "/premier/call_back", method = RequestMethod.POST)
    @ResponseBody
    public String callBack(HttpServletRequest request) {
        //解析返回串
        String state = request.getParameter("state");
        String customerId = request.getParameter("customerId");
        PremierPaymentForm form = paymentService.requestPaymentForm(PremierPaymentForm.class, customerId);
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
        String md5str = "customerId=" + customerId + "&orderNum=" + orderNum + "&orderNo=" + orderNo
                + "&orderMoney=" + orderMoney + "&state=" + state + "&key=" + form.getKey();
        String sign2 = DigestUtils.md5Hex(md5str.getBytes(StandardCharsets.UTF_8)).toUpperCase();
        if (!sign.equals(sign2)) {
            //签名错误
            log.error("签名错误");
            return "failure";
        }
        PremierPayOrder order = paymentGatewayService.getOrderByMerchantOrderId(PremierPayOrder.class, orderNo);
        order.setPlatformId(orderNum);
        if ("2".equals(state)) {
            //交易成功
            //发布成功事件
            paymentGatewayService.paySuccess(order);
            return "success";
        } else if ("1".equals(state)) {
            //交易失败
            //发布失败事件
            paymentGatewayService.payCancel(order);
            return "success";
        } else {
            //意外的状态
            return "failure";
        }
    }
}
