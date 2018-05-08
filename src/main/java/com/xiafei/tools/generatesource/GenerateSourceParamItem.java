package com.xiafei.tools.generatesource;

import com.xiafei.tools.generatesource.enums.DataBaseTypeEnum;

/**
 * <P>Description: 生成资源文件参数明细项. </P>
 * <P>CALLED BY:   齐霞飞 </P>
 * <P>UPDATE BY:   齐霞飞 </P>
 * <P>CREATE DATE: 2017/7/13</P>
 * <P>UPDATE DATE: 2017/7/13</P>
 *
 * @author qixiafei
 * @version 1.0
 * @since java 1.7.0
 */
public class GenerateSourceParamItem {

    /**
     * 数据库类型.
     *
     * @see DataBaseTypeEnum
     */
    private final DataBaseTypeEnum dataBaseType;

    /**
     * 数据库jdbc连接地址.
     */
    private final String url;

    /**
     * 数据库用户名.
     */
    private final String user;

    /**
     * 数据库密码.
     */
    private final String password;

    /**
     * 要生成的表名.
     */
    private final String tableName;

    /**
     * 表所在schema，防止不同的schema中有重名的表.
     */
    private final String tableSchema;

    /**
     * 对应数据库表的java类名字，首字母大写，不需要带PO、dao、mapper.
     */
    private final String className;

    /**
     * 类描述.
     */
    private final String classDescription;

    public GenerateSourceParamItem(final DataBaseTypeEnum dataBaseType,
                                   final String jdbcUrl,
                                   final String user,
                                   final String password,
                                   final String tableName,
                                   final String tableSchema,
                                   final String className,
                                   final String classDescription) {
        this.dataBaseType = dataBaseType;
        this.url = jdbcUrl;
        this.user = user;
        this.password = password;
        this.tableName = tableName;
        this.tableSchema = tableSchema;
        this.className = className;
        this.classDescription = classDescription;
    }


    public DataBaseTypeEnum getDataBaseType() {
        return dataBaseType;
    }


    public String getTableName() {
        return tableName;
    }


    public String getTableSchema() {
        return tableSchema;
    }


    public String getUser() {
        return user;
    }


    public String getPassword() {
        return password;
    }


    public String getUrl() {

        return url;
    }

    public String getClassName() {
        return className;
    }

    public String getClassDescription() {
        return classDescription;
    }


    @Override
    public String toString() {
        return "GenerateSourceParamItem{" +
                "dataBaseType=" + dataBaseType +
                ", url='" + url + '\'' +
                ", user='" + user + '\'' +
                ", password='" + password + '\'' +
                ", tableName='" + tableName + '\'' +
                ", tableSchema='" + tableSchema + '\'' +
                ", className='" + className + '\'' +
                ", classDescription='" + classDescription + '\'' +
                '}';
    }
}
