package com.xiafei.tools.common;

import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * <P>Description: 数据库工具. </P>
 * <P>CALLED BY:   齐霞飞 </P>
 * <P>UPDATE BY:   齐霞飞 </P>
 * <P>CREATE DATE: 2017/7/13</P>
 * <P>UPDATE DATE: 2017/7/13</P>
 *
 * @author qixiafei
 * @version 1.0
 * @since java 1.7.0
 */
@Slf4j
public final class Db {

    /**
     * mysql-jdbc驱动名称.
     */
    private static final String MYSQL_JDBC_DRIVER_NAME = "com.mysql.jdbc.Driver";


    /**
     * orcle-jdbc驱动名称.
     */
    private static final String ORACLE_JDBC_DRIVER_NAME = "oracle.jdbc.driver.OracleDriver";

    public static void main(String[] args) throws SQLException {
        final Connection connection1 = getMysqlConn("jdbc:mysql://192.168.130.221:3306/lease?useUnicode=true&characterEncoding=" +
                        "UTF-8&allowMultiQueries=true&rewriteBatchedStatements=true&useSSL=true",
                "root", "root");
        connection1.setAutoCommit(false);
        final Connection connection2 = getMysqlConn("jdbc:mysql://192.168.130.221:3306/lease?useUnicode=true&characterEncoding=" +
                        "UTF-8&allowMultiQueries=true&rewriteBatchedStatements=true&useSSL=true",
                "root", "root");
        connection2.setAutoCommit(false);

        log.info("数据库初始化成功");
        for (int i = 0; i < 10000; i++) {
            new Thread(() -> {
                try {
                    Statement sm = connection1.createStatement();
                    sm.addBatch("UPDATE  `LOCK` SET `data`=3 WHERE `data`=3 AND coop_code = '1'");
                    sm.addBatch(" UPDATE `LOCK` SET `data2` = `data2`+1 WHERE `data`=3 AND `data3`=0 AND SERVICE = '1'");
                    sm.executeBatch();
                    connection1.commit();

                } catch (Exception e) {
                    e.printStackTrace();
                }


            }).start();
            new Thread(() -> {
                try {

                    Statement sm = connection2.createStatement();
                    sm.addBatch("UPDATE  `LOCK` SET `data`=3 WHERE `data`=3 AND coop_code = '2'");
                    sm.addBatch(" UPDATE `LOCK` SET `data2` = `data2`+1 WHERE `data`=3 AND `data3`=0 AND SERVICE = '2'");
                    sm.executeBatch();
                    connection2.commit();

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }).start();

        }
    }

    /**
     * 获得mysql数据库连接.
     *
     * @param url      数据库地址
     * @param user     用户名
     * @param password 密码
     * @return mysql数据库连接
     */
    public static Connection getMysqlConn(final String url, final String user, final String password) {

        return getConn(MYSQL_JDBC_DRIVER_NAME, url, user, password);
    }

    /**
     * 获得oracle数据库连接.
     *
     * @param url      数据库地址
     * @param user     用户名
     * @param password 密码
     * @return oracle数据库连接
     */
    public static Connection getOracleConn(final String url, final String user, final String password) {
        return getConn(ORACLE_JDBC_DRIVER_NAME, url, user, password);
    }

    private static Connection getConn(final String driverClass, final String url,
                                      final String user, final String password) {
        // 加载mysql-jdbc驱动.
        try {
            Class.forName(MYSQL_JDBC_DRIVER_NAME);
            return DriverManager.getConnection(url, user, password);
        } catch (ClassNotFoundException e) {
            log.error("无法加载{}驱动", driverClass, e);
            throw new RuntimeException("无法加载数据库驱动" + driverClass);
        } catch (SQLException e) {
            log.error("建立{}数据库连接失败", driverClass, e);
            throw new RuntimeException("建立数据库连接失败" + driverClass);

        }
    }
}
