package me.jiangcai.payment.premier;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.HttpClientUtils;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.LayeredConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.nio.charset.Charset;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

/**
 * 商户可参考本类编写发送请求方法，也可直接使用本类
 */

public class HttpsClientUtil {

    private static HttpClient createAuthNonHttpClient() {
        SocketConfig socketConfig = SocketConfig.custom().setSoTimeout(100000).build();
        RegistryBuilder<ConnectionSocketFactory> registryBuilder = RegistryBuilder.<ConnectionSocketFactory>create();
        ConnectionSocketFactory plainSF = new PlainConnectionSocketFactory();
        registryBuilder.register("http", plainSF);
        //指定信任密钥存储对象和连接套接字工厂
        try {
            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            SSLContext sslContext = org.apache.http.ssl.SSLContexts.custom().useProtocol("TLS")
                    .loadTrustMaterial(trustStore, new AnyTrustStrategy()).build();
//            SSLContext sslContext = SSLContexts.custom().useTLS().loadTrustMaterial(trustStore, new AnyTrustStrategy()).build();
            LayeredConnectionSocketFactory sslSF = new SSLConnectionSocketFactory(sslContext, new NoopHostnameVerifier());
            registryBuilder.register("https", sslSF);
        } catch (KeyStoreException | KeyManagementException | NoSuchAlgorithmException e) {
            // 这种错误发生时候无法自行处理的。
            throw new IllegalStateException(e);
        }
        Registry<ConnectionSocketFactory> registry = registryBuilder.build();
        //设置连接参数
        ConnectionConfig connConfig = ConnectionConfig.custom().setCharset(Charset.forName("utf-8")).build();
        //设置连接管理器
        PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager(registry);
        connManager.setDefaultConnectionConfig(connConfig);
        connManager.setDefaultSocketConfig(socketConfig);
        //指定cookie存储对象
        BasicCookieStore cookieStore = new BasicCookieStore();
        return HttpClientBuilder.create().setDefaultCookieStore(cookieStore).setConnectionManager(connManager).build();
    }


    /**
     * 发送json格式请求到指定地址
     *
     * @param url
     * @param json
     * @return
     */
    public static String sendRequest(String url, String json) throws IOException {
        String strResult = "";
        HttpResponse resp = null;
        HttpClient httpClient = createAuthNonHttpClient();//new DefaultHttpClient();
        try {
            HttpPost httpPost = new HttpPost(url);
            httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
            if (json != null) {
                httpPost.setHeader("Content-Type", "application/json;charset=UTF-8");
                HttpEntity postEntity = new StringEntity(json, "utf-8");
                httpPost.setEntity(postEntity);
            }
            resp = httpClient.execute(httpPost);
            HttpEntity entity = resp.getEntity();
            strResult = EntityUtils.toString(entity);

        } finally {
            HttpClientUtils.closeQuietly(resp);
            HttpClientUtils.closeQuietly(httpClient);
        }
        return strResult;
    }


    private static class AnyTrustStrategy implements TrustStrategy {

        @Override
        public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            return true;
        }

    }
}
