package com.yyuap.mkb.test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Calendar;

import org.apache.commons.httpclient.HttpException;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yyuap.mkb.entity.KBINDEXTYPE;
import com.yyuap.mkb.log.MKBLogger;
import com.yyuap.mkb.nlp.OpenAPI;

public class simnet {

    public static void test() {

        zaoju();
        if (1 == 1) {
            return;
        }

        // String q1 = "我想请假";
        String q1 = "我想请假一天，有点发烧，";
        ArrayList<String> yuliao = new ArrayList();
        yuliao.add("我的孩子病了");
        yuliao.add("我的孩子病了，我想请假两天");
        yuliao.add("我明天要去接我亲戚");
        yuliao.add("我明天要去接我亲戚，我想请假一天");
        yuliao.add("我明天要陪我妈去看病");
        yuliao.add("我明天要陪我妈去看病，想请假一天");

        yuliao.add("我发烧了");
        yuliao.add("我拉肚子了");
        yuliao.add("我摔了一脚");
        yuliao.add("我出车祸了");
        yuliao.add("我有点私事");
        yuliao.add("我哥哥来了，我要接待一下");

        yuliao.add("我拉肚子了，想请假一天");
        yuliao.add("我发烧了，想请假两天");
        yuliao.add("我发烧了，想请假");
        yuliao.add("我发烧了，想休息2天");
        yuliao.add("我有点发烧了，想请假");
        yuliao.add("我发烧了，想请假两天");
        yuliao.add("我想请假两天，发烧了");
        yuliao.add("我发烧了，想请假两天，请批准");
        yuliao.add("我发高烧了，想请假一天");
        yuliao.add("我发烧了，要去医院，想请假两天");
        yuliao.add("我发烧了，下不了床，想请假两天");
        yuliao.add("我发烧了，想请一下假");
        yuliao.add("我发烧了，估计没法上班，想请假两天");
        yuliao.add("我发烧了，没法去了，想请假两天");
        yuliao.add("我发烧了，估计明天没法上班了，想请假两天");

        float score = -1;
        ArrayList<JSONObject> result = new ArrayList<JSONObject>();

        try {
            for (String q2 : yuliao) {

                score = similar(q1, q2);
                JSONObject json = new JSONObject();
                json.put("q", q1);
                json.put("x", q2);
                json.put("score", score);
                json.put("d", "我发烧了");
                result.add(json);

            }
        } catch (HttpException e) {
            // TODO Auto-generated catch block
            MKBLogger.error("Exception:" + e.toString());
        } catch (IOException e) {
            // TODO Auto-generated catch block
            MKBLogger.error("Exception:" + e.toString());
        }

        print(result);

    }

    public static void zaoju() {
        JSONArray dataSource = new JSONArray();
        // File file = new File("/Users/gct/work2/zaoju.txt");
        String filePath = "/Users/gct/work2/请假原因_" + Calendar.getInstance().getTimeInMillis() + ".xlsx";
        File file = new File(filePath);
        PrintStream ps;
        try {
            ps = new PrintStream(new FileOutputStream(file));

            // ps.println(txt);// 往文件里写入字符串

            ArrayList<String> zhu_yu = zhuyu();
            ArrayList<String> gaun_xi = guanxi();
            JSONArray wei_yu = weiyu();

            ArrayList<String> bu_yu = buyu();
            ArrayList<String> mu_di = mudi();

            int count = 0;
            if (1 == 0) {
                JSONObject json = new JSONObject();
                json.put("q", "我生病了，请假一天");
                json.put("a", "我生病了");
                dataSource.add(json);
            } else {

                for (String zhuyu0 : zhu_yu) {
                    for (String guanxi0 : gaun_xi) {
                        for (int i = 0, len = wei_yu.size(); i < len; i++) {
                            JSONObject weiyu = wei_yu.getJSONObject(i);
                            String wei_yu_reason1 = weiyu.getString("reason");
                            ArrayList<String> weiyuList = (ArrayList<String>) weiyu.get("weiyu");

                            for (String weiyu0 : weiyuList) {
                                for (String buyu0 : bu_yu) {
                                    for (String mudi0 : mu_di) {
                                        String q = zhuyu0 + guanxi0 + weiyu0 + buyu0 + mudi0;
                                        String reason = "";
                                        if (wei_yu_reason1.equals("生病了")) {
                                            if (guanxi0.equals("")) {
                                                reason = zhuyu0 + wei_yu_reason1;
                                            } else if (!guanxi0.equals("")) {
                                                reason = zhuyu0 + "家人" + wei_yu_reason1;
                                            }
                                        }else if (wei_yu_reason1.equals("家里有事")){
                                            reason = zhuyu0 +  wei_yu_reason1;
                                        }
                                        // MKBLogger.info(xxx);

                                        // ps.println(q + " 原因:" + reason);//
                                        // 往文件里写入字符串
                                        // ps.append(" 原因:" + reason);//
                                        // 在已有的基础上添加字符串

                                        JSONObject json = new JSONObject();
                                        json.put("q", q);
                                        json.put("a", reason);
                                        dataSource.add(json);

                                        count++;
                                    }
                                }
                            }
                        }
                    }
                }
            }
            writeExcelX(dataSource, filePath, KBINDEXTYPE.QA);
            MKBLogger.info("一共造句" + count);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            MKBLogger.error("Exception:" + e.toString());
        }
    }

