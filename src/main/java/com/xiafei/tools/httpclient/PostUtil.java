package com.xiafei.tools.httpclient;


import com.google.gson.reflect.TypeToken;
import com.xiafei.tools.common.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * <P>Description: 使用Http-post方式发送数据. </P>
 * <P>CALLED BY:   齐霞飞 </P>
 * <P>UPDATE BY:   齐霞飞 </P>
 * <P>CREATE DATE: 2017/8/11</P>
 * <P>UPDATE DATE: 2017/8/11</P>
 *
 * @author qixiafei
 * @version 1.0
 * @since java 1.7.0
 */
@Slf4j
@Component
public class PostUtil implements Serializable {

    /**
     * 请求返回json串在返回map中的key值.
     */
    public static final String RESP_JSON_KEY = "respJson";

    /**
     * 请求类型键.
     */
    private static final String CONTENT_TYPE_KEY = "Content-Type";

    /**
     * 请求类型为json的值.
     */
    private static final String CONTENT_TYPE_JSON = "application/json;charset=" + "utf-8";

    /**
     * 请求类型为普通form表单的值.
     */
    private static final String CONTENT_TYPE_FORM = "application/x-www-form-urlencoded;charset="
            + "utf-8";

    @Resource
    private Environment env;

    /**
     * 工具类.
     */
    private PostUtil() {

    }


    /**
     * 发送HttpPost请求到合作方.
     *
     * @param coopCode 合作方编码.
     * @param reqJson  完整请求json
     * @return 服务器返回Json数据反序列化的TreeMap
     */
    public Map<String, Object> invokeToCoop(final String coopCode, final String reqJson) {
        final String respContent = postJson(getUrl(coopCode), reqJson);
        final Map<String, Object> result = JsonUtil.fromJson(respContent,
                new TypeToken<TreeMap<String, Object>>() {
                }.getType());
        result.put(RESP_JSON_KEY, respContent);
        return result;
    }


    /**
     * 发送post请求，json格式，业务无关.
     *
     * @param url     请求url地址
     * @param reqJson 请求json
     * @return 请求响应json串
     */
    public String postJson(final String url, final String reqJson) {

        final StringEntity entity = new StringEntity(reqJson, "utf-8");
        entity.setContentEncoding("utf-8");
        entity.setContentType(CONTENT_TYPE_JSON);

        return postAndGetResult(url, entity, CONTENT_TYPE_JSON);
    }

    /**
     * 发送post请求，普通form表单提交，业务无关.
     * application/x- www-form-urlencoded
     *
     * @param url    请求url地址
     * @param reqMap 请求数据
     * @return 请求响应json串
     */
    public String postForm(final String url, final Map<String, Object> reqMap) {

        final List<BasicNameValuePair> pairList = new ArrayList<>(reqMap.size());
        for (Map.Entry<String, Object> entry : reqMap.entrySet()) {
            pairList.add(new BasicNameValuePair(entry.getKey(), entry.getValue().toString()));
        }

        try {
            return postAndGetResult(url, new UrlEncodedFormEntity(pairList, "utf-8"),
                    CONTENT_TYPE_FORM);
        } catch (UnsupportedEncodingException e) {
            log.error("postForm(),普通form表单post提交，对参数列表进行url编码时报错", e);
            throw new RuntimeException();
        }
    }


    /**
     * 使用multipart/form-data格式发送Post请求，业务无关.
     *
     * @param url      发送地址
     * @param plainMap 除了文件之外的参数Map
     * @param fileMap  文件参数Map，key是请求map的key，value是multipartFile
     * @return 请求响应json串
     */
    public String postFormData(final String url, final Map<String, Object> plainMap,
                               final Map<String, MultipartFile> fileMap) {

        //构建multipartEntity对象
        final MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
        entityBuilder.setCharset(Charset.forName("utf-8"));
        // 封装一般参数
        for (Map.Entry<String, Object> entry : plainMap.entrySet()) {
            if (entry.getValue() == null) {
                continue;
            }
            final StringBody body = new StringBody(entry.getValue().toString(), ContentType.TEXT_PLAIN);
            entityBuilder.addPart(entry.getKey(), body);
        }
        // 封装文件参数
        for (Map.Entry<String, MultipartFile> entry : fileMap.entrySet()) {
            final InputStreamBody body;
            try {
                body = new InputStreamBody(entry.getValue().getInputStream(), entry.getValue().getOriginalFilename());
            } catch (IOException e) {
                log.error("postFormData(),封装请求参数，取出文件字节流异常", e);
                throw new RuntimeException();
            }
            entityBuilder.addPart(entry.getKey(), body);
        }
        return postAndGetResult(url, entityBuilder.build(), null);
    }

