package me.jiangcai.premier.project.bean;

import me.jiangcai.payment.PaymentForm;
import me.jiangcai.payment.exception.SystemMaintainException;
import me.jiangcai.payment.service.PaymentService;
import me.jiangcai.premier.project.entity.PremierPayableOrder;
import me.jiangcai.premier.project.repository.PremierPayableOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.UUID;

/**
 * @author CJ
 */
@Controller
public class PremierController {

    @Autowired
    private PaymentService paymentService;
    //    @Autowired
//    private DemoPaymentForm demoPaymentForm;
    @Autowired
    private ApplicationContext applicationContext;
    @Autowired
    private PremierPayableOrderRepository premierPayableOrderRepository;

    @RequestMapping(value = {"", "/"})
    public String index() {
        return "index.html";
    }

    @RequestMapping(value = "/goOrder")
    public ModelAndView goOrder(HttpServletRequest request, BigDecimal amount, String name, String type)
            throws SystemMaintainException, ClassNotFoundException {
        PremierPayableOrder order = new PremierPayableOrder();
        order.setOrderDueAmount(amount);
        order.setOrderProductName(name);
        order = premierPayableOrderRepository.saveAndFlush(order);

        // 如果是拉卡拉支付的话，应该更加具体的选择 微信扫码 或者 网页网关
        Map<String, Object> additionalParameters = new HashMap<>();
        String[] d = type.split("\\|");
        type = d[0];
        String[] toParameters = new String[d.length - 1];
        System.arraycopy(d, 1, toParameters, 0, d.length - 1);
        setupAdditionalParameters(additionalParameters, toParameters);

        return paymentService.startPay(request, order, (PaymentForm) applicationContext.getBean(Class.forName(type)), additionalParameters);
    }

    private void setupAdditionalParameters(Map<String, Object> additionalParameters, String[] toParameters) {
        for (String parameter : toParameters) {
            StringTokenizer tokenizer = new StringTokenizer(parameter, ":");
            String name = tokenizer.nextToken();
            String value = tokenizer.nextToken();
            additionalParameters.put(name, value);
            if ("channel".equals(name) && "wechatScan".equals(value))
                additionalParameters.put("openId", UUID.randomUUID().toString());
            if ("tradeType".equals(name) && "JSAPI".equals(value)) {
                additionalParameters.put("openId", UUID.randomUUID().toString());
            }
            if ("channel".equals(name) && "premier".equals(value)) {
                additionalParameters.put("type", 9);
            }
        }
    }

}
