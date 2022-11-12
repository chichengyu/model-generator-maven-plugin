package io.github.chichengyu;

import io.github.chichengyu.model.JavaFileInfo;
import io.github.chichengyu.model.Table;
import io.github.chichengyu.util.DbUtil;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
 * @goal CustomMavenMojo：表示该插件的服务目标
 * @phase compile：表示该插件的生效周期阶段
 * @requiresProject false：表示是否依托于一个项目才能运行该插件
 * @parameter expression="${name}"：表示插件参数，使用插件的时候会用得到
 * @required:代表该参数不能省略
 */
/**
 * author xiaochi
 * Date 2022/11/12
 *
 * @goal model
 */
public class ModelGeneratorMojo extends AbstractMojo {

    /**
     * path of the out path.
     * @parameter expression="${outPath}"
     * @required
     */
    private String path;

    /**
     * name of the package.
     * @parameter expression="${packageName}"
     * @required
     */
    private String packageName;

    /**
     * class file suffix
     */
    private String suffix = "";

    /**
     * url of the database.
     * @parameter expression="${url}"
     * @required
     */
    private String url;

    /**
     * user of the the database.
     * @parameter expression="${username}"
     * @required
     */
    private String username;

    /**
     * password of the the database.
     * @parameter expression="${password}"
     */
    private String password;

    /**
     * table name of generator java file.
     * @parameter expression="${tableNames}"
     */
    private String[] tableNames;

    /**
     * type of the database, for example:oracle、mysql.
     */
    private String type;

    /**
     * model generator java file.
     * @parameter expression="${modelFolderName}"
     */
    private String modelFolderName = "";

    /**
     * service generator java file.
     * @parameter expression="${serviceFolderName}"
     */
    private String serviceFolderName = "";

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        getLog().info("插件命令model-generator:model开始执行...");

        if (tableNames == null || tableNames.length == 0) {
            getLog().info("没有填写任何表名！");
            return;
        }

        // 根据dbUrl获取数据库类型
        if (url.contains("oracle")) {
            type = DbUtil.ORACLE;
        } else if (url.contains("mysql")) {
            type = DbUtil.MYSQL;
        } else {
            throw new MojoFailureException("不支持的数据库类型！");
        }

