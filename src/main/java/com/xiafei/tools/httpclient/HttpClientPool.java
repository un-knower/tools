package com.xiafei.tools.httpclient;

import org.apache.http.Consts;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CookieStore;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.AuthSchemes;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.config.MessageConstraints;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.DnsResolver;
import org.apache.http.conn.HttpConnectionFactory;
import org.apache.http.conn.ManagedHttpClientConnection;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.DefaultHttpResponseFactory;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.DefaultHttpResponseParser;
import org.apache.http.impl.conn.DefaultHttpResponseParserFactory;
import org.apache.http.impl.conn.ManagedHttpClientConnectionFactory;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.impl.conn.SystemDefaultDnsResolver;
import org.apache.http.impl.io.DefaultHttpRequestWriterFactory;
import org.apache.http.io.HttpMessageParser;
import org.apache.http.io.HttpMessageParserFactory;
import org.apache.http.io.HttpMessageWriterFactory;
import org.apache.http.io.SessionInputBuffer;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicLineParser;
import org.apache.http.message.LineParser;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.CharArrayBuffer;

import javax.net.ssl.SSLContext;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.CodingErrorAction;
import java.util.Arrays;

/**
 * <P>Description: HttpClient连接池. </P>
 * <P>CALLED BY:   齐霞飞 </P>
 * <P>UPDATE BY:   齐霞飞 </P>
 * <P>CREATE DATE: 2018/4/27</P>
 * <P>UPDATE DATE: 2018/4/27</P>
 *
 * @author qixiafei
 * @version 1.0
 * @since java 1.8.0
 */
public class HttpClientPool {

    /**
     * 连接池中保持的最大总连接数.
     */
    private static final int MAX_TOTAL = 1000;

    /**
     * 每个路由能保持的最大连接数.
     */
    private static final int ROUTE_MAX = 50;

    /**
     * 连接池管理.
     */
    private static PoolingHttpClientConnectionManager cm;

    /**
     * 默认的请求配置.
     */
    private static RequestConfig defaultRequestConfig;

    /**
     * 如果需要定义cookie存储.
     */
    private static CookieStore cookieStore;

    /**
     * 认证.
     */
    private static CredentialsProvider credentialsProvider;

    /**
     * 从池中获取客户端
     *
     * @return 可用的客户端
     */
    public static CloseableHttpClient getHttpClient() {
        return HttpClients.custom().setConnectionManager(cm).setDefaultCookieStore(cookieStore).
                setDefaultCredentialsProvider(credentialsProvider).setDefaultRequestConfig(defaultRequestConfig).
                build();
    }

