package com.yyuap.mkb.cbo;

import com.alibaba.fastjson.JSONObject;

public class TenantInfo {

	private String id;
    private String tid = "";
    private String tname = "";

    private String apiKey = "";
    
    private String tusername = "";
    private String tpassword = "";

    private String dbip = "";
    private String dbport = "";
    private String dbname = "";

    private String dbusername = "";
    private String dbpassword = "";

    private String botKey;
    private String tkbcore;
    private String tdescript;
    private String tqakbcore;
    private float simscore = 0.618f;
    private boolean recommended = true;
    private String solr_qf;
    private String solr_sort;
    private boolean useSynonym;
    private boolean sorl_useFilterQueries = false;
    private String botSkillConfig;
    private JSONObject botSkillConfig_json;

    public TenantInfo() {

    }
    
    

	public String getId() {
		return id;
	}



	public void setId(String id) {
		this.id = id;
	}



	public String getTid() {
		return tid;
	}

	public void setTid(String tid) {
		this.tid = tid;
	}

	public String getTname() {
		return tname;
	}

	public void setTname(String tname) {
		this.tname = tname;
	}

	public String getApiKey() {
		return apiKey;
	}

	public void setApiKey(String apiKey) {
		this.apiKey = apiKey;
	}

	public String getTusername() {
		return tusername;
	}

	public void setTusername(String tusername) {
		this.tusername = tusername;
	}

	public String getTpassword() {
		return tpassword;
	}

	public void setTpassword(String tpassword) {
		this.tpassword = tpassword;
	}

	public String getDbip() {
		return dbip;
	}

	public void setDbip(String dbip) {
		this.dbip = dbip;
	}

	public String getDbport() {
		return dbport;
	}

	public void setDbport(String dbport) {
		this.dbport = dbport;
	}

	public String getDbname() {
		return dbname;
	}

	public void setDbname(String dbname) {
		this.dbname = dbname;
	}

	public String getDbusername() {
		return dbusername;
	}

	public void setDbusername(String dbusername) {
		this.dbusername = dbusername;
	}

	public String getDbpassword() {
		return dbpassword;
	}

	public void setDbpassword(String dbpassword) {
		this.dbpassword = dbpassword;
	}

	public String getBotKey() {
		return botKey;
	}

	public void setBotKey(String botKey) {
		this.botKey = botKey;
	}

	public String getTkbcore() {
		return tkbcore;
	}

	public void setTkbcore(String tkbcore) {
		this.tkbcore = tkbcore;
	}

	public String getTdescript() {
		return tdescript;
	}

	public void setTdescript(String tdescript) {
		this.tdescript = tdescript;
	}

	public String getTqakbcore() {
		return tqakbcore;
	}

	public void setTqakbcore(String tqakbcore) {
		this.tqakbcore = tqakbcore;
	}

	public float getSimscore() {
		return simscore;
	}

	public void setSimscore(float simscore) {
		this.simscore = simscore;
	}

	public boolean getRecommended() {
		return recommended;
	}

	public void setRecommended(boolean recommended) {
		this.recommended = recommended;
	}

	public String getSolr_qf() {
		return solr_qf;
	}

	public void setSolr_qf(String solr_qf) {
		this.solr_qf = solr_qf;
	}

	public String getSolr_sort() {
		return solr_sort;
	}

	public void setSolr_sort(String solr_sort) {
		this.solr_sort = solr_sort;
	}

	public boolean getUseSynonym() {
		return useSynonym;
	}

	public void setUseSynonym(boolean useSynonym) {
		this.useSynonym = useSynonym;
	}

	public boolean getSorl_useFilterQueries() {
		return sorl_useFilterQueries;
	}

	public void setSorl_useFilterQueries(boolean sorl_useFilterQueries) {
		this.sorl_useFilterQueries = sorl_useFilterQueries;
	}

	public String getBotSkillConfig() {
		return botSkillConfig;
	}

	public void setBotSkillConfig(String botSkillConfig) {
		this.botSkillConfig = botSkillConfig;
	}

	public JSONObject getBotSkillConfig_json() {
		return botSkillConfig_json;
	}

	public void setBotSkillConfig_json(JSONObject botSkillConfig_json) {
		this.botSkillConfig_json = botSkillConfig_json;
	}

	
   
}
