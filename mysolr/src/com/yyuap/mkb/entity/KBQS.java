/**
 * 
 */
package com.yyuap.mkb.entity;

import java.util.ArrayList;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * KBQA
 * 
 * @author gct
 * @created 2017-5-20
 */
public class KBQS extends KBEntity {
    /**
     * id
     */
    private String id = "";
    private String question = "";
    private String qid = "";
    private JSONArray qs;
    private String status;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getQuestion() {
        return this.question;
    }

    public void setQuestion(String q) {
        this.question = q;
    }

    public String getQid() {
        return this.qid;
    }

    public void setQid(String qid) {
        this.qid = qid;
    }

    public JSONObject toJSON() {
        JSONObject json = new JSONObject();
        json.put("id", this.getId());
        json.put("q", this.getQuestion());
        json.put("qid", this.getQid());

        json.put("createTime", this.getCreateTime());
        json.put("updateTime", this.getUpdateTime());
        return json;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return this.status;
    }

}
