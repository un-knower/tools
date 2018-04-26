package com.xiafei.tools.common;

import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * <P>Description: 克隆工具. </P>
 * <P>CALLED BY:   齐霞飞 </P>
 * <P>UPDATE BY:   齐霞飞 </P>
 * <P>CREATE DATE: 2018/4/9</P>
 * <P>UPDATE DATE: 2018/4/9</P>
 *
 * @author qixiafei
 * @version 1.0
 * @since java 1.8.0
 */
@Slf4j
public class CloneUtil {

    /**
     * 对象深度克隆---使用序列化进行深拷贝，效率高于apache.lang包下的SerializationUtils.
     *
     * @param obj 要克隆的对象
     * @return 注意：
     * 使用序列化的方式来实现对象的深拷贝，但是前提是，对象必须是实现了 Serializable接口才可以，Map本身没有实现
     * Serializable 这个接口，所以这种方式不能序列化Map，也就是不能深拷贝Map。但是HashMap是可以的，因为它实现了Serializable。
     */
    public static <T extends Serializable> T clone(final T obj) {
        T clonedObj = null;
        ByteArrayOutputStream baos = null;
        ObjectOutputStream oos = null;
        ByteArrayInputStream bais = null;
        ObjectInputStream ois = null;
        try {
            baos = new ByteArrayOutputStream();
            oos = new ObjectOutputStream(baos);
            oos.writeObject(obj);

            bais = new ByteArrayInputStream(baos.toByteArray());
            ois = new ObjectInputStream(bais);
            clonedObj = (T) ois.readObject();

        } catch (IOException e) {
            log.error("对象克隆流Io异常", e);
        } catch (ClassNotFoundException e) {
            log.error("对象克隆找不到对象类异常", e);
        } finally {
            StreamUtil.closeStream(ois, bais, oos, baos);
        }
        return clonedObj;
    }

    /**
     * 工具类不需要实例化.
     */
    private CloneUtil() {

    }
}
