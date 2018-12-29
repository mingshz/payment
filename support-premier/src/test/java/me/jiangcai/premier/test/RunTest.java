package me.jiangcai.premier.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

public class RunTest extends PremierPaymentTest {
    private ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void go() throws Exception {
        testOrderFor("易支付");
    }


//    @Test
//    public void testRun() throws Exception {
//        StringBuilder sb = new StringBuilder();
//
//        String customerId = "1535535402498";
//        String backUrl = "www.baidu.com";
//        String notifyUrl = "www.baidu.com";
//        String mark = "测试订单";
//        String remarks = "测试订单详情";
//        BigDecimal orderMoney = new BigDecimal("0.01");
//        String orderNo = "1";
//        String payType = "9";
//        String key = "7692ecf5b63949337473755b062f2434";
//
//        sb.append("backUrl=").append(backUrl).append("&");
//        sb.append("customerId=").append(customerId).append("&");
//        sb.append("mark=").append(mark).append("&");
//        sb.append("notifyUrl=").append(notifyUrl).append("&");
//        sb.append("orderMoney=").append(orderMoney).append("&");
//        sb.append("orderNo=").append(orderNo).append("&");
//        sb.append("payType=").append(payType).append("$");
//        sb.append("remarks=").append(remarks).append("&");
//
//        String Md5str = "customerId=" + customerId + "&orderNo=" + orderNo + "&orderMoney=" + orderMoney + "&payType=" + payType + "&notifyUrl=" + notifyUrl + "&backUrl=" + backUrl + key;
////        String strMd5 = sb.toString() + "key=" + key;
//        System.out.println(Md5str);
//        String sign;
//        try {
//            sign = DigestUtils.md5Hex(Md5str.getBytes("UTF-8")).toUpperCase();
//        } catch (Throwable ex) {
//            throw new SystemMaintainException(ex);
//        }
//
//        String requestUrl = "https://api.aisaepay.com/companypay/easyPay/recharge" + "?customerId=" + customerId + "&orderNo=" + orderNo + "&orderMoney=" + orderMoney + "&payType=" + payType + "&notifyUrl=" + notifyUrl + "&backUrl=" + backUrl + "&sign=" + sign + "&mark=" + mark + "&remarks=" + remarks;
//        JSONObject responseMap;
//        try {
//            String responseStr = HttpsClientUtil.sendRequest(requestUrl, null);
//            System.out.println(responseStr);
//            responseMap = JSON.parseObject(responseStr);
//            String status = responseMap.getString("status");
//            if ("1".equals(status)) {
//                //通信成功
//                if ("1".equals(responseMap.getJSONObject("data").getString("state"))) {
//                    // 业务成功
//                    System.out.println("支付成功");
//                } else {
//                    // 业务失败
//                    throw new PlaceOrderException("业务失败" + responseMap.getJSONObject("data").getString("msg"));
//                }
//            } else {
//                throw new PlaceOrderException("支付失败" + responseMap.getString("msg"));
//                //通信失败
//            }
//        } catch (Throwable ex) {
//            throw new SystemMaintainException(ex);
//
//        }
//    }
}
