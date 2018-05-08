package com.xiafei.tools.generatesource.template;

import com.xiafei.tools.generatesource.ColumnInfo;
import com.xiafei.tools.generatesource.GenerateSourceParam;
import com.xiafei.tools.generatesource.GenerateSourceParamItem;
import com.xiafei.tools.generatesource.enums.ColumnKeyEnum;
import com.xiafei.tools.generatesource.enums.DataBaseTypeEnum;
import com.xiafei.tools.generatesource.enums.JdbcTypeJavaTypeEnum;
import com.xiafei.tools.generatesource.enums.JdbcTypeMyBatisEnum;
import com.xiafei.tools.common.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * <P>Description:  mybatisMapper文件生成模板. </P>
 * <P>CALLED BY:   齐霞飞 </P>
 * <P>UPDATE BY:   齐霞飞 </P>
 * <P>CREATE DATE: 2017/7/14</P>
 * <P>UPDATE DATE: 2017/7/14</P>
 *
 * @author qixiafei
 * @version 1.0
 * @since java 1.7.0
 */
public final class MapperTemplate extends SourceTemplate {

    /**
     * 行最大长度，超过就换行.
     */
    private static final int LINE_LENGTH = 120;

    /**
     * 不允许实例化.
     */
    private MapperTemplate() {

    }


    /**
     * 增加输出文件内容.
     *
     * @param param       参数
     * @param item        参数明细
     * @param columnInfos 数据库表中查出的字段信息列表
     * @param content     输出文件内容.
     */
    public static void addContent(final GenerateSourceParam param, final GenerateSourceParamItem item,
                                  final List<ColumnInfo> columnInfos, final List<String> content) {
        // 增加头部信息
        addHead(param, item, content);
        ColumnInfo primaryColumn = null;
        for (ColumnInfo info : columnInfos) {
            if (item.getDataBaseType() == DataBaseTypeEnum.MYSQL
                    && ColumnKeyEnum.instance(info.getKey()) == ColumnKeyEnum.MYSQL_PRIMARY) {
                primaryColumn = info;
            }
        }
        List<ColumnInfo> columnInfosNoPrimaryColumn = new ArrayList<>(columnInfos);
        if (primaryColumn != null) {
            // ===============若主键不为空，columnInfos里面已经不包含主键信息了===========================
            columnInfosNoPrimaryColumn.remove(primaryColumn);
        }
        // resultMap的id.
        final String resultId = StringUtils.firstCharToLower(item.getClassName()) + "Result";
        // domain的包+类路径
        final String domainJavaPath = param.getDomainPackage() + "." + item.getClassName() + (
                param.getDomainSuffix() == null ? "" : param.getDomainSuffix());
        // 增加resultMap信息.
        addResultMap(resultId, domainJavaPath, primaryColumn, columnInfosNoPrimaryColumn, content);
        // 增加字段<sql>模板
        addSqlTemplate(primaryColumn, columnInfosNoPrimaryColumn, content, item.getDataBaseType());

        if (primaryColumn != null) {
            // 增加get
            addGet(resultId, primaryColumn, item.getTableName(), content, item.getDataBaseType());
            // 增加update
            addUpdate(domainJavaPath, primaryColumn, columnInfosNoPrimaryColumn, item.getTableName(), content, item.getDataBaseType());
        }
        // 增加query
        addQuery(domainJavaPath, resultId, primaryColumn, columnInfosNoPrimaryColumn, item.getTableName(), content, item.getDataBaseType());
        // 增加count
        addCount(domainJavaPath, primaryColumn, columnInfosNoPrimaryColumn, item.getTableName(), content, item.getDataBaseType());
        // 增加insert
        addInsert(domainJavaPath, primaryColumn, columnInfosNoPrimaryColumn, item.getTableName(), content, item.getDataBaseType());
        // 增加batchInsert
        addBatchInsert(primaryColumn, columnInfosNoPrimaryColumn, item.getTableName(), content, item.getDataBaseType());


        content.add("</mapper>");
    }

