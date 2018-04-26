package com.xiafei.tools.spring.springboot.aspect;


import lombok.Data;

/**
 * <P>Description: 返回vo基类. </P>
 * <P>CALLED BY:   齐霞飞 </P>
 * <P>CREATE DATE: 2017/11/22</P>
 *
 * @author qixiafei
 * @version 1.0
 * @since java 1.7.0
 */
@Data
public class BaseRespVo<T> {

    /**
     * 返回消息码.
     */
    private String code = String.valueOf(UoCodeEnum.SUCCESS.code);

    /**
     * 错误消息.
     */
    private String message = UoCodeEnum.SUCCESS.desc;

    /**
     * 数据.
     */
    private T data;

    public BaseRespVo() {

    }

    public BaseRespVo(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public BaseRespVo(T data) {
        this.data = data;
    }
}
