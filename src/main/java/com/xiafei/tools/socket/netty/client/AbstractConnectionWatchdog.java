package com.xiafei.tools.socket.netty.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.util.Timeout;
import io.netty.util.Timer;
import io.netty.util.TimerTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * <P>Description: 连接状态监控狗，放在pipeLine中第一个，
 * 当发现当前的链路关闭之后，进行12级指数增长间隔时间的重连尝试.
 * </P>
 * <p>
 * 之所以定义为抽象类是为了类初始化的时候强制使用匿名类实现handlers()方法.
 * </P>
 * <P>CALLED BY:   齐霞飞 </P>
 * <P>UPDATE BY:   齐霞飞 </P>
 * <P>CREATE DATE: 2017/7/7</P>
 * <P>UPDATE DATE: 2017/7/7</P>
 *
 * @author qixiafei
 * @version 1.0
 * @since java 1.7.0
 */
@ChannelHandler.Sharable
public abstract class AbstractConnectionWatchdog extends ChannelInboundHandlerAdapter
        implements TimerTask, ChannelHandlerHolder {

    /**
     * slf4j记录日志工具类.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractConnectionWatchdog.class);

    /**
     * 重试最大级别，2^12=4096，单位ms.
     */
    private static final int RETRY_MAX_LEVEL = 12;

    /**
     * 日志前缀，说明当前管理的是哪个连接.
     */
    private final String logPrefix;

    /**
     * netty 启动工具.
     */
    private final Bootstrap bootstrap;

    /**
     * 延迟执行的工具类.
     */
    private final Timer timer;

    /**
     * 服务器端口号.
     */
    private final int port;

    /**
     * 服务器IP.
     */
    private final String host;

    /**
     * 是否重连.
     */
    private volatile boolean reconnect = true;

    /**
     * 当前重试级别.
     */
    private int currentLevel;

    /**
     * 构造函数.
     *
     * @param pBootstrap netty 启动引导工具.
     * @param pTimer     定时后执行的工具类实现.
     * @param pPort      服务器端口号.
     * @param pHost      服务器IP.
     * @param pReconnect 断线是否重连.
     */
    AbstractConnectionWatchdog(final Bootstrap pBootstrap, final Timer pTimer, final int pPort, final String pHost,
                               final boolean pReconnect) {
        this.bootstrap = pBootstrap;
        this.timer = pTimer;
        this.port = pPort;
        this.host = pHost;
        this.reconnect = pReconnect;
        this.logPrefix = pHost + ":" + port + "，";
    }

    /**
     * channel链路每次active的时候，将其连接的次数重新置为0.
     */
    @Override
    public void channelActive(final ChannelHandlerContext ctx) throws Exception {
        LOGGER.info("{}链路已经建立连接，重连间隔级别重置为0", logPrefix);
        currentLevel = 0;
        ctx.fireChannelActive();
    }


    @Override
    public void channelInactive(final ChannelHandlerContext ctx) throws Exception {

        LOGGER.error("{}连接被服务器关闭，或重连失败。", logPrefix);
        // 如果构建类的时候指定的是需要重试
        if (reconnect) {
            // 没到间隔级别的最大值，增加一级
            if (currentLevel < RETRY_MAX_LEVEL) {
                currentLevel++;
            }
            // 重连的间隔时间会越来越长，指数增长
            final int timeout = 2 << currentLevel;
            LOGGER.info("{}当前重试间隔级别为{},将在{}秒后重试连接...", logPrefix, currentLevel - 1, timeout / 1000.0);

            // 延迟timeout毫秒后调度run()方法
            timer.newTimeout(this, timeout, TimeUnit.MILLISECONDS);
        }
        ctx.fireChannelInactive();
    }

    @Override
    public void run(final Timeout timeout) throws Exception {

        final ChannelFuture future;
        // bootstrap已经初始化好了，只需要将handler填入就可以了
        synchronized (bootstrap) {
            bootstrap.handler(new ChannelInitializer<Channel>() {
                @Override
                protected void initChannel(final Channel ch) throws Exception {
                    ch.pipeline().addLast(handlers());
                }
            });
            future = bootstrap.connect(host, port);
        }
        LOGGER.info("{}连接已经初始化，等待连接结果...", logPrefix);
        //future对象
        future.addListener(new ChannelFutureListener() {

            @Override
            public void operationComplete(final ChannelFuture f) throws Exception {
                //如果重连失败，则调用ChannelInactive方法，再次出发重连事件
                if (f.isSuccess()) {
                    LOGGER.info("{}重新连接成功！", logPrefix);
                } else {
                    LOGGER.error("{}重新连接失败！", logPrefix);
                    f.channel().pipeline().fireChannelInactive();
                }
            }
        });
    }

}
