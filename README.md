# model-generator-maven-plugin

## 介绍

为了方便从数据库自动生成java实体、model层、service层与servicel实现类的代码自动生成工具，没有MyBatis生成的那些多余的

##### 使用说明
导包
```
<plugin>
    <groupId>io.github.chichengyu</groupId>
    <artifactId>model-generator-maven-plugin</artifactId>
    <version>1.0.2</version>
    <configuration>
        <!-- 输出文件目录,不用改 -->
        <path>${basedir}/src/main/java/</path>
        <!-- 包名 -->
        <packageName>com.demo.pojo</packageName>
        <!-- model名称,可选,如 TbUserDao、TbUserEntity(最好首字母大写,然后改包名称首字母小写) -->
        <modelFolderName>Dao</modelFolderName>
        <!-- service名称,可选,如 TbUserService(最好首字母大写,然后改包名称首字母小写) -->
        <serviceFolderName>Service</serviceFolderName>
        <!-- 数据库链接 -->
        <url>jdbc:mysql://127.0.0.1/test?useSSL=false&amp;characterEncoding=utf8</url>
        <!-- 数据库账号 -->
        <username>root</username>
        <!-- 数据库密码 -->
        <password>123456</password>
        <tableNames>
            <!-- 每一个 include代表一张表,可以多个 -->
            <include>tb_user</include>
        </tableNames>
    </configuration>
</plugin>
```
导包之后，如果你的数据库是 oracle ，由于 oracle 是收费的，所以可能下载不了驱动jar包，需要手动去下载本地安装，这里我准备一个下载地址

 - 链接：https://share.weiyun.com/4V67i2fT 密码：66dg96

下载完成,放到一个你知道的位置，比如我放在D:/,执行安装命令
```
mvn install:install-file -Dfile=/D:/ojdbc6.jar -DgroupId=com.oracle -DartifactId=ojdbc6 -Dversion=11.2.0.1.0 -Dpackaging=jar -DgeneratePom=true
```
然后一切就绪后，就直接在idea右边侧边栏，选中 `maven -> Plugins -> model-generator -> model-generator:model`(执行`model-generator:help`，可看到所有配置参数),执行完成后，可以到包下已经生成对应的java文件了。