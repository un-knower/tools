package com.xiafei.tools.exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 业务异常(需要决定是否需要事务回滚)
 */
public class BizException extends RuntimeException {


    private static final Logger logger = LoggerFactory.getLogger(BizException.class);
    /**
     * 序列化标识
     */
    private static final long serialVersionUID = -659935883673661594L;

    private int code;

    public BizException(String msg) {
        super(msg);
        logger.error(msg);
    }

    public BizException(int code, String msg) {
        super(msg);
        this.code = code;
        logger.error("code=" + code + "msg=" + msg);
    }

    public BizException(int code, String msg, Exception e) {
        super(msg, e);
        this.code = code;
        logger.error("code=" + code + "msg=" + msg);
    }

    /**
     * setters && getters
     */
    public int getCode() {
        return code;
    }

}
