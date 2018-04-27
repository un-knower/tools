package com.xiafei.tools.spring.springboot.aspect;

import com.xiafei.tools.common.check.CheckUtils;
import com.xiafei.tools.exceptions.BizException;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * <P>Description: httpController切面配置. </P>
 * <P>CALLED BY:   齐霞飞 </P>
 * <P>UPDATE BY:   齐霞飞 </P>
 * <P>CREATE DATE: 2017/11/20</P>
 * <P>UPDATE DATE: 2017/11/20</P>
 *
 * @author qixiafei
 * @version 1.0
 * @since java 1.7.0
 */
@Slf4j
@Aspect
@Component
public class HttpAspect {

    /**
     * 定义拦截规则：拦截web.api.impl包下面的所有类返回值是Message并且不含有IgnoreAop注解的public方法.
     */
    /**
     * 定义拦截规则：拦截web.controller.kry包下面的所有类的公共方法不含有IgnoreAop注解并且包含RequestMapper的public方法.
     * 这些方法必须第一个参数是version，若含有CheckToken注解，则第二个参数必须是token，可以含有多个reqVo，必须继承自BaseReqVo
     */
    @Pointcut("(execution( "
            + "!@com.xiafei.tools.spring.springboot.aspect.SkipAspect "
            + " public "
            + " com.xiafei.tools.spring.springboot.aspect.BaseRespVo "
            + "com.xiafei.tools.spring.springboot.aspect.*.*(..))"
            + ")"
            + " && "
            + "((execution( "
            + "@org.springframework.web.bind.annotation.RequestMapping "
            + " public "
            + " com.xiafei.tools.spring.springboot.aspect.BaseRespVo "
            + "com.xiafei.tools.spring.springboot.aspect.*.*(..)) "
            + ") "
            + "|| "
            + "(execution( "
            + "@org.springframework.web.bind.annotation.PostMapping "
            + " public "
            + " com.xiafei.tools.spring.springboot.aspect.BaseRespVo "
            + "com.xiafei.tools.spring.springboot.aspect.*.*(..)) "
            + ") "
            + "|| "
            + "(execution( "
            + "@org.springframework.web.bind.annotation.GetMapping "
            + " public "
            + " com.xiafei.tools.spring.springboot.aspect.BaseRespVo "
            + "com.xiafei.tools.spring.springboot.aspect.*.*(..)) "
            + ") "
            + ")")
    public void controllerPointCut() {
    }

    /**
     * 拦截所有controller的公共方法，catch异常并记录日志.
     *
     * @param point 切点信息对象
     * @return contorller返回对象
     */
    @Around("controllerPointCut()")
    public Object interceptor(final ProceedingJoinPoint point) {
        // 拿到切点方法签名
        final MethodSignature signature = (MethodSignature) point.getSignature();
        // 获取被拦截的方法
        final Method method = signature.getMethod();
        //获取被拦截的方法名
        final String methodName = method.getName();
        // 获取被拦截方法中参数注解
        final Annotation[][] parameterAnnotations = method.getParameterAnnotations();
        // 被拦截的方法的参数
        final Object[] args = point.getArgs();
        // 拿到请求基类，一会儿要往里放redis缓存中存的userAccountId
        final List<BaseReqVo> reqVos = new ArrayList<>();
        // 被检查的参数列表
        final List<Object> argsToCheck = new ArrayList<>(args.length);
        for (int i = 0, len = args.length; i < len; i++) {
            final Object arg = args[i];
            if (arg == null) {
                continue;
            }
            if (BaseReqVo.class.isAssignableFrom(arg.getClass())) {
                reqVos.add((BaseReqVo) arg);
            }

            if (BaseReqVo.class == arg.getClass()) {
                boolean find = false;
                for (Annotation annotation : parameterAnnotations[i]) {
                    if (annotation.annotationType().equals(CheckUtils.ParamSkipCheck.class)) {
                        find = true;
                        break;
                    }
                }
                if (find) {
                    continue;
                }
            }
            argsToCheck.add(arg);
        }
        log.info("{}.{}()\r\nparam={}", point.getTarget().getClass().getName(), methodName, args);

        // 一切正常的情况下，继续执行被拦截的方法
        try {
            try {

                CheckUtils.checkNull(argsToCheck.toArray());
            } catch (Exception e) {
                log.info("H5请求信息参数校验失败", e);
                throw new BizException(UoCodeEnum.PARAMETER_IS_NULL.code, "存在必填项未填");
            }
            // 如果含有校验token注解，校验token并且将用户id从缓存取出来放入vo
            if (method.getAnnotation(CheckToken.class) != null) {
                final String userAccountId = checkToken(args[0].toString());
                for (BaseReqVo vo : reqVos) {
                    vo.setUserAccountId(userAccountId);
                }
            }
            final Object resp = point.proceed(args);
            log.debug("{}.{}(),\r\nresp={}", point.getTarget().getClass().getName(), methodName, resp);
            return resp;
        } catch (BizException e) {
            final BaseRespVo<Boolean> baseRespVo = new BaseRespVo<>();
            baseRespVo.setCode(String.valueOf(e.getCode()));
            baseRespVo.setMessage(e.getMessage());
            return baseRespVo;
        } catch (Throwable throwable) {
            log.error("{}.{}()\r\nuncaughtException,param={}", point.getTarget().getClass().getName(), methodName, args, throwable);
            final BaseRespVo<Boolean> baseRespVo = new BaseRespVo<>();
            baseRespVo.setCode(String.valueOf(UoCodeEnum.SYSTEM_ERROR.code));
            baseRespVo.setMessage(UoCodeEnum.SYSTEM_ERROR.desc);
            return baseRespVo;
        }
    }

    /**
     * 检查token是否存在并且刷新token持续时间并返回userAccountId.
     *
     * @param token 用户登录token
     * @return userAccountId ，如果返回空说明用户未登录
     */
    private String checkToken(final String token) {
//        final String key = RedisConfig.getUserTokenKey(token);
//        final String userAccountId = JedisClient.get(key);
//        if (userAccountId == null) {
//            log.error("redis中拿不到token,用户未登录");
//            throw new BizException(H5RespCodeEnum.USER_NOT_LOGIN.code, H5RespCodeEnum.USER_NOT_LOGIN.desc);
//
//        }
//        if (JedisClient.setIfExist(key, userAccountId, RedisConfig.USER_TOKEN_OVERTIME_SECONDS)) {
//            return userAccountId;
//        }
//        log.error("登录同时另一个客户端请求登出,用户未登录");
//        throw new BizException(H5RespCodeEnum.USER_NOT_LOGIN.code, H5RespCodeEnum.USER_NOT_LOGIN.desc);
        return "";
    }
}
