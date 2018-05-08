package com.xiafei.tools.generatesource;

import com.xiafei.tools.generatesource.enums.DataBaseTypeEnum;
import com.xiafei.tools.generatesource.template.DaoTemplate;
import com.xiafei.tools.generatesource.template.DomainTemplate;
import com.xiafei.tools.generatesource.template.MapperTemplate;
import com.xiafei.tools.common.Db;
import com.xiafei.tools.common.FileUtils;
import com.xiafei.tools.common.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * <P>Description: 资源文件生成器. </P>
 * <P>CALLED BY:   齐霞飞 </P>
 * <P>UPDATE BY:   齐霞飞 </P>
 * <P>CREATE DATE: 2017年7月13日</P>
 * <P>UPDATE DATE: 2017年7月13日</P>
 *
 * @author qixiafei
 * @version 1.0
 * @since java 1.7.0
 */
public final class SourceGenerator {

    /**
     * log4j.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(SourceGenerator.class);

    /**
     * mysql search table structrue sql.
     */
    private static final String MYSQL_SQL_TEMPLATE = "SELECT t.COLUMN_NAME,upper(t.DATA_TYPE),t.COLUMN_COMMENT,upper(t.COLUMN_KEY) FROM information_schema.COLUMNS t WHERE t.TABLE_NAME = '$tableName' AND t.TABLE_SCHEMA = '$tableSchema' ORDER BY t.ORDINAL_POSITION";


    /**
     * 工具类不允许实例化.
     */
    private SourceGenerator() {

    }