    /**
     * 增加头部.
     *
     * @param param   生成资源文件参数
     * @param item    参数明细
     * @param content 输出文件内容列表
     */
    private static void addHead(final GenerateSourceParam param, final GenerateSourceParamItem item,
                                final List<String> content) {
        content.add("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>");
        content.add("<!DOCTYPE mapper PUBLIC \"-//mybatis.org//DTD Mapper 3.0//EN\" \"http://mybatis.org/dtd/mybatis-3-mapper.dtd\" >");
        content.add("<mapper namespace=\"" + param.getDaoPackage() + "." + item.getClassName() + "Dao\">");
    }

    /**
     * 增加mapper的resultMap部分.
     *
     * @param resultId       拼好的resultMap的id
     * @param domainJavaPath domian类的包+类路径
     * @param primaryColumn  主键列信息
     * @param columnInfos    数据库字段列表信息
     * @param content        输出文件内容列表
     */
    private static void addResultMap(final String resultId, final String domainJavaPath, final ColumnInfo primaryColumn,
                                     final List<ColumnInfo> columnInfos, final List<String> content) {

        // resultMap第一行
        content.add(getIndent(1) + "<resultMap id=\"" + resultId + "\" type=\"" + domainJavaPath + "\">");
        // 判断是否存在主键列
        if (primaryColumn != null) {
            JdbcTypeMyBatisEnum myBatisJdbcTypeEnum = JdbcTypeMyBatisEnum.instance(primaryColumn.getType());
            if (myBatisJdbcTypeEnum == null) {
                throw new RuntimeException("无法将jdbc类型转换成mybatis的jdbcType");
            }
            content.add(getIndent(2) + "<id column=\"" + primaryColumn.getName() + "\" property=\""
                    + StringUtils.underLineToHump(primaryColumn.getName().toLowerCase(), false) + "\" jdbcType=\""
                    + myBatisJdbcTypeEnum.mybatisType + "\"/>");
        }

        for (ColumnInfo columnInfo : columnInfos) {
            JdbcTypeMyBatisEnum myBatisJdbcTypeEnum = JdbcTypeMyBatisEnum.instance(columnInfo.getType());
            if (myBatisJdbcTypeEnum == null) {
                throw new RuntimeException("无法将jdbc类型转换成mybatis的jdbcType");
            }
            content.add(getIndent(2) + "<result column=\"" + columnInfo.getName() + "\" property=\""
                    + StringUtils.underLineToHump(columnInfo.getName().toLowerCase(), false) + "\" jdbcType=\""
                    + myBatisJdbcTypeEnum.mybatisType + "\"/>");
        }
        content.add(getIndent(1) + "</resultMap>");
    }

    /**
     * 增加mapper的Sql模板
     *
     * @param primaryColumn 主键字段信息
     * @param columnInfos   数据库字段信息
     * @param content       输出文件内容列表
     * @param dataBaseType  数据库类型
     */
    private static void addSqlTemplate(ColumnInfo primaryColumn, List<ColumnInfo> columnInfos, List<String> content, final DataBaseTypeEnum dataBaseType) {
        // 先增加全字段模板
        if (primaryColumn == null) {
            content.add("");
            content.add("");
            content.add(getIndent(1) + "<!-- 基础字段 -->");
            content.add(getIndent(1) + "<sql id=\"Base_Columns\">");
            cycleAddColumnName(getIndent(2), columnInfos, content, dataBaseType);
            content.add(getIndent(1) + "</sql>");

            // 增加insert模板，不包含主键
            content.add("");
            content.add(getIndent(1) + "<!-- Insert使用字段 -->");
            content.add(getIndent(1) + "<sql id=\"Columns_For_Insert\">");
            // 增加字段信息，不包含主键
            cycleAddColumnName(getIndent(2), columnInfos, content, dataBaseType);
            content.add(getIndent(1) + "</sql>");

        } else {
            content.add("");
            content.add(getIndent(1) + "<!-- 基础字段 -->");
            content.add(getIndent(1) + "<sql id=\"Base_Columns\">");
            content.add(getIndent(2) + "`" + primaryColumn.getName() + "`,");
            content.add(getIndent(2) + "<include refid=\"Columns_For_Insert\"/>");
            content.add(getIndent(1) + "</sql>");

            // 增加insert模板，不包含主键
            content.add("");
            content.add(getIndent(1) + "<!-- Insert使用字段 -->");
            content.add(getIndent(1) + "<sql id=\"Columns_For_Insert\">");
            // 增加字段信息，不包含主键
            cycleAddColumnName(getIndent(2), columnInfos, content, dataBaseType);
            content.add(getIndent(1) + "</sql>");
        }

    }