    /**
     * 使用multipart/form-data格式发送Post请求，业务无关.
     *
     * @param url
     * @param plainMap    除了文件之外的参数
     * @param fileMap     文件参数,key就是发往服务方数据中的key，value是输入流
     * @param fileNameMap 文件名参数，对应fileMap中的输入流，使用fileMap的key可以取得文件名
     * @return
     */
    public String postFormData(final String url, final Map<String, Object> plainMap,
                               final Map<String, InputStream> fileMap,
                               final Map<String, String> fileNameMap) {

        //构建multipartEntity对象
        final MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
        entityBuilder.setCharset(Charset.forName("utf-8"));
        // 封装一般参数
        for (Map.Entry<String, Object> entry : plainMap.entrySet()) {
            if (entry.getValue() == null) {
                continue;
            }
            entityBuilder.addTextBody(entry.getKey(), entry.getValue().toString(), ContentType.TEXT_PLAIN);
        }
        // 封装文件参数
        for (Map.Entry<String, InputStream> entry : fileMap.entrySet()) {
            entityBuilder.addBinaryBody(entry.getKey(), entry.getValue(), ContentType.DEFAULT_BINARY, fileNameMap.get(entry.getKey()));
        }
        return postAndGetResult(url, entityBuilder.build(), null);
    }

    /**
     * 使用multipart/form-data格式发送Post请求，业务无关.
     *
     * @param url
     * @param plainMap    除了文件之外的参数
     * @param fileMap     文件参数,key就是发往服务方数据中的key，value是字节数组
     * @param fileNameMap 文件名参数，对应fileMap中的输入流，使用fileMap的key可以取得文件名
     * @return
     */
    public String postFormDataBytes(final String url, final Map<String, Object> plainMap,
                                    final Map<String, byte[]> fileMap,
                                    final Map<String, String> fileNameMap) {

        //构建multipartEntity对象
        final MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
        entityBuilder.setCharset(Charset.forName("utf-8"));
        // 封装一般参数
        for (Map.Entry<String, Object> entry : plainMap.entrySet()) {
            if (entry.getValue() == null) {
                continue;
            }
            entityBuilder.addTextBody(entry.getKey(), entry.getValue().toString(), ContentType.TEXT_PLAIN);
        }
        // 封装文件参数
        for (Map.Entry<String, byte[]> entry : fileMap.entrySet()) {
            entityBuilder.addBinaryBody(entry.getKey(), entry.getValue(), ContentType.DEFAULT_BINARY, fileNameMap.get(entry.getKey()));
        }
        return postAndGetResult(url, entityBuilder.build(), null);
    }

    /**
     * 执行发送post请求并拿到结果的方法.
     *
     * @param url         发送请求地址
     * @param reqEntity   发送请求内容
     * @param contentType 请求头内容编码类型
     * @return 响应Json格式字符串
     */
    private String postAndGetResult(final String url, final HttpEntity reqEntity, final String contentType) {
        final CloseableHttpClient client = HttpClientPool.getHttpClient();
        // multipart/form-data不能指定请求头，因为我们不知道边界怎么设置，由工具去自动计算及设置
        final HttpPost post = new HttpPost(url);
        post.setEntity(reqEntity);
        if (contentType != null) {
            post.addHeader(CONTENT_TYPE_KEY, contentType);
        }

        try {
            return client.execute(post, new StringResponseHandler());
        } catch (IOException e) {
            log.error("调用http异常", e);
            throw new RuntimeException("调用http异常");
        }

    }

    /**
     * 根据合作方编码查找http服务地址.
     *
     * @param coopCode
     * @return
     */
    private String getUrl(final String coopCode) {
        return env.getProperty(coopCode.concat("_URL"));
    }

}
