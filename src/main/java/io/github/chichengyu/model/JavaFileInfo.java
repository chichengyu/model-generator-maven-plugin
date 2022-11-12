package io.github.chichengyu.model;

/**
 * Java文件信息
 * author xiaochi
 * Date 2022/11/12
 */
public class JavaFileInfo {

    /**
     * 文件名
     */
    private String fileName;

    /**
     * 文本内容
     */
    private String text;

    private JavaFileInfo() {}

    public static JavaFileInfo create() {
        return new JavaFileInfo();
    }

    public String getFileName() {
        return fileName;
    }

    public JavaFileInfo setFileName(String fileName) {
        this.fileName = fileName;
        return this;
    }

    public String getText() {
        return text;
    }

    public JavaFileInfo setText(String text) {
        this.text = text;
        return this;
    }

    @Override
    public String toString() {
        return "JavaFileInfo{" +
                "fileName='" + fileName + '\'' +
                ", text='" + text + '\'' +
                '}';
    }
}
