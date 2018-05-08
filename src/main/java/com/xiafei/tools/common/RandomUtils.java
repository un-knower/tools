package com.xiafei.tools.common;

import java.util.Random;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * <P>Description: 随机数工具类. </P>
 * <P>CALLED BY:   齐霞飞 </P>
 * <P>UPDATE BY:   齐霞飞 </P>
 * <P>CREATE DATE: 2017/7/10</P>
 * <P>UPDATE DATE: 2017/7/10</P>
 *
 * @author qixiafei
 * @version 1.0
 * @since java 1.7.0
 */
public final class RandomUtils extends Random {

    /**
     * 防重数字.
     */
    private static volatile int preventDuplicateNumber;

    /**
     * 防重数字循环周期，到达后从0开始.
     */
    private static final int PREVENT_DUPLICATE_NUMBER_CYCLE = 99;

    /**
     * 公平锁.
     */
    private static final Lock PREVENT_DUPLICATE_NUMBER_LOCK = new ReentrantLock(true);

    /**
     * 不允许实例化.
     */
    private RandomUtils() {
        super();
    }

    /**
     * 生成System.currentTimeMillis()后追加固定5位随机数的字符串.
     *
     * @return System.currentTimeMillis()后追加固定5位随机数的字符串.
     */
    public static String generateTimeMillisAppend5Random() {
        return generateTimeMillisAppendRandom(5);
    }

    /**
     * 生成System.currentTimeMillis()后追加randomDigit位随机数的字符串.
     *
     * @param randomDigit 随机数的位数
     * @return System.currentTimeMillis()后追加randomDigit位随机数的字符串
     */
    public static String generateTimeMillisAppendRandom(final int randomDigit) {
        if (randomDigit <= 0) {
            return null;
        }
        // 本地防重数长度
        final int localPreventDuplicateNumberLength = String.valueOf(PREVENT_DUPLICATE_NUMBER_CYCLE).length();
        // 如果指定随机数长度小于等于本地防重数长度，直接得到结果
        if (randomDigit <= localPreventDuplicateNumberLength) {
            return System.currentTimeMillis()
                    + getLocalPreventDuplicateNumber().substring(localPreventDuplicateNumberLength - randomDigit);
        }

        // int 最大数214748364，十位，将指定随机数长度按不大于9每份拆分。
        final int maxPartSize = String.valueOf(Integer.MAX_VALUE).length() - 1;
        // 最大随机基数
        final int maxRandomBase = (int) Math.pow(10, maxPartSize);
        // 预留本地防重数的位置，摩maxPartSize余数
        final int modMaxPartSize = (randomDigit - localPreventDuplicateNumberLength) % maxPartSize;
        // 预留本地防重数的位置，除maxPartSize得数
        final int divideMaxPartSize = (randomDigit - localPreventDuplicateNumberLength) / maxPartSize;

        // 组装返回结果,先将当前TimeMillis放在最前面
        final StringBuilder returnSb = new StringBuilder(String.valueOf(System.currentTimeMillis()));
        // 为防止极端情况 （同一毫秒，相同随机数位数，随机结果完全相同）的发生，使用一个成员变量来保证以上情况下生成100个随机数也不可能有重复
        returnSb.append(getLocalPreventDuplicateNumber());
        // 生成modMaxPartSize位随机数
        returnSb.append(StringUtils.fillZeroLeftToDigit(
                (int) (Math.random() * Math.pow(10, modMaxPartSize)), modMaxPartSize));
        // 循环生成divideMaxPartSize位随机数
        for (int i = 0; i < divideMaxPartSize; i++) {
            returnSb.append(StringUtils.fillZeroLeftToDigit((int) (Math.random() * maxRandomBase), maxPartSize));
        }
        return returnSb.toString();
    }

    /**
     * 获取本地防重数字字符串.
     *
     * @return 本地防重数字字符串
     */
    private static String getLocalPreventDuplicateNumber() {
        PREVENT_DUPLICATE_NUMBER_LOCK.lock();
        try {
            if (preventDuplicateNumber == PREVENT_DUPLICATE_NUMBER_CYCLE) {
                preventDuplicateNumber = 0;
            } else {
                preventDuplicateNumber++;
            }
        } finally {
            PREVENT_DUPLICATE_NUMBER_LOCK.unlock();
        }
        return StringUtils.fillZeroLeftToDigit(preventDuplicateNumber,
                String.valueOf(PREVENT_DUPLICATE_NUMBER_CYCLE).length());
    }
}
