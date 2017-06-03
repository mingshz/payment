package me.jiangcai.payment.paymax.service;

import com.paymax.exception.PaymaxException;
import com.paymax.model.Charge;
import com.paymax.spring.ChargeRequest;
import com.paymax.spring.PaymaxService;
import com.paymax.spring.event.ChargeChangeEvent;
import me.jiangcai.lib.ee.ServletUtils;
import me.jiangcai.payment.PayableOrder;
import me.jiangcai.payment.entity.PayOrder;
import me.jiangcai.payment.exception.SystemMaintainException;
import me.jiangcai.payment.paymax.PaymaxChannel;
import me.jiangcai.payment.paymax.PaymaxPaymentForm;
import me.jiangcai.payment.paymax.entity.PaymaxPayOrder;
import me.jiangcai.payment.service.PaymentGatewayService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * @author CJ
 */
@Service
public class PaymaxPaymentFormImpl implements PaymaxPaymentForm {

    @Autowired
    private PaymaxService paymaxService;

    @Override
    public PayOrder newPayOrder(HttpServletRequest request, PayableOrder order
            , Map<String, Object> additionalParameters) throws SystemMaintainException {
        PaymaxPayOrder payOrder = new PaymaxPayOrder();

        ChargeRequest chargeRequest = new ChargeRequest();
        chargeRequest.setOrderNumber(UUID.randomUUID().toString().replaceAll("-", ""));
        chargeRequest.setSubject(order.getOrderProductName());
        chargeRequest.setBody(order.getOrderBody());
        chargeRequest.setClientIpAddress(ServletUtils.clientIpAddress(request));
        chargeRequest.setAmount(order.getOrderDueAmount());

        // 默认就认为是扫码支付
        Object channelObj = additionalParameters.get("channel");
        PaymaxChannel channel;
        if (channelObj == null)
            channel = null;
        else if (channelObj instanceof PaymaxChannel)
            channel = (PaymaxChannel) channelObj;
        else
            channel = PaymaxChannel.valueOf(channelObj.toString());
        Charge charge;
        try {
            if (channel == null || channel == PaymaxChannel.wechatScan) {
                charge = paymaxService.createWechatScanCharge(chargeRequest, (String) additionalParameters.get("openId"));
                // credential.wechat_csb.qr_code
                Map csb = (Map) charge.getCredential().get("wechat_csb");
                payOrder.setScanUrl((String) csb.get("qr_code"));
            } else
                throw new IllegalArgumentException("未知的支付意图");
        } catch (PaymaxException | IOException e) {
            throw new SystemMaintainException(e);
        }

        payOrder.setPlatformId(charge.getId());
        payOrder.setOrderNumber(chargeRequest.getOrderNumber());

        return payOrder;
    }

    private static final Log log = LogFactory.getLog(PaymaxPaymentFormImpl.class);
    @Autowired
    private PaymentGatewayService paymentGatewayService;

    @Override
    public void chargeChange(ChargeChangeEvent event) {
        log.debug("trade event:" + event);
        PaymaxPayOrder order = paymentGatewayService.getOrder(PaymaxPayOrder.class, event.getData().getId());
        if (order == null) {
            log.warn("received trade event without system:" + event);
            return;
        }
        order.setEventTime(LocalDateTime.now());
        order.setOrderStatus(event.getData().getStatus());

        if (order.getFinishTime() == null) {
            if ("SUCCEED".equals(order.getOrderStatus())) {
                paymentGatewayService.paySuccess(order);
            } else if ("FAILED".equals(order.getOrderStatus())) {
                paymentGatewayService.payCancel(order);
            }
        }
    }
}
