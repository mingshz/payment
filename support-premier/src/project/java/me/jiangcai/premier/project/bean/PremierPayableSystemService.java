package me.jiangcai.premier.project.bean;

import me.jiangcai.payment.PayableOrder;
import me.jiangcai.payment.entity.PayOrder;
import me.jiangcai.payment.event.OrderPaySuccess;
import me.jiangcai.payment.service.PayableSystemService;
import me.jiangcai.premier.project.entity.PremierPayableOrder;
import me.jiangcai.premier.project.repository.PremierPayableOrderRepository;
import me.jiangcai.wx.pay.entity.WeixinPayOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @author CJ
 */
@Service
public class PremierPayableSystemService implements PayableSystemService {

    @Autowired
    private PremierPayableOrderRepository premierPayableOrderRepository;

    @EventListener(OrderPaySuccess.class)
//    @Transactional
    public void paySuccess(OrderPaySuccess event) {
        PremierPayableOrder demoTradeOrder = (PremierPayableOrder) event.getPayableOrder();
        demoTradeOrder.setDone(true);
    }

    @Override
    public ModelAndView paySuccess(HttpServletRequest request, PayableOrder payableOrder, PayOrder payOrder) {
        return new ModelAndView("paySuccess.html");
    }

    @Override
    public ModelAndView pay(HttpServletRequest request, PayableOrder order, PayOrder payOrder, Map<String, Object> additionalParameters) {
        if (payOrder instanceof WeixinPayOrder) {
            return new ModelAndView("weixin-pay/pay.html");
        }
        return new ModelAndView("pay.html");
    }

    @Override
    public boolean isPaySuccess(String id) {
        return premierPayableOrderRepository.getOne(Long.parseLong(id)).isDone();
    }

    @Override
    public PayableOrder getOrder(String id) {
        return premierPayableOrderRepository.getOne(Long.parseLong(id));
    }
}
