package me.jiangcai.payment.controller;

import me.jiangcai.payment.PayableOrder;
import me.jiangcai.payment.entity.PayOrder;
import me.jiangcai.payment.service.PayableSystemService;
import me.jiangcai.payment.service.PaymentGatewayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author CJ
 */
@Controller
@RequestMapping("/_payment")
public class PaymentSupportController {

    @Autowired
    private PayableSystemService payableSystemService;
    @Autowired
    private PaymentGatewayService paymentGatewayService;

    @RequestMapping(method = RequestMethod.GET, value = "/completed/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Boolean> completed(@PathVariable("id") String id) {
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(payableSystemService.isPaySuccess(id));
    }

    @RequestMapping(method = RequestMethod.GET, value = "/success/{id}")
    public ModelAndView success(@PathVariable("id") String id) {
        // 获取那个成功支付的订单
        PayOrder order = paymentGatewayService.getSuccessOrder(id);
        final PayableOrder payableOrder = payableSystemService.getOrder(id);
        ModelAndView modelAndView = payableSystemService.paySuccess(payableOrder, order);
        modelAndView.addObject("payOrder", order);
        modelAndView.addObject("PayableOrder", payableOrder);
        return modelAndView;
    }

}
