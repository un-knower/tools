package com.xiafei.tools.httpclient;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * <P>Description: . </P>
 * <P>CALLED BY:   齐霞飞 </P>
 * <P>UPDATE BY:   齐霞飞 </P>
 * <P>CREATE DATE: 2018/4/25</P>
 * <P>UPDATE DATE: 2018/4/25</P>
 *
 * @author qixiafei
 * @version 1.0
 * @since java 1.8.0
 */
@Slf4j
public class GetUtils {
    private static final String SHORT_URL_API = "http://suo.im/api.php?url=";

    public static void main(String[] args) throws IOException {
        System.out.println(getShortUrl("https://www.ginkgofit.com/"));

    }

    public static String getShortUrl(final String oriUrl) throws IOException {
        final String oriUrlEscape;
        try {
            oriUrlEscape = URLEncoder.encode("https://www.ginkgofit.com", "utf-8");
        } catch (UnsupportedEncodingException e) {
            log.error("getShortUrl(),将原url转码失败");
            throw new RuntimeException("生成短连接原url转码失败");
        }
        final CloseableHttpClient client = HttpClientPool.getHttpClient();
        final HttpGet get = new HttpGet(SHORT_URL_API.concat(oriUrlEscape));
        get.addHeader("Content-Type", "application/json; charset=utf-8");
        return client.execute(get, new StringResponseHandler());
    }

}
