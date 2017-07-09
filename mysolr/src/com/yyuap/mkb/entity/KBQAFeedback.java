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
public class KBQAFeedback extends KBEntity {
    /**
     * id
     */
    private String id = "";
    private String question = "";
    private String answer = "";
    private String score;
   

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    
    public JSONObject toJSON() {
        JSONObject json = new JSONObject();
        json.put("id", this.getId());
        json.put("score", this.getScore());
    
        
        return json;
    }

    public String getScore() {
        // TODO Auto-generated method stub
        return this.score;
    }
    
    public void setScore(String score) {
        // TODO Auto-generated method stub
        this.score = score;
    }
}
