/**
 * 
 */
package com.yyuap.mkb.entity;

/**
 * KBIndex
 * 
 * @author gct
 * @created 2017-5-20
 */
public class KBIndex extends KBEntity {
    /**
     * id
     */
    private String id = "";
    private String title = "";
    private String descript = "";
    private String domain = "";
    private String keywords = "";

    private String tag = "";
    private String category = "";
    private String grade = "";
    private String descriptImg = "";
    private String url = "";

    private String author = "";
    private String createTime = "";
    private String updateTime = "";

    private String text = "";

    private String filePath = "";

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescript() {
        return descript;
    }

    public void setDescript(String descript) {
        this.descript = descript;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getKeywords() {
        return this.keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = domain;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String label) {
        this.tag = label;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getDescriptImg() {
        return descriptImg;
    }

    public void setDescriptImg(String descriptImg) {
        this.descriptImg = descriptImg;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getCreatTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String path) {
        this.filePath = path;
    }
}
