package com.xiafei.tools.common;

import java.math.BigDecimal;
import java.util.Date;

/**
 * <P>Description: 扩展apache的String工具类. </P>
 * <P>CALLED BY:   齐霞飞 </P>
 * <P>UPDATE BY:   齐霞飞 </P>
 * <P>CREATE DATE: ${date}</P>
 * <P>UPDATE DATE: ${date}</P>
 *
 * @author 齐霞飞
 * @version 1.0
 * @since java 1.7.0
 */
public final class StringUtils extends org.apache.commons.lang.StringUtils {

    /**
     * 下划线.
     */
    private static final String UNDER_LINE = "_";

    /**
     * 不允许实例化.
     */
    private StringUtils() {

    }

    /**
     * 将不足位数的整数左侧填充0直到指定的位数，效率优于String.format().
     *
     * @param value 十进制值
     * @param digit 位数
     * @return 左侧填充0到指定位数后的字符串
     */
    public static String fillZeroLeftToDigit(final int value, final int digit) {
        // 绝对值的返回结果
        final String absResult;

        final String absValueStr = String.valueOf(Math.abs(value));
        // value位数
        final int oriLength = absValueStr.length();

        if (oriLength >= digit) {
            absResult = absValueStr;
        } else {
            final StringBuilder absSb = new StringBuilder();
            for (int i = 0, len = digit - oriLength; i < len; i++) {
                absSb.append("0");
            }
            absSb.append(absValueStr);
            absResult = absSb.toString();
        }
        return value < 0 ? "-".concat(absResult) : absResult;

    }

    /**
     * 首字母大写.
     *
     * @param oriStr 原字符串
     * @return 首字母大写后的字符串
     */
    public static String firstCharToUpper(final String oriStr) {
        final StringBuilder sb = new StringBuilder(oriStr);
        sb.setCharAt(0, Character.toUpperCase(sb.charAt(0)));
        return sb.toString();
    }

    /**
     * 首字母小写.
     *
     * @param oriStr 原字符串
     * @return 首字母大写后的字符串
     */
    public static String firstCharToLower(final String oriStr) {
        final StringBuilder sb = new StringBuilder(oriStr);
        sb.setCharAt(0, Character.toLowerCase(sb.charAt(0)));
        return sb.toString();
    }

    /**
     * 下划线转驼峰.
     *
     * @param source       原串
     * @param firstToUpper 首字母是否大写
     * @return 将下划线模式转驼峰后的新串
     */
    public static String underLineToHump(final String source, final boolean firstToUpper) {
        if (isBlank(source)) {
            return source;
        }
        final String[] parts = source.split(UNDER_LINE);
        final StringBuilder sb = new StringBuilder();
        for (int i = 0, len = parts.length; i < len; i++) {
            if (i == 0 && !firstToUpper) {
                sb.append(parts[i]);
            } else {
                sb.append(firstCharToUpper(parts[i]));
            }
        }
        return sb.toString();
    }

    /**
     * 驼峰转下划线，原大写字母变小写字母.
     *
     * @param source          原串
     * @param firstUpperCheck 首字母若是大写字母，是否需要转下划线
     * @param ignoreStr       忽略大写字母检查的字符串
     * @return 将驼峰转下划线格式后的新串
     */
    public static String humpToUnderLine(final String source, final boolean firstUpperCheck,
                                         final String ignoreStr) {
        if (isBlank(source)) {
            return source;
        }
        // 忽略大写字母检查的字符串所在原字符串起始位置
        final Queue<Integer> ignorePositions = new Queue<>(2);
        // 若忽略字符串不为空，计算所忽略字符串在原字符串中出现的所有位置
        if (isNotBlank(ignoreStr)) {
            // 当前循环内忽略字符串所在位置
            int ignorePosition;
            for (int i = 0, len = source.length(); i < len; ) {

                ignorePosition = source.substring(i).indexOf(ignoreStr);

                if (ignorePosition > -1) {

                    ignorePositions.enqueue(i + ignorePosition);
                    i = ignorePosition + ignoreStr.length();

                } else {
                    break;
                }
            }
        }
        // 转换结果
        final StringBuilder sb = new StringBuilder();
        final char[] charArray = source.toCharArray();
        // 下一个忽略点的位置
        int nextIgnorePosition = -1;
        if (!ignorePositions.isEmpty()) {
            nextIgnorePosition = ignorePositions.dequeue();
        }
        for (int i = 0, len = source.length(); i < len; ) {

            if (nextIgnorePosition == i) {
                i += ignoreStr.length();
                nextIgnorePosition = ignorePositions.isEmpty() ? -1 : ignorePositions.dequeue();
                sb.append(ignoreStr);
                continue;
            }

            final char codePoint = charArray[i];
            if (Character.isUpperCase(codePoint)) {
                if (i > 0) {
                    sb.append(UNDER_LINE).append(Character.toLowerCase(codePoint));
                } else if (firstUpperCheck) {
                    sb.append(UNDER_LINE).append(Character.toLowerCase(codePoint));
                } else {
                    sb.append(codePoint);
                }
            } else {
                sb.append(codePoint);
            }
            i++;
        }
        return sb.toString();
    }

