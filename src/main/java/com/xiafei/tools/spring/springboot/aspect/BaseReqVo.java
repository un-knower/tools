package com.xiafei.tools.spring.springboot.aspect;


import com.xiafei.tools.common.check.CheckUtils;

/**
 * <P>Description: 请求vo基类. </P>
 * <P>CALLED BY:   齐霞飞 </P>
 * <P>UPDATE BY:   齐霞飞 </P>
 * <P>CREATE DATE: 2017/11/24</P>
 * <P>UPDATE DATE: 2017/11/24</P>
 *
 * @author qixiafei
 * @version 1.0
 * @since java 1.7.0
 */
public class BaseReqVo {

    /**
     * 操作用户id，前端不需要传.
     */
    @CheckUtils.PropertySkipCheck
    private String userAccountId;

    public final String getUserAccountId() {
        return userAccountId;
    }

    public final void setUserAccountId(String userAccountId) {
        this.userAccountId = userAccountId;
    }

    @Override
    public String toString() {
        return "BaseReqVo{" +
                ", userAccountId='" + userAccountId + '\'' +
                '}';
    }
}