    public static void writeExcelX(JSONArray dataSource, String filePath, KBINDEXTYPE type) {
        com.yyuap.mkb.fileUtil.ExcelXWriter xwriter = new com.yyuap.mkb.fileUtil.ExcelXWriter();

        try {
            xwriter.writeXlsx(dataSource, filePath, type);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            MKBLogger.error("Exception:" + e.toString());
        }
    }

    public static ArrayList<String> zhuyu() {

        ArrayList<String> result = new ArrayList<String>();
        result.add("我");

        return result;
    }

    public static ArrayList<String> guanxi() {

        ArrayList<String> result = new ArrayList<String>();
        result.add("");
        result.add("爸爸");
        result.add("妈妈");
        result.add("哥哥");
        result.add("姐姐");
        result.add("弟弟");
        result.add("妹妹");
        result.add("爷爷");
        result.add("奶奶");
        result.add("姥姥");
        result.add("姥爷");
        //result.add("叔叔");
        //result.add("姨");
        //result.add("姨夫");
        result.add("大舅");
        result.add("二舅");
        result.add("三舅");
        result.add("孩子");
        //result.add("闺女");
        result.add("小孩");
        //result.add("儿子");
        //result.add("女儿");
        result.add("老婆");
        result.add("媳妇");
        //result.add("对象");
        //result.add("妻子");
        //result.add("老伴");
        //result.add("内人");
        result.add("家人");
        //result.add("家里人");
        //result.add("亲人");
        //result.add("亲戚");
        //result.add("亲属");
        result.add("老人");
        result.add("父母");
        //result.add("爱人");
        //result.add("孩儿");
        return result;
    }

    public static JSONArray weiyu() {
        JSONArray ret = new JSONArray();

        JSONObject json = new JSONObject();
        json.put("reason", "生病了");
        json.put("weiyu", weiyu_shengbingle());
        ret.add(json);

        JSONObject json1 = new JSONObject();
        json1.put("reason", "家里有事");
        json1.put("weiyu", weiyu_jialiyoushi());
        ret.add(json1);

        return ret;

    }

    public static ArrayList<String> weiyu_shengbingle() {

        ArrayList<String> result = new ArrayList<String>();

        result.add("病了");
        result.add("得病了");
        result.add("生病了");
        result.add("身体不舒服");
        //result.add("发烧了");
        //result.add("感觉不舒服");

        result.add("发烧");
        //result.add("发高烧");
        result.add("有点发烧");

        result.add("拉肚子");
        //result.add("吃坏肚子");
        //result.add("肚子难受");
        result.add("肚子疼");
        //result.add("肠胃疼");
        //result.add("胃疼");
        //result.add("心口疼");

        //result.add("全身疼");
        //result.add("周身疼");
        //result.add("浑身疼");
        result.add("感觉不舒服");
        //result.add("浑身没劲");
        //result.add("难受的很");
        //result.add("很难受");

        result.add("摔跤了");
        result.add("摔倒了");
        //result.add("摔得挺严重");
        result.add("不小心摔了一跤");
        //result.add("崴脚了");
        //result.add("出车祸了");
        result.add("被撞了");
        //result.add("被车撞了");

        /*
         * result.add("去火车站接人"); result.add("去接人"); result.add("去车站接人");
         * result.add("去火车站接人"); result.add("去机场接人");
         * 
         * result.add("来找我"); result.add("来找我有点事");
         * 
         * 
         * result.add("要结婚"); result.add("要回家结婚"); result.add("要回老家结婚");
         * result.add("要办理结婚手续"); result.add("要举行婚礼"); result.add("要办婚宴");
         * result.add("要举办婚宴"); result.add("要回家办婚礼");
         */
     

        return result;
    }

