package com.yyuap.mkb.processor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import com.alibaba.fastjson.JSONObject;
import com.yyuap.mkb.socialChatBot.MKBHttpClient;

public class IntentPredictionManager {
    // private static final String URL =
    // "https://ia.yonyoucloud.com/api/luis/v1/analysis/";
    // private static final String DEVELOP_KEY =
    // "d49f0e51f69741c78efe840eb2535cce";

    private static final String PREDICTPHONE = "PREDICTPHONE";

    private static final String SCENE = "scene";
    private static final String SCENE_URL = "url";
    private static final String SCENE_DEVELOP_KEY = "DEVELOP_KEY";

    public JSONObject predictIntent(JSONObject conf, String text, String dialog) {
        JSONObject ret = null;
        boolean ok = false;
        String phone = conf.getString("PREDICTPHONE");
        if (phone != null) {
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
        }
        if (ok) {
            ret = predictPhone(text);
        } else {
            ret = predictSceneDialog(conf, text, dialog);
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

    private JSONObject predictSceneDialog(JSONObject conf, String text, String dialog) {
        JSONObject scene = conf.getJSONObject(this.SCENE);
        String URL = scene.getString(this.SCENE_URL);
        String DEVELOP_KEY = scene.getString(this.SCENE_DEVELOP_KEY);

        JSONObject ret = null;
        String content = "";
        MKBHttpClient httpclient = new MKBHttpClient();
        Map<String, String> createMap = new HashMap<String, String>();
        createMap.put("s", text);
        createMap.put("dilog", dialog);
        String charset = "utf-8";
        String ContentType = "application/x-www-form-urlencoded";
        String result = httpclient.doHttpPost(URL, createMap, charset, ContentType, DEVELOP_KEY);
        JSONObject json = (JSONObject) JSONObject.parse(result);
        
        String status = json.get("status") == null ? "0" : json.get("status").toString();
        String msg = json.get("msg") == null ? "" : json.get("msg").toString();
        if ("1".equals(status)) {
            // if(sceneLocal.get()!=null&&!"".equals(sceneLocal.get())){
            // ret = new JSONObject();
            // ret.put("name", msg);
            // ret.put("text", msg);
            // ret.put("status", status);
            // ret.put("code", 9002);
            // }else{
            // sceneLocal.set(json.get("status")==null?"0":json.get("status").toString());
            ret = new JSONObject();
            ret.put("name", msg);
            ret.put("text", msg);
            //ret.put("status", status);
            //ret.put("dilog", json.get("dilog") == null ? "" : json.get("dilog").toString());
            //ret.put("code", 9001);
            ret.put("scene", json);
            // }
        } else if ("2".equals(status)) {
            ret = new JSONObject();
            ret.put("name", msg);
            ret.put("text", msg);
            //ret.put("status", status);
            //ret.put("dilog", json.get("dilog") == null ? "" : json.get("dilog").toString());
            //ret.put("code", 9002);
            ret.put("scene", json);
            //ret = json;
        } else {
            ret = null;
        }

        return ret;
    }

    private static String characterFormat(String s) {
        String str = s.replaceAll("[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……& amp;*（）——+|{}【】‘；：”“’。，、？|-]", "");
        return str;
    }

    public static void main(String[] args) {

        String strConf = "{\"scene\":\"url\":\"https://ia.yonyoucloud.com/api/luis/v1/analysis/\",\"DEVELOP_KEY\":\"d49f0e51f69741c78efe840eb2535cce\"}}";
        JSONObject conf = JSONObject.parseObject(strConf);
        IntentPredictionManager a = new IntentPredictionManager();
        a.predictSceneDialog(conf, "案例", "f254fa82f9e248309de21794627318e1");
    }
}