    /**
     * 示例程序.
     *
     * @param args system boot argument
     */
    public static void main(String[] args) {
        GenerateSourceParam param = new GenerateSourceParam();
        param.setCommentsUser("齐霞飞");
        param.setCommentsSince("JDK 1.8.0");
        param.setCommentsVersion("1.0.0");
        param.setDomainDirectory("C:\\code\\local\\yx\\lease-core\\lease-core-domain\\src\\main\\java\\com\\virgo\\finance\\lease\\core\\domain\\po");
        param.setDomainPackage("com.virgo.finance.lease.core.domain.po");
        param.setDomainSuffix("Po");
        param.setMapperDirectory("C:\\code\\local\\yx\\lease-core\\lease-core-web\\src\\main\\resources\\lease\\mappings");
        param.setDaoDirectory("C:\\code\\local\\yx\\lease-core\\lease-core-dao\\src\\main\\java\\com\\virgo\\finance\\lease\\core\\dao");
        param.setDaoPackage("com.virgo.finance.lease.core.dao");
        // is cover ori file's content
        param.setCoverFile(false);
        param.setJavaTime(false);
        param.setLombok(true);

        String jdbcUrl = "jdbc:mysql://192.168.130.221:3306/lease?useUnicode=true&characterEncoding=UTF-8&allowMultiQueries=true&rewriteBatchedStatements=true&useSSL=true";
        String dbUser = "root";
        String pwd = "root";

        List<GenerateSourceParamItem> itemList = new ArrayList<>();

//        GenerateSourceParamItem table1 = new GenerateSourceParamItem(DataBaseTypeEnum.MYSQL, jdbcUrl, dbUser, pwd,
//                "IDEMPOTENT", "small_order",
//                "Idempotent", "幂等表");
//        itemList.add(table1);
//
//        GenerateSourceParamItem table2 = new GenerateSourceParamItem(DataBaseTypeEnum.MYSQL,jdbcUrl,dbUser, pwd,
//                "DEPOSIT_ACCOUNT", "small_order",
//            "DepositAccount", "保证金账户表");
//        itemList.add(table2);
//
//        GenerateSourceParamItem table3 = new GenerateSourceParamItem(DataBaseTypeEnum.MYSQL,jdbcUrl,dbUser, pwd,
//                "DEPOSIT_ACCOUNT_CHANGES", "small_order",
//            "DepositAccountChanges", "保证金账户变动记录表");
//        itemList.add(table3);
//
//        GenerateSourceParamItem table4 = new GenerateSourceParamItem(DataBaseTypeEnum.MYSQL,jdbcUrl,dbUser, pwd,
//                "APPLY_ORDER", "small_order",
//            "ApplyOrder", "租赁申请订单表");
//        itemList.add(table4);
//
//        GenerateSourceParamItem table5 = new GenerateSourceParamItem(DataBaseTypeEnum.MYSQL,jdbcUrl,dbUser, pwd,
//                "USER_INFO", "small_order",
//            "UserInfo", "用户表");
//        itemList.add(table5);
//
//        GenerateSourceParamItem table6 = new GenerateSourceParamItem(DataBaseTypeEnum.MYSQL,jdbcUrl,dbUser, pwd,
//                "SEND_LOG", "small_order",
//            "SendLog", "发送日志");
//        itemList.add(table6);
//
//        GenerateSourceParamItem table7 = new GenerateSourceParamItem(DataBaseTypeEnum.MYSQL,jdbcUrl,dbUser, pwd,
//                "APPLY_INFO", "small_order",
//            "ApplyInfo", "瑞洺租赁附加信息表");
//        itemList.add(table7);
//
//        GenerateSourceParamItem table8 = new GenerateSourceParamItem(DataBaseTypeEnum.MYSQL,jdbcUrl,dbUser, pwd,
//                "APPLY_ID_CARD_INFO", "small_order",
//            "ApplyIdCardInfo", "租赁申请身份证信息表");
//        itemList.add(table8);
//
//        GenerateSourceParamItem table9 = new GenerateSourceParamItem(DataBaseTypeEnum.MYSQL,jdbcUrl,dbUser, pwd,
//                "BUSINESS_LICENSE", "small_order",
//            "BusinessLicense", "营业执照信息表");
//        itemList.add(table9);
//
//        GenerateSourceParamItem table10 = new GenerateSourceParamItem(DataBaseTypeEnum.MYSQL,jdbcUrl,dbUser, pwd,
//                "CATERING_SERVICE_LICENSE", "small_order",
//            "CateringServiceLicense", "餐饮服务许可证信息表");
//        itemList.add(table10);
//
//        GenerateSourceParamItem table11 = new GenerateSourceParamItem(DataBaseTypeEnum.MYSQL,jdbcUrl,dbUser, pwd,
//                "APPLY_FILES", "small_order",
//            "ApplyFiles", "租赁文件资料表（1对多）");
//        itemList.add(table11);
//        GenerateSourceParamItem table12 = new GenerateSourceParamItem(DataBaseTypeEnum.MYSQL,jdbcUrl,dbUser, pwd,
//                "APPLY_GOODS", "small_order",
//            "ApplyGoods", "租赁物资表（1对多）");
//        itemList.add(table12);
//        GenerateSourceParamItem table13 = new GenerateSourceParamItem(DataBaseTypeEnum.MYSQL,jdbcUrl,dbUser, pwd,
//                "CONTRACT_INFO", "small_order",
//            "ContractInfo", "合同信息表");
//        itemList.add(table13);
//        GenerateSourceParamItem table14 = new GenerateSourceParamItem(DataBaseTypeEnum.MYSQL,jdbcUrl,dbUser, pwd,
//                "USER_INFO", "small_order",
//            "UserInfo", "用户表");
//        itemList.add(table14);
//        GenerateSourceParamItem table15 = new GenerateSourceParamItem(DataBaseTypeEnum.MYSQL,jdbcUrl,dbUser, pwd,
//                "BUSINESS_LICENSE", "lease",
//            "BusinessLicense", "营业执照信息表");
//        itemList.add(table15);
//        GenerateSourceParamItem table16 = new GenerateSourceParamItem(DataBaseTypeEnum.MYSQL,jdbcUrl,dbUser, pwd,
//                "CATERING_SERVICE_LICENSE", "lease",
//            "CateringServiceLicense", "餐饮服务许可证信息表");
//        itemList.add(table16);
//        GenerateSourceParamItem table17 = new GenerateSourceParamItem(DataBaseTypeEnum.MYSQL,jdbcUrl,dbUser, pwd,
//                "SERIAL_NO", "lease",
//            "SerialNo", "序列号步长表");
//        itemList.add(table17);
//        GenerateSourceParamItem table18 = new GenerateSourceParamItem(DataBaseTypeEnum.MYSQL,jdbcUrl,dbUser, pwd,
//                "COOPERATION", "lease",
//            "Cooperation", "合作方表");
//        itemList.add(table18);
        GenerateSourceParamItem table19 = new GenerateSourceParamItem(DataBaseTypeEnum.MYSQL, jdbcUrl, dbUser, pwd,
                "CONTRACT_TPLT", "lease",
                "ContractTplt", "合同模板信息表");
        itemList.add(table19);
//        GenerateSourceParamItem table20 = new GenerateSourceParamItem(DataBaseTypeEnum.MYSQL,jdbcUrl,dbUser, pwd,
//                "COOP_TPLT_REL", "lease",
//            "CoopTpltRel", "合作方-合同模板关联关系信息表");
//        itemList.add(table20);//
//        GenerateSourceParamItem table21 = new GenerateSourceParamItem(DataBaseTypeEnum.MYSQL,jdbcUrl,dbUser, pwd,
//                "SEND_LOG", "lease",
//                "SendLog", "发送日志");
//        itemList.add(table21);
//        GenerateSourceParamItem table22 = new GenerateSourceParamItem(DataBaseTypeEnum.MYSQL,jdbcUrl,dbUser, pwd,
//                "REPAYMENT_FAIL_RECORD", "lease",
//                "RepaymentFailRecord", "还款失败记录表");
//        itemList.add(table22);
//        GenerateSourceParamItem table23 = new GenerateSourceParamItem(DataBaseTypeEnum.MYSQL,jdbcUrl,dbUser, pwd,
//                "TIAN_YAN_CHA_CO_RECORD", "lease",
//                "TianYanChaCoRecord", "天眼查查询企业基本信息调用记录表");
//        itemList.add(table23);
        param.setItems(itemList);

        exec(param);


    }

