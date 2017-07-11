/**
 * 
 */
package com.yyuap.mkb.entity;

import java.util.ArrayList;

import com.alibaba.fastjson.JSONObject;

/**
 * KBQA
 * 
 * @author gct
 * @created 2017-5-20
 */
public class KBQA extends KBEntity {
    /**
     * id
     */
    private String id = "";
    private String question = "";
    private String answer = "";
    private String qtype = "";
    private String[] questions = null;
    private String libraryPk = null;

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

    public String getAnswer() {
        return this.answer;
    }

    public void setAnswer(String a) {
        this.answer = a;
    }

    public String getQtype() {
        return this.qtype;
    }

    public void setQtype(String t) {
        this.qtype = t;
    }

    public String[] getQuestions() {
        return this.questions;
    }

    public void setQuestions(String[] qs) {
        this.questions = qs;
    }

    public JSONObject toJSON() {
        JSONObject json = new JSONObject();
        json.put("id", this.getId());
        json.put("question", this.getQuestion());
        json.put("answer", this.getAnswer());
        json.put("qtype", this.getQtype());
        json.put("createTime", this.getCreateTime());
        json.put("updateTime", this.getUpdateTime());
        return json;
    }

    public String getLibraryPk(){
        return this.libraryPk;
    }
    public void setLibraryPk(String libraryPk) {
       this.libraryPk = libraryPk;   
    }
}