        try {
            exec();
        } catch (final Exception e) {
            getLog().error(e);
            throw new MojoFailureException("执行发生异常了！");
        }
        getLog().info("处理完毕！");
    }

    /**
     * 执行
     */
    private void exec() throws Exception {
        List<Table> tables = new ArrayList<>();
        try {
            tables = geTables(tableNames);
        } catch (final Exception e) {
            //getLog().error(e);
            throw e;
        }
        String folderPathStr = path + packageName.replace(".", "/");
        String modelPathStr = folderPathStr + "/" + modelFolderName;
        String servicePathStr = folderPathStr + "/" + serviceFolderName;
        String serviceImplPathStr = servicePathStr + "/impl";
        Path folderPath = Paths.get(folderPathStr);
        try {
            if (!Files.exists(folderPath)) {
                Files.createDirectories(folderPath);
            }
            // model service serviceImpl
            if (!"".equals(modelFolderName)){
                Path modelPath = Paths.get(modelPathStr);
                if (!Files.exists(modelPath)) {
                    Files.createDirectories(modelPath);
                }
            }
            if (!"".equals(serviceFolderName)){
                Path servicePath = Paths.get(servicePathStr);
                if (!Files.exists(servicePath)) {
                    Files.createDirectories(servicePath);
                }
                Path serviceImplPath = Paths.get(serviceImplPathStr);
                if (!Files.exists(serviceImplPath)) {
                    Files.createDirectories(serviceImplPath);
                }
            }
        } catch (final IOException ioe) {
            //getLog().error(ioe);
            throw ioe;
        }
        // 使用并行流
        tables.stream().forEach(table -> {
            JavaFileInfo fileInfo = createDoJavaFileInfo(table);
            try {
                Path filePath = Paths.get(folderPathStr, fileInfo.getFileName() + ".java");
                if (!Files.exists(filePath)) {
                    Files.createFile(filePath);
                }
                Files.write(filePath, fileInfo.getText().getBytes());
                // model service serviceImpl
                if (!"".equals(modelFolderName)){
                    JavaFileInfo modelFileInfo = createModelFileInfo(table);
                    Path modelFilePath = Paths.get(modelPathStr, modelFileInfo.getFileName() + ".java");
                    if (!Files.exists(modelFilePath)){
                        Files.createFile(modelFilePath);
                    }
                    Files.write(modelFilePath, modelFileInfo.getText().getBytes());
                }
                if (!"".equals(serviceFolderName)){
                    JavaFileInfo serviceFileInfo = createServiceFileInfo(table);
                    Path serviceFilePath = Paths.get(servicePathStr, serviceFileInfo.getFileName() + ".java");
                    if (!Files.exists(serviceFilePath)){
                        Files.createFile(serviceFilePath);
                    }
                    Files.write(serviceFilePath, serviceFileInfo.getText().getBytes());
                    JavaFileInfo serviceImplFileInfo = createServiceImplFileInfo(table);
                    Path serviceImplFilePath = Paths.get(serviceImplPathStr, serviceImplFileInfo.getFileName() + ".java");
                    if (!Files.exists(serviceImplFilePath)){
                        Files.createFile(serviceImplFilePath);
                    }
                    Files.write(serviceImplFilePath, serviceImplFileInfo.getText().getBytes());
                }
            } catch (final IOException ioe) {
                getLog().error(ioe);
            }
            // 日志打印信息
            getLog().info(table.getTableName()  + "表的结构：");
            table.getColumns().stream().forEach(column -> getLog().info("    " + "字段名：" + column.getColumnName() + ", 类型：" + column.getColumnType() + ", 注释：" + column.getColumnRemark()));
        });
    }

    /**
     * 获取表对象列表
     */
    private List<Table> geTables(String[] tableNames) throws Exception {
        Connection connection = DbUtil.getConnection(type,url,username,password);
        DatabaseMetaData dbmd = connection.getMetaData();

        ResultSet resultSet = dbmd.getTables(null, "%", "%", new String[] { "TABLE" });
        List<Table> tables = new ArrayList<>();
        while (resultSet.next()) {
            String tableName = resultSet.getString("TABLE_NAME");
            boolean need = Arrays.stream(tableNames).anyMatch(name -> name.equalsIgnoreCase(tableName));
            boolean contains = tables.parallelStream().anyMatch(table -> table.getTableName().equalsIgnoreCase(tableName));
            if (!need || contains) {
                continue;
            }
            getLog().info("正在分析表" + tableName + "...");

            Table table = new Table();
            table.setTableName(tableName);
            table.setTableRemark(resultSet.getString("REMARKS"));

            List<Table.Column> columns = table.getColumns();
            ResultSet rs = null;
            if (DbUtil.ORACLE.equalsIgnoreCase(type)) {
                rs = dbmd.getColumns(null, getSchema(connection), tableName.toUpperCase(), "%");
            }
            // 除了oracle和db2其它这么用
            else {
                rs = dbmd.getColumns(null, "%", tableName, "%");
            }

            while (rs.next()) {
                Table.Column column = new Table.Column();
                column.setColumnName(rs.getString("COLUMN_NAME"));
                column.setColumnType(rs.getString("TYPE_NAME"));
                column.setColumnSize(rs.getInt("COLUMN_SIZE"));
                column.setColumnNullable(rs.getInt("NULLABLE"));
                column.setColumnDefaultValue(rs.getString("COLUMN_DEF"));
                column.setColumnRemark(rs.getString("REMARKS"));
                columns.add(column);
            }

            getLog().info("读取到" + columns.size() + "个字段");
            tables.add(table);
        }
        connection.close();
        return tables;
    }

    /**
     * 其他数据库不需要这个方法 oracle和db2需要
     */
    private String getSchema(Connection connection) throws Exception {
        String schema;
        schema = connection.getMetaData().getUserName();
        if ((schema == null) || (schema.length() == 0)) {
            throw new Exception("ORACLE数据库模式不允许为空");
        }
        return schema.toUpperCase().toString();
    }

    /**
     * 生成Model对象
     */
    private JavaFileInfo createModelFileInfo(Table table) {
        String tableName = table.getTableName(),
                className = tableName.substring(0, 1).toUpperCase() + lineToHump(tableName).substring(1) + modelFolderName;
        getLog().info("正在生成Model类" + className + "...");

        // 替换类名、包名、表名
        String classText = classModelTemplateText.replace("${packageName}", packageName+"."+modelFolderName)
                .replace("${className}", className).replace("${tableName}", tableName);

        return JavaFileInfo.create().setFileName(className).setText(classText.toString());
    }

    /**
     * 生成Service对象
     */
    private JavaFileInfo createServiceFileInfo(Table table) {
        String tableName = table.getTableName(),
                className = tableName.substring(0, 1).toUpperCase() + lineToHump(tableName).substring(1) + serviceFolderName;
        getLog().info("正在生成Service类" + className + "...");

        // 替换类名、包名、表名
        String classText = classServiceTemplateText.replace("${packageName}", packageName+"."+serviceFolderName)
                .replace("${className}", className).replace("${tableName}", tableName);

        return JavaFileInfo.create().setFileName(className).setText(classText.toString());
    }

    /**
     * 生成ServiceImpl对象实现类
     */
    private JavaFileInfo createServiceImplFileInfo(Table table) {
        String tableName = table.getTableName(),
                className = tableName.substring(0, 1).toUpperCase() + lineToHump(tableName).substring(1) ;
        String serviceClassName = className + serviceFolderName;
        className += serviceFolderName + "Impl";
        getLog().info("正在生成ServiceImpl实现类" + className + "...");
        // 替换类名、包名、表名
        String classText = classServiceImplTemplateText.replace("${packageName}", packageName+"."+ serviceFolderName + ".impl")
                .replace("${servicePackageName}", packageName+"."+serviceFolderName+"."+serviceClassName)
                .replace("${className}", className)
                .replace("${serviceName}", serviceClassName)
                .replace("${tableName}", tableName);

        return JavaFileInfo.create().setFileName(className).setText(classText.toString());
    }

    /**
     * 生成DO对象文本
     */
    private JavaFileInfo createDoJavaFileInfo(Table table) {
        String tableName = table.getTableName(),
                className = tableName.substring(0, 1).toUpperCase() + lineToHump(tableName).substring(1) + suffix;
        getLog().info("正在生成实体类" + className + "...");

        // 替换类名、包名、表名
        String classText = classTemplateText.replace("${packageName}", packageName)
                .replace("${className}", className).replace("${tableName}", tableName);

        List<String> fieldTexts = new ArrayList<>();
        List<String> getSetMethodTexts = new ArrayList<>();
        table.getColumns().stream().forEach(column -> {
            String name = lineToHump(column.getColumnName()),
                    type = DbUtil.getTypeToField(column.getColumnType().toUpperCase()),
                    nullAble = Objects.equals(column.getColumnNullable(), 1) ? "可空" : "非空",
                    remark = Optional.ofNullable(column.getColumnRemark()).orElse(""),
                    getterSetterName = name.substring(0, 1).toUpperCase() + name.substring(1);
            // 得到一个字段的声明、get方法、set方法
            String fieldText = fieldTemplateText.replace("${fieldRemark}", remark)
                    .replace("${otherInfo}", "长度：" + column.getColumnSize() + "，" + nullAble + "，默认值：" + column.getColumnDefaultValue())
                    .replace("${fieldType}", type).replace("${fieldName}", name);
            String getSetMethodText = getSetMethodTemplateText.replace("${fieldType}", type)
                    .replace("${fieldName}", name)
                    .replace("${u_fieldName}", getterSetterName);

            fieldTexts.add(fieldText);
            getSetMethodTexts.add(getSetMethodText);
        });

        // 得到全部字段的声明语句
        String fieldPart = fieldTexts.stream().reduce((f1, f2) -> f1 + f2).orElse("// 什么也没有^_-_^");
        // 得到全部get方法、set方法的语句
        String getSetMethodPart = getSetMethodTexts.stream().reduce((f1, f2) -> f1 + "\n" + f2).orElse("// 什么也没有^_-_^");

        // 替换类模板文件的字段部分和方法部分
        String classFinalText = classText.replace("@{fieldPart}", fieldPart)
                .replace("${getSetMethodPart}", getSetMethodPart);

        return JavaFileInfo.create().setFileName(className).setText(classFinalText.toString());
    }

    /**
     * 下划线转驼峰
     */
    private Pattern linePattern = Pattern.compile("_(\\w)");
    public String lineToHump(String str){
        str = str.toLowerCase();
        Matcher matcher = linePattern.matcher(str);
        StringBuffer sb = new StringBuffer();
        while(matcher.find()){
            matcher.appendReplacement(sb, matcher.group(1).toUpperCase());
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    /**
     * model类文件模板
     */
    private static final String classModelTemplateText = "package ${packageName};\n" +
            "\n" +
            "/**\n" +
            " * 工具自动生成...\n" +
            " * tableName = \"${tableName}\"\n" +
            " */\n" +
            "public interface ${className}{\n}";

    /**
     * service类文件模板
     */
    private static final String classServiceTemplateText = "package ${packageName};\n" +
            "\n" +
            "/**\n" +
            " * 工具自动生成...\n" +
            " * tableName = \"${tableName}\"\n" +
            " */\n" +
            "public interface ${className}{\n}";

    /**
     * serviceImpl实现类文件模板
     */
    private static final String classServiceImplTemplateText = "package ${packageName};\n" +
            "\n" +
            "import ${servicePackageName};\n" +
            "\n" +
            "/**\n" +
            " * 工具自动生成...\n" +
            " * tableName = \"${tableName}\"\n" +
            " */\n" +
            "public class ${className} implements ${serviceName}{\n}";

    /**
     * 类文件模板
     */
    private static final String classTemplateText = "package ${packageName};\n" +
            "\n" +
            "import java.io.Serializable;\n" +
            "import java.util.Date;\n" +
            "\n" +
            "/**\n" +
            " * 工具自动生成...\n" +
            " * tableName = \"${tableName}\"\n" +
            " */\n" +
            "public class ${className} implements Serializable {\n" +
            "\n" +
            "    private static final long serialVersionUID = 1L;\n" +
            "@{fieldPart}\n" +
            "${getSetMethodPart}\n" +
            "\n" +
            "}";

    /**
     * 字段模板
     */
    private static final String fieldTemplateText = "\n" +
            "    /**\n" +
            "     * ${fieldRemark}\n" +
            "     */\n" +
            "     // ${otherInfo}\n" +
            "    private ${fieldType} ${fieldName};\n";

    /**
     * getSet方法模板
     */
    private static final String getSetMethodTemplateText = "\n" +
            "    public ${fieldType} get${u_fieldName}() {\n" +
            "        return this.${fieldName};\n" +
            "    }\n" +
            "\n" +
            "    public void set${u_fieldName}(${fieldType} ${fieldName}) {\n" +
            "        this.${fieldName} = ${fieldName};\n" +
            "    }";
}