    /**
     * 执行资源文件生成.
     *
     * @param param 生成参数
     */
    public static void exec(final GenerateSourceParam param) {
        // 校验参数合法，必要校验都放在这里
        if (!validParam(param)) {
            return;
        }

        // 循环要生成资源的项目
        for (GenerateSourceParamItem item : param.getItems()) {
            // 从数据库解析出的字段信息列表
            final List<ColumnInfo> columnInfoList = new ArrayList<>(16);
            // 访问数据库，查询表结构
            if (DataBaseTypeEnum.MYSQL == item.getDataBaseType()) {

                try {
                    // 建立数据库连接
                    final Connection conn = Db.getMysqlConn(item.getUrl(), item.getUser(), item.getPassword());
                    // 执行sql
                    String sql = MYSQL_SQL_TEMPLATE.replace("$tableName", item.getTableName()).replace("$tableSchema", item.getTableSchema());
                    LOGGER.info("查询表结构，sql：{}", sql);
                    final PreparedStatement preparedStatement = conn.prepareStatement(sql);
                    // 解析结果集拼装数据库字段对象
                    final ResultSet resultSet = preparedStatement.executeQuery();
                    if (resultSet == null) {
                        throw new RuntimeException("找不到数据库表信息");
                    }
                    while (resultSet.next()) {
                        final ColumnInfo columnInfo = new ColumnInfo();
                        columnInfo.setName(resultSet.getString(1));
                        columnInfo.setType(resultSet.getString(2));
                        columnInfo.setComment(resultSet.getString(3));
                        columnInfo.setKey(resultSet.getString(4));
                        columnInfoList.add(columnInfo);
                    }
                } catch (SQLException e) {
                    LOGGER.error("查询mysql数据库表结构出错", e);
                    return;
                }
            } else if (DataBaseTypeEnum.ORACLE == item.getDataBaseType()) {
                LOGGER.error("暂时不支持oracle数据库的资源自动生成");
                return;
            }

            if (columnInfoList.isEmpty()) {
                LOGGER.error("没有找到数据库表结构");
                return;
            }

            // 生成domain文件
            if (StringUtils.isNotBlank(param.getDomainDirectory())) {
                generateDomain(param, item, columnInfoList);
            }

            // 生成mapper文件
            if (StringUtils.isNotBlank(param.getMapperDirectory())) {
                generateMapper(param, item, columnInfoList);
            }

            // 生成dao文件
            if (StringUtils.isNotBlank(param.getDaoDirectory())) {
                generateDao(param, item, columnInfoList);
            }

        }

    }

