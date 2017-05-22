package me.jiangcai.payment.chanpay.service.impl;

import me.jiangcai.chanpay.data.trade.CreateInstantTrade;
import me.jiangcai.chanpay.event.TradeEvent;
import me.jiangcai.chanpay.model.TradeStatus;
import me.jiangcai.chanpay.service.TransactionService;
import me.jiangcai.chanpay.service.impl.InstantTradeHandler;
import me.jiangcai.payment.PayableOrder;
import me.jiangcai.payment.chanpay.entity.ChanpayPayOrder;
import me.jiangcai.payment.chanpay.service.ChanpayPaymentForm;
import me.jiangcai.payment.entity.PayOrder;
import me.jiangcai.payment.exception.SystemMaintainException;
import me.jiangcai.payment.service.PaymentGatewayService;
import me.jiangcai.payment.service.PaymentService;
import me.jiangcai.payment.util.RequestUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.security.SignatureException;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * @author CJ
 */
@Service
public class ChanpayPaymentFormImpl implements ChanpayPaymentForm {

    private static final Log log = LogFactory.getLog(ChanpayPaymentForm.class);

    @Autowired
    private TransactionService transactionService;
    @Autowired
    private PaymentGatewayService paymentGatewayService;

    @Override
    public void tradeUpdate(TradeEvent event) throws IOException, SignatureException {
        log.debug("trade event:" + event);
        ChanpayPayOrder order = paymentGatewayService.getOrder(ChanpayPayOrder.class, event.getSerialNumber());
        if (order == null) {
            log.warn("received trade event without system:" + event);
            return;
        }
        order.setEventTime(LocalDateTime.now());
        order.setTradeStatus(event.getTradeStatus());

        if (order.getFinishTime() == null) {
            if (event.getTradeStatus() == TradeStatus.TRADE_SUCCESS)
                paymentGatewayService.paySuccess(order);
            else if (event.getTradeStatus() == TradeStatus.TRADE_CLOSED || event.getTradeStatus() == TradeStatus.failed)
                paymentGatewayService.payCancel(order);
        }
    }

    @Override
    public PayOrder newPayOrder(HttpServletRequest httpRequest, PayableOrder order, Map<String, Object> additionalParameters) throws SystemMaintainException {
        CreateInstantTrade request = new CreateInstantTrade();
        request.setAmount(order.getOrderDueAmount());
//        request.setPayerName(card.getOwner());
        request.setProductName(order.getOrderProductName());


        // /_payment/success/
        // 默认微信支付
        if (additionalParameters != null && additionalParameters.get("desktop") != null && Boolean.valueOf(additionalParameters.get("desktop").toString())) {
            // 计算url
            StringBuilder sb;
            if (additionalParameters.containsKey(PaymentService.ContextURLNAME)) {
                sb = new StringBuilder();
                sb.append(additionalParameters.get(PaymentService.ContextURLNAME));
            } else {
                sb = RequestUtil.buildContextUrl(httpRequest);
            }
            sb.append("/_payment/success/")
                    .append(order.getPayableOrderId());
            request.setReturnUrl(sb.toString());
            request.setReturnPayUrl(true);

        } else
            request.scanPay();

        try {
            String url = transactionService.execute(request, new InstantTradeHandler());
            ChanpayPayOrder chanpayPayOrder = new ChanpayPayOrder();
//            chanpayOrder.setCashOrder(order);
//        chanpayOrder.setStatus();
            chanpayPayOrder.setPlatformId(request.getSerialNumber());
            chanpayPayOrder.setUrl(url);
            return chanpayPayOrder;
        } catch (IOException | SignatureException | NullPointerException e) {
            throw new SystemMaintainException(e);
        }

    }
}