    /**
     * 增加get方法.
     *
     * @param resultId      resultMap的id
     * @param primaryColumn 主键字段信息
     * @param tableName     数据库表名
     * @param content       输出文件内容
     * @param dataBaseType  数据库类型
     */
    private static void addGet(final String resultId, final ColumnInfo primaryColumn, final String tableName,
                               final List<String> content, final DataBaseTypeEnum dataBaseType) {
        content.add("");
        content.add(getIndent(1) + "<!-- 根据主键查询一行数据 -->");
        content.add(getIndent(1) + "<select id=\"get\" parameterType=\"java.lang."
                + JdbcTypeJavaTypeEnum.instance(primaryColumn.getType()).javaType
                + "\" resultMap=\"" + resultId + "\">");
        content.add(getIndent(2) + "SELECT");
        content.add(getIndent(2) + "<include refid=\"Base_Columns\"/>");
        content.add(getIndent(2) + "FROM " + tableName);
        if (DataBaseTypeEnum.MYSQL == dataBaseType) {
            content.add(getIndent(2) + "WHERE `" + primaryColumn.getName() + "` = #{"
                    + StringUtils.underLineToHump(primaryColumn.getName().toLowerCase(), false)
                    + ",jdbcType=" + JdbcTypeMyBatisEnum.instance(primaryColumn.getType()).mybatisType + "}");
        } else if (DataBaseTypeEnum.ORACLE == dataBaseType) {
            content.add(getIndent(2) + "WHERE \"" + primaryColumn.getName() + "\" = #{"
                    + StringUtils.underLineToHump(primaryColumn.getName().toLowerCase(), false)
                    + ",jdbcType=" + JdbcTypeMyBatisEnum.instance(primaryColumn.getType()).mybatisType + "}");
        } else {
            throw new IllegalArgumentException("DataBaseTypeEnum枚举值改变了这里没修改");
        }
        content.add(getIndent(1) + "</select>");
    }

    /**
     * 增加根据主键更新方法sql.
     */
    private static void addUpdate(final String domainJavaPath, final ColumnInfo primaryColumn, final List<ColumnInfo> columnInfos, final String tableName, final List<String> content, final DataBaseTypeEnum dataBaseType) {
        content.add("");
        content.add(getIndent(1) + "<!-- 根据主键更新非空字段 -->");
        content.add(getIndent(1) + "<update id=\"update\" parameterType=\"" + domainJavaPath + "\">");
        content.add(getIndent(2) + "<if test=\" "
                + StringUtils.underLineToHump(primaryColumn.getName().toLowerCase(), false) + " != null \" >");
        content.add(getIndent(3) + "UPDATE " + tableName);
        content.add(getIndent(3) + "<set>");
        cycleAddIfTest(getIndent(4), columnInfos, ",", content, dataBaseType);
        content.add(getIndent(3) + "</set>");
        if (DataBaseTypeEnum.MYSQL == dataBaseType) {
            content.add(getIndent(3) + "WHERE `" + primaryColumn.getName() + "` = #{"
                    + StringUtils.underLineToHump(primaryColumn.getName().toLowerCase(), false)
                    + ",jdbcType=" + JdbcTypeMyBatisEnum.instance(primaryColumn.getType()).mybatisType + "}");
        } else if (DataBaseTypeEnum.ORACLE == dataBaseType) {
            content.add(getIndent(3) + "WHERE \"" + primaryColumn.getName() + "\" = #{"
                    + StringUtils.underLineToHump(primaryColumn.getName().toLowerCase(), false)
                    + ",jdbcType=" + JdbcTypeMyBatisEnum.instance(primaryColumn.getType()).mybatisType + "}");
        } else {
            throw new IllegalArgumentException("DataBaseTypeEnum枚举值改变了这里没修改");
        }
        content.add(getIndent(2) + "</if>");
        content.add(getIndent(1) + "</update>");
    }