    static {
        cookieStore = new BasicCookieStore();
        defaultRequestConfig = RequestConfig.custom().
                setCookieSpec(CookieSpecs.DEFAULT).
                setExpectContinueEnabled(true).
                setTargetPreferredAuthSchemes(Arrays.asList(AuthSchemes.NTLM, AuthSchemes.DIGEST)).
                setProxyPreferredAuthSchemes(Arrays.asList(AuthSchemes.BASIC)).
                setConnectionRequestTimeout(2000).
                setConnectTimeout(10000).
                setSocketTimeout(10000).
                build();
        credentialsProvider = new BasicCredentialsProvider();
        // 如果服务器需要验证
//        credentialsProvider.setCredentials(
//                new AuthScope("httpbin.org", 80), new UsernamePasswordCredentials("userName", "password"));


        // 响应处理工场
        final HttpMessageParserFactory<HttpResponse> responseParserFactory = new DefaultHttpResponseParserFactory() {

            @Override
            public HttpMessageParser<HttpResponse> create(SessionInputBuffer buffer, MessageConstraints constraints) {
                LineParser lineParser = new BasicLineParser() {

                    @Override
                    public Header parseHeader(final CharArrayBuffer buffer) {
                        try {
                            // 响应header处理，每一次进入该方法都是一条header，在这里可以改变响应的header
                            // 一个buffer是一条header的内容
                            return super.parseHeader(buffer);
                        } catch (ParseException ex) {
                            return new BasicHeader(buffer.toString(), null);
                        }
                    }

                };
                return new DefaultHttpResponseParser(buffer, lineParser, DefaultHttpResponseFactory.INSTANCE, constraints) {

                    @Override
                    protected boolean reject(final CharArrayBuffer line, int count) {
                        // try to ignore all garbage preceding a status line infinitely
                        return false;
                    }

                };
            }

        };
        // 请求处理工厂
        final HttpMessageWriterFactory<HttpRequest> requestWriterFactory =
                new DefaultHttpRequestWriterFactory();

        // 连接工厂
        final HttpConnectionFactory<HttpRoute, ManagedHttpClientConnection> connFactory =
                new ManagedHttpClientConnectionFactory(requestWriterFactory, responseParserFactory);

        // https配置
        final SSLContext sslcontext = SSLContexts.createSystemDefault();

        final Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("http", PlainConnectionSocketFactory.INSTANCE)
                .register("https", new SSLConnectionSocketFactory(sslcontext))
                .build();


        // 初始化连接池
        cm = new PoolingHttpClientConnectionManager(
                socketFactoryRegistry, connFactory, getSpecialDnsResolver());

        final SocketConfig defaultSocktConfig = SocketConfig.custom().setTcpNoDelay(true).build();
        cm.setDefaultSocketConfig(defaultSocktConfig);
        // 每隔一秒重新验证失效的连接是否可用
        cm.setValidateAfterInactivity(1000);

        // 消息配置
        final MessageConstraints messageConstraints = MessageConstraints.custom().
                setMaxHeaderCount(-1). // 最大头部长度，头部条数+1，当头部条数等于该配置时就抛出异常，设置为-1为不限制长度
                setMaxLineLength(-1). // 消息内容最大长度，字符数+1，设置为-1为不限
                build();
        // 默认连接配置
        final ConnectionConfig defaultConnectionConfig = ConnectionConfig.custom().
                setMalformedInputAction(CodingErrorAction.IGNORE).
                setUnmappableInputAction(CodingErrorAction.IGNORE).
                setCharset(Consts.UTF_8).
                setMessageConstraints(messageConstraints).
                build();

        cm.setDefaultConnectionConfig(defaultConnectionConfig);
        cm.setMaxTotal(MAX_TOTAL);
        cm.setDefaultMaxPerRoute(ROUTE_MAX);

        setSpecialSocketConfig(cm);
        setSpecialConnectionConfig(cm);
        setSpecialPerRoute(cm);
    }

    /**
     * 控制dns解析.
     *
     * @return
     */
    private static DnsResolver getSpecialDnsResolver() {

        final DnsResolver dnsResolver = new SystemDefaultDnsResolver() {

            @Override
            public InetAddress[] resolve(final String host) throws UnknownHostException {
                if (host.equalsIgnoreCase("myhost")) {
                    // 如果域名是myhost，则映射到ip  175.6.26.203
                    return new InetAddress[]{InetAddress.getByAddress(new byte[]{(byte) 175, 6, 26, (byte) 203})};
                } else {
                    return super.resolve(host);
                }
            }

        };

        return dnsResolver;
    }

    /**
     * 设置某些连接特别的socket连接配置.
     *
     * @param cm
     */
    private static void setSpecialSocketConfig(final PoolingHttpClientConnectionManager cm) {
        final SocketConfig socketConfig = SocketConfig.custom().setTcpNoDelay(true).build();

        cm.setSocketConfig(new HttpHost("somehost", 80), socketConfig);

    }

    /**
     * 设置某些连接特别的连接配置.
     *
     * @param cm
     */
    private static void setSpecialConnectionConfig(final PoolingHttpClientConnectionManager cm) {
        cm.setConnectionConfig(new HttpHost("somehost", 80), ConnectionConfig.DEFAULT);
    }


    /**
     * 设置需要特别声明的某些路由的请求的最大连接数量.
     *
     * @param cm 连接配置管理
     */
    private static void setSpecialPerRoute(final PoolingHttpClientConnectionManager cm) {
        cm.setMaxPerRoute(new HttpRoute(new HttpHost("somehost", 80)), 20);
    }
}