    /**
     * 从类似URL参数字符串中获取某一个属性的值.
     *
     * @param source       字符串
     * @param propertyName 属性名
     * @param separator    分隔符
     * @return 属性值
     */
    public static String getPropertyValueFromSimilarURL(final String source,
                                                        final String propertyName, final String separator) {
        if (isBlank(source) || isBlank(propertyName)) {
            return null;
        }
        // 先拿到属性所在字符串起始位置，拼上=号以防止属性名部分相同的情况发生
        final int propertyIndex = source.indexOf(propertyName.concat("="));
        if (propertyIndex > -1) {
            // 属性的值拼接builder
            final StringBuilder sb = new StringBuilder();
            // 属性值的起始位置是属性名起始位置+属性名长度+1越过等于号
            for (int i = propertyIndex + propertyName.length() + 1, len = source.length();
                 i < len; i++) {

                final char c = source.charAt(i);
                // 如果已经遍历到分隔符，退出循环
                if (source.substring(i).indexOf(separator) == 0) {
                    break;
                } else {
                    sb.append(c);
                }
            }

            return sb.toString();

        } else {
            return null;
        }
    }

    /**
     * 查找正则在字符串中第一次出现的位置，若找不到返回-1.
     *
     * @param source      要分析的字符串
     * @param regex       正则表达式
     * @param regexLength 正则表达式匹配的总长度
     * @return 字符串中符合正则表达式的首字母位置
     */
    public static int indexOfRegex(final String source, final String regex, final int regexLength) {

        for (int i = 0, len = source.length() - regexLength + 1; i < len; i++) {
            if (source.substring(i, i + regexLength).matches(regex)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * 按GBK编码截取字节长度字符串.
     *
     * @param source     被截取字符串
     * @param byteLength 要截取的字节长度
     * @return 截取出来的字符串
     */
    public static String cutGbkByteLength(final String source, final int byteLength) {
        final StringBuilder sb = new StringBuilder();
        final char[] arr = source.toCharArray();
        int byteCount = 0;
        for (Character c : arr) {
            if (byteCount == byteLength) {
                break;
            }
            if (isChinese(c)) {
                byteCount = byteCount + 2;
            } else {
                byteCount++;
            }
            sb.append(c);
        }
        return sb.toString();
    }

    /**
     * 只保留数字.
     *
     * @param source 原字符串
     * @return 去掉非数字字符后的新串
     */
    public static String digitOnly(final String source) {
        String withPoint = numberOnly(source);
        return withPoint == null ? null : withPoint.replace(".", "").replace("-", "");
    }

    /**
     * 只保留数字和小数点.
     *
     * @param source 原字符串
     * @return 去掉非数字字符后的新串
     */
    public static String numberOnly(final String source) {
        if (isBlank(source)) {
            return source;
        }
        final StringBuilder sb = new StringBuilder();
        for (char c : source.toCharArray()) {
            if (Character.isDigit(c) || c == '.' || c == '-') {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    /**
     * 只保留数字，转整型.
     *
     * @param source 原字符串
     * @return 去掉非数字字符后的整型
     */
    public static Integer parseInt(final String source) {
        if (isBlank(source)) {
            return null;
        }
        return Integer.parseInt(digitOnly(source));
    }

    /**
     * 根据Unicode编码完美的判断中文汉字和符号.
     *
     * @param c 准备判断的字符
     * @return true-是汉字，false-不是
     */
    private static boolean isChinese(final char c) {
        final Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B
                || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
                || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS
                || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION) {
            return true;
        }
        return false;
    }

    /**
     * 将二进制转换成16进制的字符串.
     *
     * @param buf 字节流
     * @return 16进制的字符串
     */
    public static String bytes2HexStr(final byte buf[]) {
        if (buf == null) {
            return null;
        }
        final StringBuilder sb = new StringBuilder();
        for (byte aBuf : buf) {
            String hex = Integer.toHexString(aBuf & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            sb.append(hex.toUpperCase());
        }
        return sb.toString();
    }

    /**
     * 将16进制字符串转换为字节流.
     *
     * @param hexStr 16进制字符串
     * @return 字节流
     */
    public static byte[] hexStr2Bytes(final String hexStr) {
        if (hexStr == null || hexStr.length() == 0)
            return null;
        byte[] result = new byte[hexStr.length() >>> 1];
        for (int i = 0; i < hexStr.length() / 2; i++) {
            int high = Integer.parseInt(hexStr.substring(i * 2, i * 2 + 1), 16);
            int low = Integer.parseInt(hexStr.substring(i * 2 + 1, i * 2 + 2), 16);
            result[i] = (byte) (high * 16 + low);
        }
        return result;
    }

    /**
     * 将字符串转换成指定格式的对象.
     *
     * @param source 字符串
     * @param clazz  要转换成的目标格式
     * @return 目标格式对象
     */
    public static <T> T parseStringToType(final String source, final Class<T> clazz) {
        final Object result;
        try {
            if (clazz == null || source == null || source.equals("null")) {
                result = null;
            } else if (clazz == Date.class) {
                result = DateUtils.parse(source);
            } else if (clazz == BigDecimal.class) {
                result = new BigDecimal(source);
            } else if (clazz == Character.class) {
                result = source.charAt(0);
            } else if (clazz == Byte.class) {
                result = Byte.parseByte(source);
            } else if (clazz == Short.class) {
                result = Short.parseShort(source);
            } else if (clazz == Integer.class) {
                result = Integer.parseInt(source);
            } else if (clazz == Long.class) {
                result = Long.parseLong(source);
            } else if (clazz == Float.class) {
                result = Float.parseFloat(source);
            } else if (clazz == Double.class) {
                result = Double.parseDouble(source);
            } else if (clazz == Boolean.class) {
                result = Boolean.parseBoolean(source);
            } else {

                result = source;
            }
        } catch (NumberFormatException e) {
            System.out.println("格式化异常，字符=" + source);
            throw e;
        }

        return (T) result;
    }
}
