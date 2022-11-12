package io.github.chichengyu;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

/**
 * author xiaochi
 * Date 2022/11/12
 *
 * @goal help
 */
public class HelpMojo extends AbstractMojo {

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        StringBuffer sb = new StringBuffer();
        sb.append("使用方式").append("\n");
        sb.append("参数配置：").append("\n");
        sb.append("    path：${basedir}/src/main/java/ 输出文件目录(不用修改) 必须").append("\n");
        sb.append("    packageName：com.xxx.xxx 包名(自定义) 必须").append("\n");
        sb.append("    modelFolderName：如 TbUserDao、TbUserEntity Model名称(自定义) 可选").append("\n");
        sb.append("    serviceFolderName：如 TbUserService Service名称(自定义) 可选").append("\n");
        sb.append("    url：jdbc:mysql://xxx.cn/test?useSSL=false&amp;characterEncoding=utf8 数据库url(自定义) 必须").append("\n");
        sb.append("    username：xxxxxx 数据库username(自定义) 必须").append("\n");
        sb.append("    password：xxxxxx 数据库password(自定义) 必须").append("\n");
        sb.append("    tableNames：一个include代表一个表 数据库password(自定义) 必须").append("\n");
        sb.append("        <tableNames>").append("\n");
        sb.append("            <include>tb_user</include>").append("\n");
        sb.append("            <include>...</include>").append("\n");
        sb.append("        </tableNames>");
        getLog().info(sb);
    }
}
