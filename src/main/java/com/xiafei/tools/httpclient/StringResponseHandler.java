package com.xiafei.tools.httpclient;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

/**
 * <P>Description: http响应处理器.
 * 调用httpClient.execute时当做参数传入，返回值就是String类型，并且response会被正确释放，强烈推荐使用这种方式</P>
 * <P>CALLED BY:   齐霞飞 </P>
 * <P>UPDATE BY:   齐霞飞 </P>
 * <P>CREATE DATE: 2018/4/27</P>
 * <P>UPDATE DATE: 2018/4/27</P>
 *
 * @author qixiafei
 * @version 1.0
 * @since java 1.8.0
 */
public class StringResponseHandler implements ResponseHandler<String> {

    @Override
    public String handleResponse(final HttpResponse httpResponse) throws IOException {
        final int status = httpResponse.getStatusLine().getStatusCode();
        if (status >= 200 && status < 300) {
            final HttpEntity entity = httpResponse.getEntity();
            return entity != null ? EntityUtils.toString(entity) : null;
        } else {
            throw new ClientProtocolException("Unexpected response status: " + status);
        }
    }
}
