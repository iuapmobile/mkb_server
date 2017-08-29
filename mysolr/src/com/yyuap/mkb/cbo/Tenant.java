package com.yyuap.mkb.cbo;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

public class Tenant {

    private String tid = "";
    private String tusername = "";
    private String tpassword = "";
    private String apiKey = "";

    private String dbip = "";
    private String dbport = "";
    private String dbname = "";

    private String dbusername = "";
    private String dbpassword = "";

    private String botKey;
    private String tkbcore;
    private String tname;
    private String tdescript;
    private String tqakbcore;
    private float _simscore = 0.618f;
    private boolean _recommended = true;
    private String _solr_qf;
    private String _solr_sort;
    private boolean _useSynonym;
    private boolean _sorl_useFilterQueries = false;
    private String _botSkillConfig;
    private JSONObject _botSkillConfig_json;

    public Tenant() {

    }

    public String gettid() {
        return this.tid;
    }

    public void settid(String id) {
        this.tid = id;
    }

    public String gettname() {
        return this.tname;
    }

    public void settname(String tname) {
        this.tname = tname;
    }
    
    public String getTdescript() {
		return tdescript;
	}

	public void setTdescript(String tdescript) {
		this.tdescript = tdescript;
	}

	public String gettusername() {
        return this.tusername;
    }

    public void settusername(String tusername) {
        this.tusername = tusername;
    }
    

    public String getTpassword() {
		return tpassword;
	}

	public void setTpassword(String tpassword) {
		this.tpassword = tpassword;
	}

	public String gettAPIKey() {
        return this.apiKey;
    }

    public void setAPIKey(String apikey) {
        this.apiKey = apikey;
    }

    public String getdbip() {
        return this.dbip;
    }

    public void setdbip(String ip) {
        this.dbip = ip;
    }

    public String getdbport() {
        return this.dbport;
    }

    public void setdbport(String port) {
        this.dbport = port;
    }

    public String getdbname() {
        return this.dbname;
    }

    public void setdbname(String dbname) {
        this.dbname = dbname;
    }

    public String getdbpassword() {
        return this.dbpassword;
    }

    public void setdbpassword(String psw) {
        this.dbpassword = psw;
    }

    public String gettkbcore() {
        return this.tkbcore;
    }

    public void settkbcore(String kbcore) {
        this.tkbcore = kbcore;
    }

    public String gettqakbcore() {
        return this.tqakbcore;
    }

    public void settqakbcore(String qakbcore) {
        this.tqakbcore = qakbcore;
    }

    public String getbotKey() {
        return this.botKey;
    }

    public void setbotKey(String key) {
        this.botKey = key;
    }

    public String getdbusername() {
        // TODO Auto-generated method stub
        return this.dbusername;
    }

    public void setdbusername(String username) {
        // TODO Auto-generated method stub
        this.dbusername = username;
    }

    public float getSimscore() {
        return this._simscore;
    }

    public void setSimscore(float value) {
        this._simscore = value;
    }

    public boolean getRecommended() {
        return this._recommended;
    }

    public void setRecommended(boolean value) {
        this._recommended = value;
    }

    public String getSolr_qf() {
        return this._solr_qf;
    }

    public void setSolr_qf(String value) {
        this._solr_qf = value;
    }

    public String getSolr_sort() {
        return this._solr_sort;
    }

    public void setSolr_sort(String value) {
        this._solr_sort = value;
    }

    public boolean getUseSynonym() {
        return this._useSynonym;
    }

    public void setUseSynonym(boolean value) {
        this._useSynonym = value;
    }

    public boolean getSorl_useFilterQueries() {
        return this._sorl_useFilterQueries;
    }

    public void setSorl_useFilterQueries(boolean value) {
        this._sorl_useFilterQueries = value;
    }

    public String getBotSkillConfig() {
        return this._botSkillConfig;
    }

    public JSONObject getBotSkillConfigJSON() {
        if (this._botSkillConfig_json == null) {
            this._botSkillConfig_json = JSON.parseObject(this._botSkillConfig);
        }
        return this._botSkillConfig_json;
    }

    public void setBotSkillConfig(String value) {
        this._botSkillConfig = value;
    }
}