    public static ArrayList<String> weiyu_jialiyoushi() {

        ArrayList<String> result = new ArrayList<String>();
        //result.add("去火车站接人");
        result.add("去接人");
        //result.add("去车站接人");
        //result.add("去机场接人");

        //result.add("办手续");
        result.add("买房子");
        result.add("房子过户");
        //result.add("签协议过户");

        result.add("要结婚");
        result.add("要回家结婚");
        result.add("要回老家结婚");
        result.add("要办理结婚手续");
        result.add("要举行婚礼");
        //result.add("要办婚宴");
        //result.add("要举办婚宴");
        result.add("要回家办婚礼");

        //result.add("有点私事");
        //result.add("有点事");
        //result.add("办事");
        result.add("有事");

        return result;
    }

    public static ArrayList<String> buyu() {
        ArrayList<String> result = new ArrayList<String>();
        result.add("");
        //result.add("想在家休息一天");
        result.add("想在家休息");
        result.add("没法工作");
        //result.add("没法下床");
        //result.add("没法起来");
        result.add("没法上班");
        //result.add("没法动身");
        //result.add("没法动");
        result.add("要去医院看病");
        //result.add("要去医院");
        result.add("去医院");
       // result.add("去趟医院");
        result.add("去看医生");
        //result.add("去让医生看病");
        result.add("去看病");
        //result.add("去看看医生");
        //result.add("去医院看看");
        //result.add("去医院检查身体");
        //result.add("去医院做检查");
        result.add("去医院检查");
        //result.add("去检查");
        //result.add("去医院开药");
        //result.add("去医院看医生");

        return result;
    }

    public static ArrayList<String> mudi() {

        ArrayList<String> result = new ArrayList<String>();

        result.add("");
        result.add("想请假一天");
        result.add("想请假两天");
        result.add("想请假三天");
        result.add("想请假几天");
        result.add("想请几天假");
        result.add("想请个假");
        result.add("想请假");

        result.add("请假一天");
        result.add("请假两天");
        result.add("请假三天");
        result.add("请假几天");
        result.add("请几天假");
        result.add("请个假");
        result.add("请假");
        result.add("请假");

        return result;
    }

    public static void print(ArrayList<JSONObject> result) {
        for (JSONObject obj : result) {
            String q1 = obj.getString("q");
            String q2 = obj.getString("x");
            String q3 = obj.getString("d");
            String score = obj.getString("score");

            MKBLogger.info(">>>>>>>>>>>>>>>>>>>>>");
            MKBLogger.info("用户问:" + q1);
            MKBLogger.info("预置问:" + q2);
            MKBLogger.info("原因:" + q3);
            MKBLogger.info(score);
            MKBLogger.info("<<<<<<<<<<<<<<<<<<<<<");

        }
    }

    public static float similar(String q1, String q2) throws HttpException, IOException {
        //BaiAdapter bai = new BaiAdapter();
        float score = OpenAPI.simnet(q1, q2);
        return score;
    }

    public void WriteStringToFile(String filePath, String txt) {
        try {
            File file = new File(filePath);
            PrintStream ps = new PrintStream(new FileOutputStream(file));
            ps.println(txt);// 往文件里写入字符串
            // ps.append(txt);// 在已有的基础上添加字符串
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            MKBLogger.error("Exception:" + e.toString());
        }
    }
}
