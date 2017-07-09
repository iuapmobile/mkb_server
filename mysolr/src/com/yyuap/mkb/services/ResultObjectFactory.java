package com.yyuap.mkb.services;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class ResultObjectFactory {
    
    
    public ResultObjectFactory(){
        
    }
    public ResultObject create(int val){
        ResultObject ro = new ResultObject();
        ro.setStatus(val);
        return ro;
    }
      
}
