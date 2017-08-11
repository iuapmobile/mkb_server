package com.yyuap.mkb.processor;

import javax.servlet.http.HttpServletRequest;

import com.alibaba.fastjson.JSONObject;

public class MKBSession {
    
    public MKBSession(HttpServletRequest req){
        
        
        JSONObject obj = new JSONObject();
        req.getSession().setAttribute("mkb_syn", obj);
    }

}
