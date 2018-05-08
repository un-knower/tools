package com.xiafei.tools.generatesource;

import com.xiafei.tools.generatesource.enums.ColumnKeyEnum;

/**
 * <P>Description: 数据库字段信息. </P>
 * <P>CALLED BY:   齐霞飞 </P>
 * <P>UPDATE BY:   齐霞飞 </P>
 * <P>CREATE DATE: 2017/7/13</P>
 * <P>UPDATE DATE: 2017/7/13</P>
 *
 * @author qixiafei
 * @version 1.0
 * @since java 1.7.0
 */
public class ColumnInfo {

    /**
     * 字段名称.
     */
    private String name;

    /**
     * 字段类型.
     */
    private String type;

    /**
     * 字段注释.
     */
    private String comment;

    /**
     * 字段约束key.
     *
     * @see ColumnKeyEnum
     */
    private String key;

    public String getName() {
        return name;
    }

    public void setName(final String pName) {
        name = pName;
    }

    public String getType() {
        return type;
    }

    public void setType(final String pType) {
        type = pType;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(final String pComment) {
        comment = pComment;
    }

    public String getKey() {
        return key;
    }

    public void setKey(final String pKey) {
        key = pKey;
    }

    @Override
    public String toString() {
        return "ColumnInfo{" +
                "name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", comment='" + comment + '\'' +
                ", key='" + key + '\'' +
                '}';
    }
}
