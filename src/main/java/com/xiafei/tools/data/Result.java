/*
 * Copyright (c) 2016-2020 LEJR.COM All Right Reserved
 */

package com.xiafei.tools.data;

/**
 * 结果枚举。
 *
 * @author yanhuajian
 * @version 1.0
 * @date 2017/1/10 下午9:29
 */
public enum Result {

    /**
     * 成功。
     */
    SUCCESS(1),

    /**
     * 失败。(表示逻辑上的错误)
     */
    FAILED(0),

    /**
     * 错误。
     */
    ERROR(-1),
    /**
     * 修改密码
     */
    UPDATEPWD(2);

    private int value;

    private Result(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static boolean isSuccess(Result result) {
        if (result == Result.SUCCESS) {
            return true;
        }
        return false;
    }

}
