package com.xiafei.tools.generatesource.enums;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * <P>Description: myBatis的jdbcType和数据库type之间的对应关系枚举. </P>
 * <P>CALLED BY:   齐霞飞 </P>
 * <P>UPDATE BY:   齐霞飞 </P>
 * <P>CREATE DATE: 2017/7/14</P>
 * <P>UPDATE DATE: 2017/7/14</P>
 *
 * @author qixiafei
 * @version 1.0
 * @since java 1.7.0
 */
public enum JdbcTypeMyBatisEnum {

    ARRAY("ARRAY", Collections.EMPTY_LIST),
    BIGINT("BIGINT", new ArrayList<String>() {
        {
            add("BIGINT");
        }
    }),
    BINARY("BINARY", Collections.EMPTY_LIST),
    BIT("BIT", new ArrayList<String>() {
        {
            add("BIT");
        }
    }),
    BLOB("BLOB", new ArrayList<String>() {
        {
            add("BLOB");
        }
    }),
    BOOLEAN("BOOLEAN", Collections.EMPTY_LIST),
    CHAR("CHAR", new ArrayList<String>() {
        {
            add("CHAR");
        }
    }),
    CLOB("CLOB", new ArrayList<String>() {
        {
            add("TEXT");
            add("CLOB");
        }
    }),
    CURSOR("CURSOR", new ArrayList<String>() {
        {
            add("REAL");
        }
    }),

    DATALINK("DATALINK", Collections.EMPTY_LIST),
    DATE("DATE", new ArrayList<String>() {
        {
            add("DATE");
        }
    }),
    DATETIMEOFFSET("DATETIMEOFFSET", Collections.EMPTY_LIST),
    DECIMAL("DECIMAL", new ArrayList<String>() {
        {
            add("DECIMAL");
        }
    }),
    DISTINCT("DISTINCT", Collections.EMPTY_LIST),
    DOUBLE("DOUBLE", new ArrayList<String>() {
        {
            add("DOUBLE");
            add("NUMBER");
        }
    }),
    FLOAT("FLOAT", new ArrayList<String>() {
        {
            add("FLOAT");
        }
    }),
    INTEGER("INTEGER", new ArrayList<String>() {
        {
            add("INTEGER");
            add("INT");
        }
    }),
    JAVA_OBJECT("JAVA_OBJECT", Collections.EMPTY_LIST),
    LONGNVARCHAR("LONGNVARCHAR", Collections.EMPTY_LIST),
    LONGVARBINARY("LONGVARBINARY", Collections.EMPTY_LIST),
    LONGVARCHAR("LONGVARCHAR", new ArrayList<String>() {
        {
            add("LONG VARCHAR");
        }
    }),
    NCHAR("NCHAR", new ArrayList<String>() {
        {
            add("NCHAR");
        }
    }),
    NCLOB("NCLOB", new ArrayList<String>() {
        {
            add("NCLOB");
        }
    }),
    NULL("NULL", Collections.EMPTY_LIST),
    NUMERIC("NUMERIC", new ArrayList<String>() {
        {
            add("NUMERIC");
            add("NUMBER");
        }
    }),
    NVARCHAR("NVARCHAR", Collections.EMPTY_LIST),
    OTHER("OTHER", Collections.EMPTY_LIST),
    REAL("REAL", new ArrayList<String>() {
        {
            add("REAL");
        }
    }),
    REF("REF", Collections.EMPTY_LIST),
    ROWID("ROWID", Collections.EMPTY_LIST),
    SMALLINT("SMALLINT", new ArrayList<String>() {
        {
            add("SMALLINT");
        }
    }),
    SQLXML("SQLXML", Collections.EMPTY_LIST),
    STRUCT("STRUCT", Collections.EMPTY_LIST),
    TIME("TIME", Collections.EMPTY_LIST),
    TIMESTAMP("TIMESTAMP", new ArrayList<String>() {
        {
            add("TIMESTAMP");
            add("DATETIME");
        }
    }),
    TINYINT("TINYINT", new ArrayList<String>() {
        {
            add("TINYINT");
        }
    }),
    UNDEFINED("UNDEFINED", Collections.EMPTY_LIST),
    VARBINARY("VARBINARY", Collections.EMPTY_LIST),
    VARCHAR("VARCHAR", new ArrayList<String>() {
        {
            add("VARCHAR");
        }
    }),;

    /**
     * java类型.
     */
    public final String mybatisType;
    /**
     * java类型对应的jdbc类型.s
     */
    public final List<String> jdbcTypeList;

    JdbcTypeMyBatisEnum(final String mybatisType, final List<String> jdbcTypeList) {
        this.mybatisType = mybatisType;
        this.jdbcTypeList = jdbcTypeList;
    }

    public static JdbcTypeMyBatisEnum instance(final String jdbcType) {
        if (jdbcType == null) {
            return null;
        }
        for (JdbcTypeMyBatisEnum e : values()) {
            if (e.jdbcTypeList.contains(jdbcType)) {
                return e;
            }
        }
        return null;
    }
}
