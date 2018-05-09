package com.virgo.finance.lease.core.service.common;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * <P>Description: 等于cpu数量的线程池. </P>
 * <P>CALLED BY:   齐霞飞 </P>
 * <P>UPDATE BY:   齐霞飞 </P>
 * <P>CREATE DATE: 2018/5/9</P>
 * <P>UPDATE DATE: 2018/5/9</P>
 *
 * @author qixiafei
 * @version 1.0
 * @since java 1.8.0
 */
@Scope("prototype")
@Component
public class CpuThread {

    private ExecutorService pool;

    private static final int COUNT = Runtime.getRuntime().availableProcessors();

    public CpuThread() {
        pool = Executors.newFixedThreadPool(COUNT);
    }


    public void execute(Runnable task) {
        pool.execute(task);
    }
}