    /**
     * 增加根据条件查询列表sql.
     *
     * @param domainJavaPath 包+PO类名字符串
     * @param resultId       resultMap的id
     * @param primaryColumn  主键列
     * @param columnInfos    数据库字段信息（不包含主键）
     * @param tableName      数据库表名
     * @param content        输出文件内容
     * @param dataBaseType   数据库类型
     */
    private static void addQuery(final String domainJavaPath, final String resultId, final ColumnInfo primaryColumn, final List<ColumnInfo> columnInfos, final String tableName, final List<String> content, final DataBaseTypeEnum dataBaseType) {
        content.add("");
        content.add(getIndent(1) + "<!-- 根据条件查询列表 -->");
        content.add(getIndent(1) + "<select id=\"query\" parameterType=\"" + domainJavaPath + "\" resultMap=\"" + resultId + "\">");
        content.add(getIndent(2) + "SELECT");
        content.add(getIndent(2) + "<include refid=\"Base_Columns\" />");
        content.add(getIndent(2) + "FROM " + tableName);
        content.add(getIndent(2) + "<where>");

        List<ColumnInfo> tempColumnInfos = new ArrayList<>(columnInfos);
        if (primaryColumn != null) {
            tempColumnInfos.add(primaryColumn);
        }
        cycleAddIfTest(getIndent(3), tempColumnInfos, "AND", content, dataBaseType);

        content.add(getIndent(2) + "</where>");
        content.add(getIndent(1) + "</select>");

    }

    /**
     * 增加统计数量sql.
     *
     * @param domainJavaPath 包+PO类名字符串
     * @param primaryColumn  主键列
     * @param columnInfos    数据库字段信息（不包含主键）
     * @param tableName      数据库表名
     * @param content        输出文件内容
     * @param dataBaseType
     */
    private static void addCount(final String domainJavaPath, final ColumnInfo primaryColumn, final List<ColumnInfo> columnInfos, final String tableName, final List<String> content, final DataBaseTypeEnum dataBaseType) {
        content.add("");
        content.add(getIndent(1) + "<!-- 统计数量 -->");
        content.add(getIndent(1) + "<select id=\"count\" parameterType=\"" + domainJavaPath + "\" resultType=\"java.lang.Integer\" >");
        content.add(getIndent(2) + "SELECT count(*)");
        content.add(getIndent(2) + "FROM " + tableName);
        content.add(getIndent(2) + "<where>");

        List<ColumnInfo> tempColumnInfos = new ArrayList<>(columnInfos);
        if (primaryColumn != null) {
            tempColumnInfos.add(primaryColumn);
        }
        cycleAddIfTest(getIndent(3), tempColumnInfos, "AND", content, dataBaseType);

        content.add(getIndent(2) + "</where>");
        content.add(getIndent(1) + "</select>");

    }

