package com.yyuap.mkb.processor;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import com.alibaba.fastjson.JSONObject;
import com.yyuap.mkb.cbo.Tenant;
import com.yyuap.mkb.entity.KBSynonym;
import com.yyuap.mkb.log.MKBLogger;
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
            obj = null;
            if (obj == null) {
                list = this.setSynonym2Session(request, tenant);
            } else {
                list = (ArrayList<KBSynonym>) obj;
            }

        } catch (Exception e) {
            MKBLogger.info("getSynonym failed! tname=" + tenant.gettname() + ", apiKey=" + tenant.gettAPIKey());
        }
        return list;
    }

    public String findKeywordFromSynonym(String q, HttpServletRequest request, Tenant tenant) {
        try {
            ArrayList<KBSynonym> list = this.getSynonym(request, tenant);
            ArrayList<String> retKW = new ArrayList<String>();
            JSONObject replaces = new JSONObject();
            // 分两步走
            // 1、对q对整体处理，例如iuap design iuap mobile MA MDM ==> 前端技术平台 移动平台 MA MDM
            for (KBSynonym kbs : list) {
                String[] synwords = kbs.getSynonym().split(",");
                for (String synword : synwords) {
                    if (q.contains(synword)) {
                        retKW.add(kbs.getKeyword());
                        String word = replaces.getString(synword);
                        if (word == null) {
                            word = kbs.getKeyword();
                        } else {
                            word += " " + kbs.getKeyword();
                        }
                        replaces.put(synword, word);
                        break;
                        // q = q.replace(synword, kbs.getKeyword());
                    } else {

                    }
                }
            }

            for (String key : replaces.keySet()) {
                q = q.replace(key, replaces.getString(key));
            }

            // 2、以空格分离后的同义词处理
            // iuap design iuap mobile MA MDM ==> 前端技术平台 移动平台 MA MDM
            // ==>前端技术平台 移动平台 移动支撑平台 移动设备管理 主数据管理(MDM)
            ArrayList<String> ret = new ArrayList<String>();
            String[] q_array = q.trim().split(" ");

            for (int i = 0, len = q_array.length; i < len; i++) {
                String word = q_array[i];
                if (word == null || word.equals("")) {
                    continue;
                }
                boolean has = false;
                for (KBSynonym kbs : list) {
                    String xxx = kbs.getSynonym();
                    if (xxx.toLowerCase().contains(word.trim().toLowerCase())) {
                        String kwd = kbs.getKeyword().trim();
                        ret.add(kwd);
                        has = true;
                    }
                }
                if (!has) {
                    ret.add(word);
                }
            }

            // 重新形成同义词后的q
            String new_q = "";
            // for (int i = 0, len = ret.size(); i < len; i++) {
            // String str = ret.get(i);
            // if (i == 0) {
            // new_q = str;
            // } else {
            // new_q += " " + str;
            // }
            // }
            new_q = String.join(" ", ret.toArray(new String[ret.size()]));
            return new_q;

        } catch (Exception e) {
            MKBLogger.info("getSynonym failed! tname=" + tenant.gettname() + ", apiKey=" + tenant.gettAPIKey());
        }

        return null;
    }
}
