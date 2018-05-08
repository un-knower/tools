package com.xiafei.tools.generatesource.template;

import com.xiafei.tools.generatesource.ColumnInfo;
import com.xiafei.tools.generatesource.GenerateSourceParam;
import com.xiafei.tools.generatesource.GenerateSourceParamItem;
import com.xiafei.tools.generatesource.enums.ColumnKeyEnum;
import com.xiafei.tools.generatesource.enums.DataBaseTypeEnum;
import com.xiafei.tools.generatesource.enums.JdbcTypeJavaTypeEnum;
import com.xiafei.tools.common.StringUtils;

import java.util.List;

/**
 * <P>Description: 数据访问层接口Dao文件生成模板.  </P>
 * <P>CALLED BY:   齐霞飞 </P>
 * <P>UPDATE BY:   齐霞飞 </P>
 * <P>CREATE DATE: 2017/7/18</P>
 * <P>UPDATE DATE: 2017/7/18</P>
 *
 * @author qixiafei
 * @version 1.0
 * @since java 1.7.0
 */
public final class DaoTemplate extends SourceTemplate {

    /**
     * 不允许实例化
     */
    private DaoTemplate() {

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

        final String domainClassName = item.getClassName() + param.getDomainSuffix();

        content.add("package " + param.getDaoPackage() + ";");
        content.add("");
        content.add("import java.util.List;");
        content.add("import " + param.getDomainPackage() + "." + domainClassName + ";");
        // 增加类注释
        addClassComments(param, item, "数据访问层接口", content);
        // 类声明
        content.add("public interface " + item.getClassName() + "Dao {");
        ColumnInfo primaryColumn = null;
        for (ColumnInfo info : columnInfos) {
            if (item.getDataBaseType() == DataBaseTypeEnum.MYSQL
                    && ColumnKeyEnum.instance(info.getKey()) == ColumnKeyEnum.MYSQL_PRIMARY) {
                primaryColumn = info;
            }
        }
        if (primaryColumn != null) {
            // 增加get方法声明
            addGetMethod(primaryColumn, domainClassName, content);
            // 增加update方法声明
            addUpdateMethod(domainClassName, content);
        }
        // 增加query方法声明
        addQueryMethod(domainClassName, content);
        // 增加count方法声明
        addCountMethod(domainClassName, content);
        // 增加insert方法声明
        addInsertMethod(domainClassName, content);
        // 增加batchInsert方法声明
        addBatchInsertMethod(domainClassName, content);
        content.add("}");
    }

    /**
     * 增加get方法.
     *
     * @param primaryColumn   主键列信息
     * @param domainClassName domain类名
     * @param content         输出文件内容
     */
    private static void addGetMethod(final ColumnInfo primaryColumn, final String domainClassName, final List<String> content) {
        content.add("");
        content.add(getIndent(1) + "/**");
        content.add(getIndent(1) + " * 根据主键查询.");
        content.add(getIndent(1) + " * ");

        final String primaryKeyName = StringUtils.underLineToHump(primaryColumn.getName(), false);
        content.add(getIndent(1) + " * @param " + primaryKeyName + " 主键值");
        content.add(getIndent(1) + " * @return 根据主键查询到的对象");
        content.add(getIndent(1) + " */");
        content.add(getIndent(1) + domainClassName + " get("
                + JdbcTypeJavaTypeEnum.instance(primaryColumn.getType()).javaType
                + " " + primaryKeyName + ");");
    }

    /**
     * 增加update方法.
     *
     * @param domainClassName domain类名
     * @param content         输出文件内容
     */
    private static void addUpdateMethod(final String domainClassName, final List<String> content) {
        content.add("");
        content.add(getIndent(1) + "/**");
        content.add(getIndent(1) + " * 根据主键更新.");
        content.add(getIndent(1) + " * ");
        content.add(getIndent(1) + " * @param " + StringUtils.firstCharToLower(domainClassName) + " 要更新的对象");
        content.add(getIndent(1) + " * @return 更新where条件影响条数");
        content.add(getIndent(1) + " */");
        content.add(getIndent(1) + "int update(" + domainClassName + " "
                + StringUtils.firstCharToLower(domainClassName) + ");");
    }

    /**
     * 增加query方法，根据查询条件查询列表.
     *
     * @param domainClassName domain类名
     * @param content         输出文件内容
     */
    private static void addQueryMethod(final String domainClassName, final List<String> content) {
        content.add("");
        content.add(getIndent(1) + "/**");
        content.add(getIndent(1) + " * 根据查询条件查询列表.");
        content.add(getIndent(1) + " * ");
        content.add(getIndent(1) + " * @param condition 查询条件");
        content.add(getIndent(1) + " * @return 查询出来的对象列表");
        content.add(getIndent(1) + " */");
        content.add(getIndent(1) + "List<" + domainClassName + ">" + " query(" + domainClassName + " condition);");
    }

    /**
     * 增加count方法，统计数量.
     *
     * @param domainClassName domain类名
     * @param content         输出文件内容
     */
    private static void addCountMethod(final String domainClassName, final List<String> content) {
        content.add("");
        content.add(getIndent(1) + "/**");
        content.add(getIndent(1) + " * 根据查询条件统计数量.");
        content.add(getIndent(1) + " * ");
        content.add(getIndent(1) + " * @param condition 统计条件");
        content.add(getIndent(1) + " * @return 由查询条件限制下的统计条数");
        content.add(getIndent(1) + " */");
        content.add(getIndent(1) + "int count(" + domainClassName + " condition);");
    }

    /**
     * 增加insert方法.
     *
     * @param domainClassName domain类名
     * @param content         输出文件内容
     */
    private static void addInsertMethod(final String domainClassName, final List<String> content) {
        content.add("");
        content.add(getIndent(1) + "/**");
        content.add(getIndent(1) + " * 插入一条数据.");
        content.add(getIndent(1) + " * ");
        content.add(getIndent(1) + " * @param " + StringUtils.firstCharToLower(domainClassName) + " 待插入对象");
        content.add(getIndent(1) + " */");
        content.add(getIndent(1) + "void insert(" + domainClassName + " " + StringUtils.firstCharToLower(domainClassName) + ");");
    }

    /**
     * 增加batchInsert方法，批量插入数据.
     *
     * @param domainClassName domain类名
     * @param content         输出文件内容
     */
    private static void addBatchInsertMethod(final String domainClassName, final List<String> content) {
        content.add("");
        content.add(getIndent(1) + "/**");
        content.add(getIndent(1) + " * 批量插入多条数据.");
        content.add(getIndent(1) + " * ");
        content.add(getIndent(1) + " * @param list 待插入对象列表");
        content.add(getIndent(1) + " */");
        content.add(getIndent(1) + "void batchInsert(List<" + domainClassName + "> list);");
    }
}
