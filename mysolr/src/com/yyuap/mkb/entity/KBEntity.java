package com.yyuap.mkb.entity;

public class KBEntity {

    private String id = "";
    private String createTime = "";
    private String updateTime = "";
    private String createBy = "";
    private String updateBy = "";

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCreateTime() {
        return this.createTime;
    }

    public void setCreateTime(String time) {
        this.createTime = time;
    }

    public String getUpdateTime() {
        return this.updateTime;
    }

    public void setUpdateTime(String time) {
        this.updateTime = time;
    }

    public String getCreateBy() {
        return this.createBy;
    }

    public void setCreateBy(String by) {
        this.createBy = by;
    }

    public String getUpdateBy() {
        return this.updateBy;
    }

    public void setUpdateBy(String by) {
        this.updateBy = by;
    }
}
