package com.yyuap.mkb.processor;

import java.util.ArrayList;
import java.util.regex.Pattern;

import com.alibaba.fastjson.JSONObject;

public class IntentPredictionManager {

    public JSONObject predictIntent(JSONObject param) {
        JSONObject ret = null;
        if (param == null)
            return null;
        String text = param.getString("q");
        boolean ok = false;
        if ((text.contains("呼") && !text.endsWith("呼")) || (text.contains("呼叫") && !text.endsWith("呼叫"))) {
            ok = true;
        } else if (text.contains("打电话")) {
            ok = true;
        } else if (text.contains("打手机")) {
            ok = true;
        } else if (text.contains("拨")
                && (text.contains("电话") || text.contains("手机") || text.contains("号码") || text.contains("phone"))) {
            ok = true;
        } else if (text.contains("打")
                && (text.contains("电话") || text.contains("手机") || text.contains("号码") || text.contains("phone"))) {
            ok = true;
        } else if (text.contains("call")
                && (text.contains("电话") || text.contains("手机") || text.contains("号码") || text.contains("phone"))) {
            ok = true;
        }

        if (ok) {
            ret = predictPhone(text);
        } else {
            // 调用罗鹏场景会话服务
            ret = predictIntentSession(text);
        }
        return ret;
    }

    private JSONObject predictIntentSession(String text) {
        JSONObject ret = null;
        // 调用服务获取返回值

        // ret = new JSONObject();
        // ret.put("name", name);
        // // ret.put("action", "callphone");
        // ret.put("sessionid", "xxxxxxxx");
        // ret.put("key", "xxxxxxx");
        // ret.put("text", "正在给" + name + "打电话");
        // ret.put("code", 9001);

        return ret;
    }

    private JSONObject predictPhone(String text) {
        JSONObject ret = null;
        ArrayList<String> regList = new ArrayList<String>();
        regList.add("((?<=(呼叫[一]?下))[^(吧|啊|呀|呗|哦)\\?(打)\\?(个)\\?电话]+)");
        regList.add("((?<=(呼叫))[^(吧|啊|呀|呗|哦)\\?(打)\\?(个)\\?电话]+)");
        regList.add("((?<=(呼))[^个电话]+)");
        // regList.add("((?<=(呼叫))[^(吧|啊|呀)\\?]+)");
        regList.add("((?<=(呼叫))[个\\?|打\\?]+)");
        regList.add("((?<=(呼叫))[^吧|电话]+)");
        regList.add("((?<=(呼叫))[^个电话\\?]+)");
        regList.add("((?<=(呼叫))[^(的手机)?打|拨打.*电话\\?]+)");

        regList.add("((?<=(给))[^(的手机)\\?打|拨打.*电话\\?(吧)\\?]+)");
        // regList.add("((?<=(拨打))[^(的电话|的手机|手机|电话)\\+]\\+)");
        regList.add("((?<=(拨打))[^(电话|手机|的手机|的电话)\\?]+)");
        regList.add("((?<=(拨|打))[^(电话|手机|的手机|的电话)\\+]+)");
        regList.add("((?<=(拨|打))[^(电话|手机|的手机|的电话)\\+]+(给)\\?)");
        regList.add("((?<=(拨|打))[^(电话|手机|的手机|的电话)\\+]+)");

        regList.add("((?<=(呼|打))[^(电话|手机|的手机|的电话)\\?]?)");

        String name = "";
        for (int i = 0, len = regList.size(); i < len; i++) {
            String reg = regList.get(i);
            Pattern pattern = Pattern.compile(reg);
            String newtext = characterFormat(text);
            java.util.regex.Matcher m = pattern.matcher(newtext);
            boolean has = false;
            while (m.find()) {

                name = m.group();
                if (name.length() == 0) {
                    System.out.println(text + "*****************非打电话场景");
                    System.out.println("");
                    continue;
                }
                System.out.println(text + "====>" + name + "                 reg" + i + ":" + reg);// 输出
                has = true;
            }
            if (has) {
                break;
            }
        }

        if (name != null && !name.equals("")) {
            ret = new JSONObject();
            ret.put("name", name);
            ret.put("action", "callphone");
            ret.put("text", "正在给" + name + "打电话");
            ret.put("code", 9001);
        }
        return ret;
    }

    private static String characterFormat(String s) {
        String str = s.replaceAll("[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……& amp;*（）——+|{}【】‘；：”“’。，、？|-]", "");
        return str;
    }
}
