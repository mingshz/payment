package me.jiangcai.premier.project.bean;

import me.jiangcai.payment.premier.PremierPaymentForm;
import me.jiangcai.payment.service.PaymentGatewayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @author CJ
 */
@Controller
public class PremierPayController {

    @Autowired
    private PremierPaymentForm premierPaymentForm;

    @RequestMapping(method = RequestMethod.PUT, value = "/pay/{id}")
    public ModelAndView pay(HttpServletRequest request, @PathVariable String id, @RequestBody Map<String, Object> requestBody) {
        //首先判断该订单是支付还是取消
        boolean success = (boolean) requestBody.get("success");
        String payUrl = (String) requestBody.get("payUrl");
        //修改订单状态
        //返回视图
        return premierPaymentForm.payOrCancel(request, id, success, payUrl);
    }

}
