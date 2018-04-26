package com.xiafei.tools.spring.springboot.aspect;

/**
 * <P>Description: 面向用户(User-Oriented)返回码枚举. </P>
 * <P>CALLED BY:   齐霞飞 </P>
 * <P>UPDATE BY:   齐霞飞 </P>
 * <P>CREATE DATE: 2017/11/23</P>
 * <P>UPDATE DATE: 2018/4/26</P>
 *
 * @author qixiafei
 * @version 1.0
 * @since java 1.7.0
 */
public enum UoCodeEnum {

    SUCCESS(0, "调用成功"),
    PWD_FAIL(999, "密码错误"),
    LOCK_FAIL(991, "密码连续输错超过5次，锁定账户！"),
    USER_NON_FAIL(990, "用户不存在"),
    PWD_EMPTY(992, "密码为空！"),
    USER_PHONE_EMPTY(993, "用户手机号为空！"),
    VALIDATE_OLD_PWD_FAIL(801, "旧密码错误！"),
    VALIDATE_CODE_FAIL(802, "验证码错误！"),
    VALIDATE_CODE_TIME_FAIL(803, "验证码过期！"),
    OLD_PWD_EMPTY(804, "旧密码为空"),
    NEW_PWD_EMPTY(805, "新密码为空"),
    VALIDATE_CODE_EMPTY(806, "验证码为空"),
    VALIDATE_CODE_PHONE_EMPTY(701, "手机号为空！"),
    SMS_TYPE_EMPTY(702, "短信类型为空！"),
    SMS_TYPE_ERROR(703, "短信类型错误！"),
    PARAMETER_IS_NULL(2000, "存在必传参数为空"),
    PARAMETER_IS_ILLEGAL(2001, "参数类型非法"),
    USER_NOT_LOGIN(2002, "用户未登录"),
    USER_LOSE_EFFC(2003, "用户已失效"),
    TIAN_YAN_CHA_ERROR(2050, "天眼查查询错误"),
    DOWNSTREAM_SYSTEM_ERROR(2060, "系统不稳定，请重试"),
    ID_CARD_ILLEGAL(2070, "身份证非法"),
    NO_IMG(2080, "没有上传待识别的图像"),
    PHONE_ILLEGAL(2090, "非法手机号"),
    DATA_ERROR(9996, "内部数据错误"),
    INTERNAL_EXCEPTION(9997, "内部调用异常"),
    DATA_LOSS(9998, "系统数据丢失"),
    SYSTEM_ERROR(9999, "系统异常");

    /**
     * 返回状态编码.
     */
    public final Integer code;

    /**
     * 返回状态描述.
     */
    public final String desc;

    UoCodeEnum(final Integer code, final String desc) {
        this.code = code;
        this.desc = desc;
    }
}
