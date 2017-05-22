package me.jiangcai.payment.controller;

import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StreamUtils;
import org.springframework.web.servlet.view.AbstractTemplateView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.util.Map;

/**
 * @author CJ
 */
public class CheckSuccessLaterView extends AbstractTemplateView {

    CheckSuccessLaterView() {
        setContentType("text/html; charset=UTF-8");
    }

    @Override
    protected void renderMergedTemplateModel(Map<String, Object> model, HttpServletRequest request
            , HttpServletResponse response) throws Exception {
        try (InputStream in = new ClassPathResource("/payment/checkSuccess.html").getInputStream()) {
            StreamUtils.copy(in, response.getOutputStream());
        }
    }
}
