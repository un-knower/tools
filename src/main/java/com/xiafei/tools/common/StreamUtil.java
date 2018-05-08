package com.xiafei.tools.common;

import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;

/**
 * <P>Description: 流工具包. </P>
 * <P>CALLED BY:   齐霞飞 </P>
 * <P>UPDATE BY:   齐霞飞 </P>
 * <P>CREATE DATE: 2018/3/27</P>
 * <P>UPDATE DATE: 2018/3/27</P>
 *
 * @author qixiafei
 * @version 1.0
 * @since java 1.8.0
 */
@Slf4j
public class StreamUtil {
    private static final int BUFFER_SIZE = 1024 << 3;

    public static byte[] getBytes(final InputStream in) throws IOException {

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            int remains = 0;
            byte[] buffer = new byte[BUFFER_SIZE];
            while ((remains = in.read(buffer, 0, BUFFER_SIZE)) > 0) {
                baos.write(buffer, 0, remains);
            }
            return baos.toByteArray();
        }
    }

    /**
     * 关闭流.
     *
     * @param streams 流
     */
    public static void closeStream(final Closeable... streams) {

        if (streams != null) {
            for (Closeable stream : streams) {
                if (stream != null) {
                    try {
                        stream.close();
                    } catch (IOException e) {
                        log.warn("stream 关闭失败", e);
                    }
                }
            }

        }
    }
}
