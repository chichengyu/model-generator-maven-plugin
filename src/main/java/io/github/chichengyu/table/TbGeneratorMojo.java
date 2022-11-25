//package io.github.chichengyu.table;
//
//import io.github.chichengyu.util.DbUtil;
//import org.apache.maven.plugin.AbstractMojo;
//import org.apache.maven.plugin.MojoFailureException;
//
//import java.lang.reflect.Modifier;
//import java.net.URL;
//import java.net.URLClassLoader;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//import java.sql.Connection;
//import java.sql.ResultSet;
//import java.sql.SQLException;
//import java.sql.Statement;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;
//
///*
// * @goal CustomMavenMojo：表示该插件的服务目标
// * @phase compile：表示该插件的生效周期阶段
// * @requiresProject false：表示是否依托于一个项目才能运行该插件
// * @parameter expression="${name}"：表示插件参数，使用插件的时候会用得到
// * @required:代表该参数不能省略
// */
//
///**
// * author xiaochi
// * Date 2022/11/12
// *
// * @goal tb
// *
// */
//public class TbGeneratorMojo extends AbstractMojo {
//
//    /**
//     * path of the source folder.
//     * @parameter expression="${sourceFolderPath}"
//     * @required
//     */
//    private String sourceFolderPath;
//
//    /**
//     * path of the classes folder.
//     * @parameter expression="${classFolderPath}"
//     * @required
//     */
//    private String classFolderPath;
//
//    /**
//     * name of the package.
//     * @parameter expression="${packageName}"
//     * @required
//     */
//    private String packageName;
//
//    /**
//     * url of the database.
//     * @parameter expression="${dbUrl}"
//     * @required
//     */
//    private String dbUrl;
//
//    /**
//     * user of the the database.
//     * @parameter expression="${dbUser}"
//     * @required
//     */
//    private String dbUser;
//
//    /**
//     * password of the the database.
//     * @parameter expression="${dbPwd}"
//     * //@required
//     */
//    private String dbPwd;
//
//    /**
//     * table name of generator java file.
//     * @parameter expression="${classNames}"
//     * @required
//     */
//    private String[] classNames;
//
//    /**
//     * type of the database, for example:oracle、mysql.
//     */
//    private String dbType;
//
//    @Override
//    public void execute() throws MojoFailureException {
//        getLog().info("插件命令generator:tb开始执行...");
//
//        if (classNames == null || classNames.length == 0) {
//            getLog().info("没有填写任何类名！");
//            return;
//        }
//
//        // 根据dbUrl获取数据库类型
//        if (dbUrl.contains("oracle")) {
//            dbType = DbUtil.ORACLE;
//        }
//        else if (dbUrl.contains("mysql")) {
//            dbType = DbUtil.MYSQL;
//        }
//        else {
//            throw new MojoFailureException("不支持的数据库类型！");
//        }
//
//        try {
//            exec();
//        } catch (final Exception e) {
//            getLog().error(e);
//            throw new MojoFailureException("执行发生异常了！");
//        }
//        getLog().info("处理完毕！");
//    }
//
//    /**
//     * 执行主逻辑方法
//     */
//    private void exec() throws Exception {
//        // 分析类结构
//        List<Do> dos = getDoList();
//
//        // 初始化数据库字段类型与Java类型对照表
//        initTypeMap();
//
//        // 创建sql并执行
//        if (DbUtil.ORACLE.equalsIgnoreCase(dbType)) {
//            createTableForOracle(dos);
//        }
//        else if (DbUtil.MYSQL.equalsIgnoreCase(dbType)) {
//            createTableForMysql(dos);
//        }
//        else {
//            throw new MojoFailureException("不支持的数据库类型！");
//        }
//
//    }
//
//    /**
//     * 得到类信息集合
//     * @return
//     * @throws Exception
//     */
//    private List<Do> getDoList() throws Exception {
//        // 根据文件夹名字构建URL，注意：文件夹必须以/结尾
//        URL url = new URL("file:/" + classFolderPath);
//        URLClassLoader loader = new URLClassLoader(new URL[] {url});
//
//        // 加载类并放入集合
//        List<Do> dos = new ArrayList<>();
//        Arrays.stream(classNames)
//            // 加载类，不存在的忽略
//            .map(className -> {
//                Class<?> clazz = null;
//                try {
//                    clazz = loader.loadClass(packageName + "." + className);
//                    getLog().info("成功加载了类" + className);
//                } catch (ClassNotFoundException e) {
//                    getLog().warn(e);
//                }
//                return clazz;
//            })
//            // 过滤为空的class对象
//            .filter(clazz -> clazz != null)
//            // 将Clazz对象转为PO对象，并放进dos集合
//            .forEach(clazz -> {
//                getLog().info("正在解析类：" + clazz.getName());
//                Do _do = new Do();
//                _do.setPoName(clazz.getSimpleName());
//
//                // 属性注释
//                Path path = Paths.get(sourceFolderPath + packageName.replace(".", "/") + "/" +  _do.getPoName() + ".java");
//                Map<String, String> ParamsNotes = new HashMap<>();
//                try {
//                    String text = Files.readAllLines(path).stream() .reduce("", (s1, s2) -> s1 + s2);
//                    text = text.substring(text.indexOf("public class"));
//
//                    List<String> ss = getSubUtil(text, "\\/\\*\\*(\\s|.)*?;");
//                    ss.stream().filter(s -> !s.contains("public"))
//                        .forEach(s -> {
//                        String paramName = s.substring(s.lastIndexOf(" ") + 1, s.length() - 1);
//                        String remark = s.substring(0, s.lastIndexOf("*/")).replace("/", "").replace("*", "").replaceAll("\\s", "");
//                        ParamsNotes.put(paramName, remark);
//                    });
//
//                } catch (Exception e) {
//                    getLog().warn(DbUtil.getExceptionSimpleInfo(e));
//                    getLog().warn("解析Java文件的属性注释失败！");
//                }
//
//                List<Do.Param> params = new ArrayList<>();
//                // 获取所有的字段数组，转为Stream操作
//                Arrays.stream(clazz.getDeclaredFields())
//                    // 过滤serialVersionUID字段
//                    .filter(f -> !"serialVersionUID".equals(f.getName()))
//                    // 过滤非private字段
//                    .filter(f -> f.getModifiers() == Modifier.PRIVATE)
//                    // 遍历，对每一个字段取值，并放进map中
//                    .forEach(f -> {
//                        Do.Param param = _do.new Param();
//                        String name = f.getName();
//                        // 设置属性名
//                        param.setParamName(name);
//                        // 设置参数类型
//                        param.setParamType(f.getType().getSimpleName());
//                        // 设置注释
//                        param.setParamRemark(ParamsNotes.getOrDefault(name, ""));
//
//                        params.add(param);
//                    });
//                _do.setParams(params);
//
//                getLog().info("得到类信息：" + _do.toString());
//                dos.add(_do);
//            });
//        return dos;
//    }
//
//    /**
//     * 创建Oracle表
//     * @param dos
//     */
//    private void createTableForOracle(List<Do> dos) {
//        dos.parallelStream()
//            .forEach((Do _do) -> {
//                String tableName = className2TableName(_do.getPoName());
//
//                // 创表sql初始语句
//                StringBuffer cteateTableSql = new StringBuffer();
//                cteateTableSql.append("create table " + tableName + "(" + ENTER_KEY);
//                // 字段注释sql初始集合
//                List<String> fieldsNotesSqls = new ArrayList<>();
//
//                // 遍历字段，拼接创表sql和添加字段注释sql元素
//                List<Do.Param> params = _do.getParams();
//                params.stream()
//                    .forEach((param -> {
//                        String  paramName = param.getParamName(),
//                                fieldName = camelToUnderline(paramName),
//                                fieldType = typeMap.get(param.getParamType());
//                        cteateTableSql.append(fieldName + " " + fieldType + "," + ENTER_KEY);
//                        // 产生注释语句
//                        String fieldNoteSql = "comment on column " + tableName + "." + fieldName + " is '" + param.getParamRemark() + "'";
//                        fieldsNotesSqls.add(fieldNoteSql);
//                    }));
//
//                // 得到最终版创表sql
//                int index = cteateTableSql.lastIndexOf(",");
//                cteateTableSql.replace(index, index + 1, "");
//                cteateTableSql.append(")");
//                String createTbSql = cteateTableSql.toString();
//                // 检查表是否已存在sql
//                String checkExistSql = "select count(1) from user_tables where table_name = '" + tableName.toUpperCase() + "'";
//
//                try (Connection connCount = getConnection(); Statement statement = connCount.createStatement();
//                    ResultSet resultSet = statement.executeQuery(checkExistSql)) {
//                    // 表已存在必须先删除
//                    if (resultSet.next() && resultSet.getInt(1) > 0) {
//                        getLog().info("表" + tableName + "已存在，将执行删除操作...");
//                        String deleteSql = "drop table " + tableName;
//                        statement.executeUpdate(deleteSql);
//                    }
//                    // 执行创表sql
//                    getLog().info("执行创建表语句..." + ENTER_KEY + createTbSql);
//                    statement.executeUpdate(createTbSql);
//
//                    // 执行添加注释sql
//                    fieldsNotesSqls.forEach(fieldNotesSql -> {
//                        try {
//                            statement.execute(fieldNotesSql);
//                        } catch (SQLException e) {
//                            getLog().warn(DbUtil.getExceptionSimpleInfo(e));
//                        }
//                    });
//
//                    getLog().info("创建表" + tableName + "成功！");
//                } catch (Exception e) {
//                    getLog().error(e);
//                    System.exit(0);
//                }
//            });
//    }
//
//    /**
//     * 创建Mysq表
//     * @param dos
//     */
//    private void createTableForMysql(List<Do> dos) {
//        dos.parallelStream()
//            .forEach((Do _do) -> {
//                String tableName = className2TableName(_do.getPoName());
//
//                // 如果表存在，先删除
//                String deleteSql = "drop table if exists " + tableName;
//                try (Connection connDel = getConnection(); Statement statement = connDel.createStatement()) {
//                    statement.executeUpdate(deleteSql);
//                } catch (SQLException | ClassNotFoundException | MojoFailureException e) {
//                    getLog().error(e);
//                    System.exit(0);
//                }
//
//                // 创表sql初始化
//                StringBuffer sql = new StringBuffer();
//                sql.append("create table " + tableName + "(" + ENTER_KEY);
//                // 遍历字段，拼接创表sql
//                _do.getParams().stream()
//                    .forEach((param -> {
//                        String fieldName = camelToUnderline(param.getParamName()),
//                                fieldType = typeMap.get(param.getParamType());
//                        sql.append(fieldName + " " + fieldType + " comment '" + param.getParamRemark() + "'," + ENTER_KEY);
//
//                    }));
//
//                // 得到最终版创表sql
//                int index = sql.lastIndexOf(",");
//                sql.replace(index, index + 1, "");
//                sql.append(")");
//                String createTableSql = sql.toString();
//
//                // 执行sql语句
//                getLog().info("正在创建表" + tableName + "..." + ENTER_KEY + createTableSql);
//                try (Connection connection = getConnection(); Statement statement = connection.createStatement()) {
//                    statement.executeUpdate(createTableSql);
//                    getLog().info("创建表" + tableName + "成功！");
//                } catch (SQLException | ClassNotFoundException | MojoFailureException e) {
//                    getLog().error(e);
//                    getLog().error("创建表" + tableName + "失败！");
//                }
//            });
//    }
//
//    /**
//     * 数据库字段类型与Java类型对照表
//     */
//    private Map<String, String> typeMap = new HashMap<>();
//
//    /**
//     * 初始化数据库字段类型与Java类型对照表
//     */
//    private void initTypeMap() {
//        if (DbUtil.ORACLE.equalsIgnoreCase(dbType)) {
//            typeMap.put("Long", "NUMBER(11)");
//            typeMap.put("Date", "DATE");
//            typeMap.put("String", "VARCHAR2(100)");
//            typeMap.put("Integer", "NUMBER(5)");
//            typeMap.put("long", "NUMBER(11)");
//            typeMap.put("int", "NUMBER(5)");
//        }
//        if (DbUtil.MYSQL.equalsIgnoreCase(dbType)) {
//            typeMap.put("Date", "DATETIME");
//            typeMap.put("String", "VARCHAR(100)");
//            typeMap.put("Long", "INT");
//            typeMap.put("Integer", "INT");
//            typeMap.put("long", "INT");
//            typeMap.put("int", "INT");
//        }
//    }
//    /**
//     * 驼峰命名转下划线命名（针对属性）
//     */
//    public String camelToUnderline(String str) {
//        if (str == null || str.trim().isEmpty()){
//            return "";
//        }
//        int len = str.length();
//        StringBuilder sb = new StringBuilder(len);
//        for (int i = 0; i < len; i++) {
//            char c = str.charAt(i);
//            if (Character.isUpperCase(c)){
//                sb.append("_");
//                sb.append(Character.toLowerCase(c));
//            }else{
//                sb.append(c);
//            }
//        }
//        return sb.toString();
//    }
//
//    /**
//     * 类名转表名
//     * @param className
//     * @return
//     */
//    private String className2TableName(String className) {
//        return Arrays.stream(className.split(""))
//            .reduce((s1, s2) -> {
//                String ss1 = Character.isUpperCase(s1.charAt(0)) ? "_" + s1 : s1;
//                String ss2 = Character.isUpperCase(s2.charAt(0)) ? "_" + s2 : s2;
//                return ss1 + ss2;
//            })
//            .orElse("_")
//            .substring(1)
//            .toLowerCase();
//    }
//
//    /**
//     * 获得连接对象
//     * @return
//     * @throws MojoFailureException
//     * @throws SQLException
//     * @throws ClassNotFoundException
//     */
//    private Connection getConnection() throws MojoFailureException, SQLException, ClassNotFoundException {
//        return DbUtil.getConnection(dbType, dbUrl, dbUser, dbPwd);
//    }
//
//    /**
//     * 根据正则表达式获得指定字符串符合的内容
//     * @param soap
//     * @param rgex
//     * @return
//     */
//    public static List<String> getSubUtil(String soap,String rgex){
//        List<String> list = new ArrayList<String>();
//        Pattern pattern = Pattern.compile(rgex);
//        Matcher m = pattern.matcher(soap);
//        while (m.find()) {
//            list.add(m.group(0));
//        }
//        return list;
//    }
//
//    private static final String ENTER_KEY = "\r\n";
//
//}