package com.yyuap.mkb.processor;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import com.alibaba.fastjson.JSONObject;
import com.yyuap.mkb.cbo.Tenant;
import com.yyuap.mkb.entity.KBSynonym;
import com.yyuap.mkb.pl.DBManager;

public class MKBSessionManager {
    public static final String MKB_SYN = "mkb_syn";

    public ArrayList<KBSynonym> setSynonym2Session(HttpServletRequest req, Tenant tenant) {

        DBManager dbmgr = new DBManager();
        ArrayList<KBSynonym> list = dbmgr.selectSynonym(tenant);

        req.getSession().setAttribute(MKBSessionManager.MKB_SYN, list);
        return list;
    }

    public ArrayList<KBSynonym> getSynonym(HttpServletRequest request, Tenant tenant) {
        ArrayList<KBSynonym> list = null;
        try {
            Object obj = request.getSession().getAttribute(MKBSessionManager.MKB_SYN);

            if (obj == null) {
                list = this.setSynonym2Session(request, tenant);
            } else {
                list = (ArrayList<KBSynonym>) obj;
            }

        } catch (Exception e) {
            System.out.println("getSynonym failed! tname=" + tenant.gettname() + ", apiKey=" + tenant.gettAPIKey());
        }
        return list;
    }

    public String findKeywordFromSynonym(String q, HttpServletRequest request, Tenant tenant) {
        try {
            ArrayList<KBSynonym> list = this.getSynonym(request, tenant);

            String[] q_array = q.trim().split(" ");

            for (int i = 0, len = q_array.length; i < len; i++) {
                String word = q_array[i];

                boolean has = false;
                for (KBSynonym kbs : list) {
                    String xxx = kbs.getSynonym();
                    if (xxx.contains(word.trim())) {
                        String ret = kbs.getKeyword().trim();
                        q_array[i] = ret;
                        break;
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("getSynonym failed! tname=" + tenant.gettname() + ", apiKey=" + tenant.gettAPIKey());
        }
        return null;
    }
}
