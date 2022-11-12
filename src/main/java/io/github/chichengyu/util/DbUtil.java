package io.github.chichengyu.util;

import org.apache.maven.plugin.MojoFailureException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Optional;
import java.util.Properties;

/** Db工具类
 * author xiaochi
 * Date 2022/11/12
 */
public class DbUtil {

    public static final String ORACLE = "ORACLE";
    public static final String MYSQL = "MYSQL";

    /**
     * 获取数据库连接
     */
    public static Connection getConnection(String type, String url, String username, String password) throws SQLException, ClassNotFoundException, MojoFailureException {
        if (ORACLE.equalsIgnoreCase(type)) {
            String driver = "oracle.jdbc.driver.OracleDriver";
            Properties props = new Properties();
            props.put("user", username);
            props.put("password", password);
            props.put("remarksReporting","true");
            Class.forName(driver);
            return DriverManager.getConnection(url, props);
        } else if (MYSQL.equalsIgnoreCase(type)) {
            String driver = "com.mysql.jdbc.Driver";
            Class.forName(driver);
            return DriverManager.getConnection(url, username, password);
        } else {
            throw new MojoFailureException("不支持的数据库类型，dbType=" + type);
        }
    }

    /**
     * 根据数据库类型与之对应的java类型
     */
    public static String getTypeToField(String type) {
        if (type.contains("BIGINT")){
            return "Long";
        }else if (type.contains("NUMBER")||type.contains("INT")||type.contains("BIT")){
            return "Integer";
        }else if (type.contains("VARCHAR2")||type.contains("VARCHAR")||type.contains("CHAR")){
            return "String";
        }else if (type.contains("DATETIME")||type.contains("DATE")){
            return "Date";
        }else {
            return "null";
        }
    }

    /**
     * 获取异常基本信息
     */
    public static String getExceptionSimpleInfo(Exception e) {
        Optional<String> eName = Optional.ofNullable(e.getClass().getName());
        Optional<String> eMessage = Optional.ofNullable(e.getMessage());
        return eName.orElse("java.lang.Exception") + ":" + eMessage.orElse("发生了异常");
    }
}
