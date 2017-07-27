package com.yyuap.mkb.cbo;

public class Tenant {

    private String tid = "";
    private String tusername = "";
    private String apiKey = "";

    private String dbip = "";
    private String dbport = "";
    private String dbname = "";

    private String dbusername = "";
    private String dbpassword = "";

    private String botKey;
    private String tkbcore;
    private String tname;

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
    public String gettusername() {
        return this.tusername;
    }

    public void settusername(String tusername) {
        this.tusername = tusername;
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
}