    /**
     * 增加插入sql.
     *
     * @param domainJavaPath 包+PO类名字符串
     * @param primaryColumn  主键列
     * @param columnInfos    数据库字段信息（不包含主键）
     * @param tableName      数据库表名
     * @param content        输出文件内容
     * @param dataBaseType
     */
    private static void addInsert(final String domainJavaPath, final ColumnInfo primaryColumn, final List<ColumnInfo> columnInfos, final String tableName, final List<String> content, final DataBaseTypeEnum dataBaseType) {
        content.add("");
        content.add(getIndent(1) + "<!-- 插入数据 -->");

        String headInfo = "<insert id=\"insert\" parameterType=\"" + domainJavaPath + "\" ";
        if (primaryColumn != null) {
            headInfo += "useGeneratedKeys=\"true\" keyProperty=\"" + StringUtils.underLineToHump(primaryColumn.getName().toLowerCase(), false) + "\"";
        }
        headInfo += ">";
        content.add(getIndent(1) + headInfo);

        content.add(getIndent(2) + "INSERT INTO");
        content.add(getIndent(2) + tableName);
        content.add(getIndent(2) + "(");
        content.add(getIndent(3) + "<include refid=\"Columns_For_Insert\"/>");
        content.add(getIndent(2) + ")");
        content.add(getIndent(2) + "VALUES");
        content.add(getIndent(2) + "(");
        cycleAddPropertyValue(getIndent(3), "", columnInfos, content);
        content.add(getIndent(2) + ")");
        content.add(getIndent(1) + "</insert>");
    }

    /**
     * 增加批量插入sql.
     *
     * @param primaryColumn 主键列
     * @param columnInfos   数据库字段信息（不包含主键）
     * @param tableName     数据库表名
     * @param content       输出文件内容
     * @param dataBaseType
     */
    private static void addBatchInsert(final ColumnInfo primaryColumn, final List<ColumnInfo> columnInfos, final String tableName, final List<String> content, final DataBaseTypeEnum dataBaseType) {
        content.add("");
        content.add(getIndent(1) + "<!-- 批量插入 -->");
        String headInfo = "<insert id=\"batchInsert\" parameterType=\"java.util.List\" ";
        if (primaryColumn != null) {
            headInfo += "useGeneratedKeys=\"true\" keyProperty=\"" + StringUtils.underLineToHump(primaryColumn.getName().toLowerCase(), false) + "\" ";
        }
        headInfo += ">";
        content.add(getIndent(1) + headInfo);
        content.add(getIndent(2) + "INSERT INTO");
        content.add(getIndent(2) + tableName);
        content.add(getIndent(2) + "(");
        content.add(getIndent(3) + "<include refid=\"Columns_For_Insert\"/>");
        content.add(getIndent(2) + ")");
        content.add(getIndent(2) + "VALUES");
        content.add(getIndent(2) + "<foreach collection=\"list\" item=\"item\" separator=\",\" >");
        content.add(getIndent(3) + "(");
        cycleAddPropertyValue(getIndent(4), "item.", columnInfos, content);
        content.add(getIndent(3) + ")");
        content.add(getIndent(2) + "</foreach>");

        content.add(getIndent(1) + "</insert>");
    }

    /**
     * 循环增加字段信息
     *
     * @param indent       缩进
     * @param columnInfos  数据库字段信息列表
     * @param content      输出文件内容
     * @param dataBaseType 数据库类型
     */
    private static void cycleAddColumnName(final String indent, final List<ColumnInfo> columnInfos, final List<String> content, final DataBaseTypeEnum dataBaseType) {
        // 拼接逗号分隔字段信息
        final StringBuilder sb = new StringBuilder();
        final int contentLength = LINE_LENGTH - indent.length();
        for (ColumnInfo columnInfo : columnInfos) {
            final String columnName = columnInfo.getName();
            if ((sb.length() + columnName.length()) / contentLength > 0) {
                // 如果新增该列描述后超长了，那么将新增前的内容算作一行，接下来另起一行
                content.add(indent + sb.toString());

                sb.delete(0, sb.length());
            }
            if (DataBaseTypeEnum.MYSQL == dataBaseType) {
                sb.append("`").append(columnName).append("`, ");
            } else if (DataBaseTypeEnum.ORACLE == dataBaseType) {
                sb.append("\"").append(columnName).append("\", ");
            } else {
                throw new IllegalArgumentException("DataBaseTypeEnum新增枚举值这里没修改");
            }
        }
        // 删掉最后一个逗号和空格
        sb.deleteCharAt(sb.length() - 1).deleteCharAt(sb.length() - 1);
        content.add(indent + sb.toString());
    }

