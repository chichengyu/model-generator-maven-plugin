# model-generator-maven-plugin

<p align="left">
    <a href="https://github.com/chichengyu/model-generator-maven-plugin">
        <img src="https://img.shields.io/badge/%E4%BD%9C%E8%80%85-%E5%B0%8F%E6%B1%A0-%23129e50" alt="MIT License" />
    </a>
    <a href="https://github.com/chichengyu/model-generator-maven-plugin">
        <img src="https://img.shields.io/badge/last version-1.2.6-green" alt="version-1.2.6" />
    </a>
    <a href="https://github.com/chichengyu/model-generator-maven-plugin">
        <img src="https://img.shields.io/badge/last version-1.2.8-blue" alt="version-1.2.8" />
    </a>
</p>

## 介绍

为了方便从数据库自动生成java实体、model层、service层与servicelImpl实现类的代码自动生成工具，没有MyBatis生成的那些多余的

### 使用说明
分2个版本：` 1.2.8 `只支持 ` MySql `，` 1.2.6 `支持 ` MySql `与 ` Oracle `

#### 1.2.8版本
` 1.2.8 `只支持 ` MySql `，导包
```
<plugin>
    <groupId>io.github.chichengyu</groupId>
    <artifactId>model-generator-maven-plugin</artifactId>
    <version>1.2.8</version>
    <configuration>
        <!-- 输出文件目录,不用改 -->
        <path>${basedir}/src/main/java/</path>
        <!-- 包名 -->
        <packageName>com.demo.pojo</packageName>
        <!-- 实体后缀,可选,如：TbUserPojo、TbUserEntity -->
        <suffix>Entity</suffix>
        <!-- model名称,可选,如 TbUserDao、TbUserMapper(最好首字母大写,然后改包名称首字母小写) -->
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

#### 1.2.6版本
` 1.2.6 `支持 ` MySql `与 ` Oracle `，导包
```
<plugin>
    <groupId>io.github.chichengyu</groupId>
    <artifactId>model-generator-maven-plugin</artifactId>
    <version>1.2.6</version>
    <configuration>
        <!-- 输出文件目录,不用改 -->
        <path>${basedir}/src/main/java/</path>
        <!-- 包名 -->
        <packageName>com.demo.pojo</packageName>
        <!-- 实体后缀,可选,如：TbUserPojo、TbUserEntity -->
        <suffix>Entity</suffix>
        <!-- model名称,可选,如 TbUserDao、TbUserMapper(最好首字母大写,然后改包名称首字母小写) -->
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
导包之后，会报错
```
Could not find artifact com.oracle:ojdbc6:pom:11.2.0.1.0 in alimaven (http://maven.aliyun.com/nexus/content/groups/public/)
```
因为` oracle `是收费的，所以中央仓库没有这个包，需要手动去[下载安装](#可能问题(如果本地安装了oracle,直接找到包ojdbc6.jar进行安装))  

然后一切就绪后，就直接在idea右边侧边栏，选中 `maven -> Plugins -> model-generator -> model-generator:model`(执行`model-generator:help`，可看到所有配置参数),执行完成后，可以到包下已经生成对应的java文件了。

#### 可能问题(如果本地安装了oracle,直接找到包ojdbc6.jar进行安装)

导包之后，如果你的数据库是 oracle ，由于 oracle 是收费的，所以可能下载不了驱动jar包，需要手动去下载本地安装，这里我准备一个下载地址

 - 链接：https://share.weiyun.com/4V67i2fT 密码：66dg96

下载完成,放到一个你知道的位置，比如我放在D:/盘,执行安装命令
```
# 注意：只需要修改路径 -Dfile=/D:/ojdbc6.jar ,后面的不需要修改
mvn install:install-file -Dfile=/D:/ojdbc6.jar -DgroupId=com.oracle -DartifactId=ojdbc6 -Dversion=11.2.0.1.0 -Dpackaging=jar -DgeneratePom=true
```