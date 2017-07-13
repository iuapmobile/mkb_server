/**
 * 
 */
package com.yyuap.mkb.entity;

import java.util.ArrayList;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * QaCollection
 * 
 * @author pengjf
 * @created 2017年7月13日18:00:33
 */
public class QaCollection extends KBEntity {
    /**
     * id
     */
    private String id = "";
    private String tenantid = "";
    private String userid = "";
    private String kbindexid = "";
    private String descript =  "";
    private String title =  "";
    private String url = "";
    private String qid = "";
    private String qsid = "";
    private String question = "";
    private String answer = "";
    
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

    public String getTenantid() {
		return tenantid;
	}

	public void setTenantid(String tenantid) {
		this.tenantid = tenantid;
	}

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public String getKbindexid() {
		return kbindexid;
	}

	public void setKbindexid(String kbindexid) {
		this.kbindexid = kbindexid;
	}

	public String getDescript() {
		return descript;
	}

	public void setDescript(String descript) {
		this.descript = descript;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getQid() {
		return qid;
	}

	public void setQid(String qid) {
		this.qid = qid;
	}

	 public String getQsid() {
		return qsid;
	}

	public void setQsid(String qsid) {
		this.qsid = qsid;
	}


	public JSONObject toJSON() {
        JSONObject json = new JSONObject();
        json.put("id", this.getId());
        json.put("tenantid", this.getTenantid());
        json.put("userid", this.getUserid());
        json.put("kbindexid", this.getKbindexid());
        json.put("title", this.getTitle());
        json.put("url", this.getUrl());
        json.put("qid", this.getQid());
        json.put("qsid", this.getQsid());
        json.put("question", this.getQuestion());
        json.put("answer", this.getAnswer());
        json.put("createTime", this.getCreateTime());
        json.put("updateTime", this.getUpdateTime());
        return json;
    }
}