    /**
     * 参数正确性做全方位校验.
     *
     * @param param 参数
     * @return true-通过,false-不通过
     */
    private static boolean validParam(final GenerateSourceParam param) {
        if (param.getItems() == null || param.getItems().isEmpty()) {
            LOGGER.error("参数中明细列表items为空，退出方法");
            return false;
        }

        if (StringUtils.isBlank(param.getDomainDirectory()) && StringUtils.isBlank(param.getDaoDirectory()) && StringUtils.isBlank(param.getMapperDirectory())) {
            LOGGER.error("参数中domian路径、dao路径、mapper路径都为空，退出方法");
            return false;
        }
        for (GenerateSourceParamItem item : param.getItems()) {
            if (item.getDataBaseType() == null) {
                LOGGER.error("必须传递数据库类字段，所传递参数：{}", param);
                return false;
            }
        }
        return true;
    }

    /**
     * 生成domian文件.
     *
     * @param param          生成参数
     * @param item           参数项
     * @param columnInfoList 数据库字段信息列表
     */
    private static void generateDomain(final GenerateSourceParam param, final GenerateSourceParamItem item, final List<ColumnInfo> columnInfoList) {
        // 输出文件内容
        final List<String> fileContent = new ArrayList<>(500);
        // 填充文件内容
        DomainTemplate.addContent(param, item, columnInfoList, fileContent);
        // 输出到文件
        String filePath = param.getDomainDirectory().replace("\\", "/");
        if (!filePath.endsWith("/")) {
            filePath += "/";
        }
        filePath += item.getClassName() + (param.getDomainSuffix() == null ? "" : param.getDomainSuffix()) + ".java";
        FileUtils.outPutToFileByLine(filePath, fileContent, param.isCoverFile());
        LOGGER.info("成功生成domain文件：{}", filePath);
    }


    /**
     * 生成mapper文件.
     *
     * @param param          生成参数
     * @param item
     * @param columnInfoList 数据库字段信息列表
     */
    private static void generateMapper(final GenerateSourceParam param, final GenerateSourceParamItem item, final List<ColumnInfo> columnInfoList) {
        // 输出文件内容
        final List<String> fileContent = new ArrayList<>(500);
        // 从模板加载文件内容
        MapperTemplate.addContent(param, item, columnInfoList, fileContent);
        // 输出到文件
        String filePath = param.getMapperDirectory().replace("\\", "/");
        ;
        if (!filePath.endsWith("/")) {
            filePath += "/";
        }
        filePath += item.getClassName() + "Mapper.xml";
        FileUtils.outPutToFileByLine(filePath, fileContent, param.isCoverFile());
        LOGGER.info("成功生成mapper文件：{}", filePath);
    }

    /**
     * 生成dao文件.
     *
     * @param param          生成参数
     * @param item
     * @param columnInfoList 数据库字段信息列表
     */
    private static void generateDao(final GenerateSourceParam param, final GenerateSourceParamItem item, final List<ColumnInfo> columnInfoList) {
        // 输出文件内容
        final List<String> fileContent = new ArrayList<>(500);
        // 从模板加载文件内容
        DaoTemplate.addContent(param, item, columnInfoList, fileContent);
        // 输出到文件
        String filePath = param.getDaoDirectory().replace("\\", "/");
        ;
        if (!filePath.endsWith("/")) {
            filePath += "/";
        }
        filePath += item.getClassName() + "Dao.java";
        FileUtils.outPutToFileByLine(filePath, fileContent, param.isCoverFile());
        LOGGER.info("成功生成dao文件：{}", filePath);
    }

}
