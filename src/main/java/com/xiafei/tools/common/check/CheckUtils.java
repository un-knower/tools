package com.xiafei.tools.common.check;

import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * <P>Description: 检查工具类，与业务耦合. </P>
 * <P>CALLED BY:   齐霞飞 </P>
 * <P>UPDATE BY:   齐霞飞 </P>
 * <P>CREATE DATE: 2017/11/23</P>
 * <P>UPDATE DATE: 2017/11/23</P>
 *
 * @author qixiafei
 * @version 1.0
 * @since java 1.7.0
 */
@Slf4j
public class CheckUtils {

    private static final String nullDesc = "必填参数为空";

    private CheckUtils() {

    }

    /**
     * 在需要跳过检查的属性上加这个注解.
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface PropertySkipCheck {

    }

    /**
     * 在需要跳过检查的参数上加这个注解.
     */
    @Target(ElementType.PARAMETER)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface ParamSkipCheck {

    }


    /**
     * 检查对象及字段是否为空，最多遍历两层.
     *
     * @param objs 要检查的对象，可变.
     */
    public static void checkNull(Object... objs) throws Exception {
        if (objs == null) {
            throw new Exception(nullDesc);
        }
        for (Object obj : objs) {
            checkObj(obj, null);
        }
    }

    /**
     * 检查对象字段是否为空,最多向下遍历两层..
     *
     * @param obj   要检查的对象
     * @param clazz 要检查的对象class
     * @param name
     */
    private static void checkPojo(final Object obj, final Class clazz, final String name) throws Exception {
        final List<Field> fieldList = new ArrayList<>(Arrays.asList(clazz.getDeclaredFields()));
        Class tempClass = clazz;
        while (true) {
            tempClass = tempClass.getSuperclass();
            if (tempClass == null || tempClass == Object.class) {
                break;
            }
            Field[] fields = tempClass.getDeclaredFields();
            if (fields != null && fields.length > 0) {
                fieldList.addAll(new ArrayList<>(Arrays.asList(fields)));
            }
        }
        final Field[] fields = new Field[fieldList.size()];
        fieldList.toArray(fields);

        for (Field field : fields) {
            field.setAccessible(true);
            // 如果有跳过注解的字段放过检查
            if (field.getAnnotation(PropertySkipCheck.class) != null) {
                continue;
            }
            final Object propertyValue;
            try {
                propertyValue = field.get(obj);
            } catch (IllegalAccessException e) {
                log.error("反射获取属性值对象报错", e);
                throw new Exception(field.getName() + "校验失败");
            }
            checkObj(propertyValue, name == null ? field.getName() : name.concat(".").concat(field.getName()));
        }
    }


    private static void checkObj(final Object obj, final String name) throws Exception {
        final String errorMsg;
        if (name != null) {
            errorMsg = name.concat("不能为空");
        } else {
            errorMsg = nullDesc;
        }
        // 如果对象为空，抛出异常
        if (obj == null) {
            throw new Exception(errorMsg);
        }
        final Class clazz = obj.getClass();
        // 字符串判断是否是空串
        if (clazz == String.class) {
            if (obj.toString().trim().equals("")) {
                throw new Exception(errorMsg);
            }
        }

        // 如果是可以遍历的类型，比如list，set，遍历处理参数
        if (Iterable.class.isAssignableFrom(clazz)) {
            int count = 0;
            for (Object property : (Iterable) obj) {
                count++;
                checkObj(property, name);
            }
            if (count == 0) {
                throw new Exception(errorMsg);
            }
            return;
        }

        // 如果是pojo，判断属性是否含有空的
        if (isPojo(clazz)) {
            // 如果不是基本类型，利用内省机制检查字段
            checkPojo(obj, clazz, name);
        }
    }

    /**
     * 判断对象是否是jdk中定义之外的对象.
     *
     * @param clazz 对象的class
     * @return
     */
    private static boolean isPojo(final Class clazz) {
        return clazz != Date.class && clazz != Character.class && clazz != Boolean.class
                && !Number.class.isAssignableFrom(clazz) && !Iterable.class.isAssignableFrom(clazz)
                && String.class != clazz && !InputStream.class.isAssignableFrom(clazz)
                && !OutputStream.class.isAssignableFrom(clazz);
    }

}
