package com.yyuap.mkb.pl;

import java.sql.SQLException;

import com.alibaba.fastjson.JSONObject;

public class KBDuplicateSQLException extends KBSQLException {

    private JSONObject extData = new JSONObject();

    // private final int EXCEPTIONCODE = 1062;
    public KBDuplicateSQLException(String reason) {

        super(reason);
        super.kbExceptionCode = 1062;
    }

    public void put(String key, String value) {
        extData.put(key, value);
    }

    public JSONObject getExtData() {
        return this.extData;
    }

    public void setExtData(JSONObject json) {
        this.extData = json;
    }

    public void setId(String id) {
        this.extData.put("id", id);
    }

    public String getId() {
        return this.extData.getString("id");
    }

    
}
