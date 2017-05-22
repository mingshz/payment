package me.jiangcai.demo.project.bean;

import me.jiangcai.demo.project.entity.DemoTradeOrder;
import me.jiangcai.demo.project.repository.DemoTradeOrderRepository;
import me.jiangcai.payment.PaymentForm;
import me.jiangcai.payment.exception.SystemMaintainException;
import me.jiangcai.payment.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.math.BigDecimal;

/**
 * @author CJ
 */
@Controller
public class DemoController {

    @Autowired
    private PaymentService paymentService;
    //    @Autowired
//    private DemoPaymentForm demoPaymentForm;
    @Autowired
    private ApplicationContext applicationContext;
    @Autowired
    private DemoTradeOrderRepository demoTradeOrderRepository;

    @RequestMapping(value = {"", "/"})
    public String index() {
        return "index.html";
    }

    @RequestMapping(value = "/goOrder")
    public ModelAndView goOrder(BigDecimal amount, String name, String type) throws SystemMaintainException, ClassNotFoundException {
        DemoTradeOrder order = new DemoTradeOrder();
        order.setOrderDueAmount(amount);
        order.setOrderProductName(name);
        order = demoTradeOrderRepository.saveAndFlush(order);

        return paymentService.startPay(null, order, (PaymentForm) applicationContext.getBean(Class.forName(type)), null);
    }

}