    /**
     * 循环增加<if test=""></>.
     *
     * @param indent       缩进
     * @param columnInfos  数据库字段信息列表
     * @param s            分隔符
     * @param content      输出文件内容
     * @param dataBaseType 数据库类型
     */
    private static void cycleAddIfTest(final String indent, final List<ColumnInfo> columnInfos, final String s, final List<String> content, final DataBaseTypeEnum dataBaseType) {
        for (ColumnInfo columnInfo : columnInfos) {
            final String propertyName = StringUtils.underLineToHump(columnInfo.getName().toLowerCase(), false);
            final JdbcTypeJavaTypeEnum javaType = JdbcTypeJavaTypeEnum.instance(columnInfo.getType());
            final String mybatisType = JdbcTypeMyBatisEnum.instance(columnInfo.getType()).mybatisType;
            if (JdbcTypeJavaTypeEnum.STRING == javaType) {
                content.add(indent + "<if test=\" " + propertyName + " != null and " + propertyName + " != '' \" >");
            } else {
                content.add(indent + "<if test=\" " + propertyName + " != null \" >");
            }
            if ("AND".equalsIgnoreCase(s)) {
                if (DataBaseTypeEnum.MYSQL == dataBaseType) {
                    content.add(indent + getIndent(1) + "AND `" + columnInfo.getName() + "` = #{" + propertyName + ",jdbcType=" + mybatisType + "}");

                } else if (DataBaseTypeEnum.ORACLE == dataBaseType) {
                    content.add(indent + getIndent(1) + "AND \"" + columnInfo.getName() + "\" = #{" + propertyName + ",jdbcType=" + mybatisType + "}");
                } else {
                    throw new IllegalArgumentException("DataBaseTypeEnum枚举值改变了这里没修改");
                }
            } else if (",".equalsIgnoreCase(s)) {
                if (DataBaseTypeEnum.MYSQL == dataBaseType) {
                    content.add(indent + getIndent(1) + "`" + columnInfo.getName() + "` = #{" + propertyName + ",jdbcType=" + mybatisType + "},");

                } else if (DataBaseTypeEnum.ORACLE == dataBaseType) {
                    content.add(indent + getIndent(1) + "\"" + columnInfo.getName() + "\" = #{" + propertyName + ",jdbcType=" + mybatisType + "},");
                } else {
                    throw new IllegalArgumentException("DataBaseTypeEnum枚举值改变了这里没修改");
                }
            }
            content.add(indent + "</if>");
        }
    }

    /**
     * 循环增加java字段值.
     *
     * @param indent      缩进
     * @param prefix      字段值的前缀
     * @param columnInfos 数据库字段信息列表
     * @param content     输出文件内容
     */
    private static void cycleAddPropertyValue(final String indent, final String prefix, final List<ColumnInfo> columnInfos, final List<String> content) {
        // 拼接逗号分隔java字段值
        final StringBuilder sb = new StringBuilder();
        // 实际内容长度限制.
        final int contentLength = LINE_LENGTH - indent.length();
        for (ColumnInfo columnInfo : columnInfos) {
            final String propertyName = StringUtils.underLineToHump(columnInfo.getName().toLowerCase(), false);
            final String propertyValue = "#{" + prefix + propertyName + ",jdbcType=" + JdbcTypeMyBatisEnum.instance(columnInfo.getType()) + "}";

            if ((sb.length() + propertyValue.length()) / contentLength > 0) {
                // 如果新增该列后超长了，那么将新增前的内容算作一行，接下来另起一行
                content.add(indent + sb.toString());

                sb.delete(0, sb.length());
            }

            sb.append(propertyValue).append(",");
        }
        // 删掉最后一个逗号
        sb.deleteCharAt(sb.length() - 1);
        content.add(indent + sb.toString());
    }

}
