package me.jiangcai.payment.util;

import javax.servlet.http.HttpServletRequest;

/**
 * @author CJ
 */
public class RequestUtil {

    public static StringBuilder buildContextUrl(HttpServletRequest httpRequest) {
        StringBuilder sb = new StringBuilder();
        sb.append(httpRequest.getScheme());
        sb.append("://");
        sb.append(httpRequest.getServerName());
        // 负数 或者433 或者80 就省略
        if (httpRequest.getServerPort() <= 0
                || (httpRequest.isSecure() && httpRequest.getServerPort() == 433)
                || (!httpRequest.isSecure() && httpRequest.getServerPort() == 80)
                )
            ;
        else {
            sb.append(":").append(httpRequest.getServerPort());
        }
        sb.append(httpRequest.getContextPath());
        return sb;
    }
}
