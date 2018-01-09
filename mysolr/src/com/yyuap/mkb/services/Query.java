package com.yyuap.mkb.services;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.solr.client.solrj.SolrServerException;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yyuap.mkb.cbo.CBOManager;
import com.yyuap.mkb.cbo.Tenant;
import com.yyuap.mkb.log.MKBLogger;
import com.yyuap.mkb.processor.IntentPredictionManager;
import com.yyuap.mkb.processor.MKBSessionManager;
import com.yyuap.mkb.processor.QAManager;
import com.yyuap.mkb.processor.SolrManager;
import com.yyuap.mkb.services.util.MKBRequestProcessor;
import com.yyuap.mkb.services.util.PropertiesUtil;
import com.yyuap.mkb.services.util.RedisUtil;
import com.yyuap.mkb.socialChatBot.EmotiBot;
import com.yyuap.mkb.socialChatBot.MKBHttpClient;
import com.yyuap.mkb.socialChatBot.TulingBot;

/**
 * Servlet implementation class mkbQuery
 */
@WebServlet("/mkbQuery")
public class Query extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final String BOTURL_TL = "http://www.tuling123.com/openapi/api";
    private static final String BOTAPIKEY_del = "f08c391260304acc81f8fdb27de44832";// 雪儿
    // private static final String BOTAPIKEY=
    // "301c3b5d56934e7b9248f7142fbad15d";

    /**
     * @see HttpServlet#HttpServlet()
     */
    public Query() {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
     *      response)
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // TODO Auto-generated method stub
        request.setCharacterEncoding("UTF-8");
        // 这句话的意思，是让浏览器用utf8来解析返回的数据
        response.setHeader("Content-type", "application/json;charset=UTF-8");
        // 这句话的意思，是告诉servlet用UTF-8转码，而不是用默认的ISO8859
        response.setCharacterEncoding("UTF-8");
        // response.setContentType("application/json");
        String content_type = request.getContentType();
        JSONObject requestParam = new JSONObject();

        MKBRequestProcessor rp = new MKBRequestProcessor();
        if (content_type != null && content_type.toLowerCase().indexOf("application/json") >= 0) {
            requestParam = rp.readJSON4JSON(request);
        } else {
            requestParam = rp.readJSON4Form_urlencoded(request);
        }

        String __debug = requestParam.getString("__debug");
        String q = requestParam.getString("q");
        String q_old = requestParam.getString("q");

        String strIsObserver = requestParam.getString("isObserver");
        boolean isObserver = false;
        if (strIsObserver != null && strIsObserver.toLowerCase().equals("true")) {
            isObserver = true;
        }
        String bot = requestParam.getString("bot");
        String apiKey = requestParam.getString("apiKey");
        String buserid = requestParam.getString("buserid");
        String userid = requestParam.getString("userid");
        if (userid == null) {
            // 不存在userid时，使用buserid，兼容老的使用buserid的调用
            userid = buserid;
        }

        String dialog = "";// 存储场景的dilog
        dialog = requestParam.getString("dailog");// 场景生成的会话id，客户端不应该关注
        String dialogid = requestParam.getString("dailogid");// bot的会话id
        if (dialogid != null && (!dialogid.equals(""))) {
            // 客户端有dialogid则从Redis中获取dilog
            dialog = RedisUtil.getString(dialogid);
        } else {
            // 第一次会生成一个dialogid
            dialogid = UUID.randomUUID().toString();
        }

        String[] tag = request.getParameterValues("tag");// tag: "personinside"
                                                         // //内部为personinside
                                                         // 其余为空
        requestParam.put("tag", tag);

        MKBLogger.info("query request Start! apiKey=" + apiKey);
        MKBLogger.error("test query error request! apiKey=" + apiKey);

        // Step1：获取租户信息
        Tenant tenant = this.getTenantInfo(apiKey);
        // 同义词转化
        String new_q = this.handleKeywordFromSynonym(q, request, tenant);
        if (new_q != null && !new_q.equals("")) {
            q = new_q;
            requestParam.put("q", q);
        }

        // Step2：开始处理
        ResultObject ro = this.createDefaultResultObject();
        if (tenant == null) {
            // 没有租户信息
            ro = this.setResultObject4TenantNoExist(ro, apiKey, q);
        } else {
            JSONObject uniqueQA = null;
            JSONObject predictIntentRet = null;
            // 0、预测
            try {
                JSONObject botConfig = tenant.getBotSkillConfigJSON();
                if (botConfig != null) {
                    // {"skills": {"intent": true,"prediction": true}}
                    JSONObject skills = botConfig.getJSONObject("skills");
                    boolean prediction = skills.getBooleanValue("prediction");
                    if (prediction) {
                        if (!isObserver) {
                            IntentPredictionManager intentMgr = new IntentPredictionManager();
                            JSONObject conf = tenant.getBot_api_ia_conf_JSON();
                            predictIntentRet = intentMgr.predictIntent(conf, q, dialog);
                            if (predictIntentRet == null) {
                                // 表明场景出错了，需要重置dilog状态
                                RedisUtil.setString(dialogid, "");
                            } else {
                                String sceneStatus = null;
                                String dilog = null;
                                JSONObject scene = predictIntentRet.getJSONObject("scene");
                                if (scene != null) {
                                    sceneStatus = scene.getString("status");
                                    dilog = scene.getString("dilog");
                                    if (dilog != null) {
                                        // Redis存储dialogid和dilog的对应关系
                                        RedisUtil.setString(dialogid, dilog);
                                        dialog = dilog;// 此时应该相等，除非中间有断，这句代码没什么用，dilog
                                    }
                                }
                                if (sceneStatus == null) {
                                    // 未指定的状态
                                } else if (sceneStatus.equals("1")) {
                                    // 1表示场景进行中
                                    predictIntentRet.put("dailogid", dialogid);
                                    predictIntentRet.put("kbid", "1");
                                    predictIntentRet.put("ktype", "scene");
                                    ro.setBotResponse(predictIntentRet);
                                    response.getWriter().write(ro.getResutlString());
                                    return;
                                } else if (sceneStatus.equals("2")) {
                                    // 2表示场景结束
                                    JSONObject sceneuserdata = predictIntentRet.getJSONObject("sceneuserdata");
                                    boolean returnAfterEnd = true;
                                    if (sceneuserdata != null) {
                                        returnAfterEnd = sceneuserdata.getBooleanValue("returnAfterEnd");// 下一步找罗鹏定义对接后续流程分支
                                    }
                                    if (returnAfterEnd) {
                                        // 场景会话结束后，直接返回，无需后续bot逻辑
                                        predictIntentRet.put("dailogid", dialogid);
                                        predictIntentRet.put("kbid", "1");
                                        predictIntentRet.put("ktype", "scene");// 场景结束，设置ktype为scene
                                        ro.setBotResponse(predictIntentRet);
                                        response.getWriter().write(ro.getResutlString());
                                        return;
                                    } else {
                                        String scenename = predictIntentRet.get("scenename").toString();
                                        String dataStr = "";
                                        JSONObject jsonObject = predictIntentRet.getJSONObject("data");
                                        // 遍历 ai系统返回参数中data data中 有我们需要的值 拼成
                                        // value1
                                        // value2 value3 中间空格 隔开
                                        for (Map.Entry<String, Object> entry : jsonObject.entrySet()) {
                                            MKBLogger.debug("Scene: returnAfterEnd==false, predictIntentRet的Key-Value:"
                                                    + entry.getKey() + ":" + entry.getValue());
                                            dataStr = dataStr + " " + entry.getValue();
                                        }
                                        dataStr = scenename + dataStr;
                                        q = dataStr;
                                        requestParam.put("q", q);
                                    }
                                } else {
                                    // 未处理的状态
                                    predictIntentRet.put("dailogid", dialogid);
                                    predictIntentRet.put("kbid", "1");
                                    predictIntentRet.put("ktype", "scene");
                                    ro.setBotResponse(predictIntentRet);
                                    response.getWriter().write(ro.getResutlString());
                                    return;
                                }

                            }
                        } else {
                            // 作为观察者，不需要进入场景，场景只有在对话接受方时才生效
                            // 但是，不代表机器人不会做出预测。场景仅仅是一种预测
                            // 日志记录：跳过场景
                        }
                    } // 预测结束

                    boolean intent = skills.getBooleanValue("intent");
                    if (intent) {
                        // 调用意图处理接口

                    }
                }
            } catch (Exception e) {
                MKBLogger.error("Exception:" + e.toString());
            } catch (Throwable a) {
                ro.setStatus(21000);
                ro.setReason(a.getMessage());
                response.getWriter().write(ro.getResutlString());
                return;
            }

            String id = null;
            // 1、查询唯一答案，根据相似性处理得出唯一答案
            QAManager qamgr = new QAManager();
            try {
                uniqueQA = qamgr.getUniqueAnswer(q, tenant, tag);
                if (uniqueQA != null && (!uniqueQA.getString(q).equals("") || !uniqueQA.getString("url").equals(""))) {
                    ro.setBotResponseKV("dailogid", dialogid);
                    id = uniqueQA.getString("id");
                    // processBotResponse(ro, uniqueQA,dailogid);
                }
            } catch (SolrServerException e1) {
                ro.setStatus(21000);
                ro.setReason(e1.getMessage());
                response.getWriter().write(ro.getResutlString());
                return;
            } catch (Exception e) {
                ro.setStatus(21000);
                ro.setReason(e.getMessage());
                response.getWriter().write(ro.getResutlString());
                return;
            } catch (Throwable a) {
                ro.setStatus(21000);
                ro.setReason(a.getMessage());
                response.getWriter().write(ro.getResutlString());
                return;
            }

            // 2、是否推荐，启用搜索引擎进行推荐

            boolean recommended = tenant.getRecommended();
            if (recommended) {
                try {
                    String corename = tenant.gettkbcore();
                    SolrManager solrmng = new SolrManager(corename);
                    JSONObject _ret = solrmng.query(requestParam, tenant, id);// 获取查询结果,，一个新的对象
                    ro.set(_ret);// 导致botResponse需要重新赋值
                } catch (Exception e) {
                    String log = "==========>推荐出错！q=" + q + " tname=" + tenant.gettname() + " apiKey=" + apiKey + " "
                            + e.toString();
                    MKBLogger.error(log);
                }
            }
            if (uniqueQA != null && (!uniqueQA.getString(q).equals("") || !uniqueQA.getString("url").equals(""))) {
                processBotResponse(ro, uniqueQA, dialogid);
            }

            // 3、没有唯一答案时，外接bot处理
            try {
                if (uniqueQA == null || (uniqueQA.getString("a").equals("") && uniqueQA.getString("url").equals(""))) {
                    if (predictIntentRet != null) {
                        // 场景有（命中）匹配返回值
                        JSONObject json = new JSONObject();
                        json.put("text", "您好，我找呀找还是没有找到您想要的内容！");
                        String _kbid = "-1";
                        if (uniqueQA != null) {
                            _kbid = uniqueQA.getString("kbid");
                        }
                        json.put("kbid", _kbid == null ? "1" : _kbid);
                        json.put("ktype", "qa");

                        json.put("dailogid", dialogid);// bot的会话id,dailogid会找到场景所需定dilog

                        ro.setBotResponse(json);
                    } else {
                        String bot_social_chatBot_enabled = PropertiesUtil
                                .getConfigPropString("bot_social_chatBot_enabled");
                        String bot_social_chatBot_v = PropertiesUtil.getConfigPropString("bot_social_chatBot_v");

                        boolean t_bot_social_chatBot_enabled = tenant.getBot_social_chatBot_enabled();
                        int t_bot_social_chatBot_v = tenant.getBot_social_chatBot_v();
                        if (t_bot_social_chatBot_v > 0) {
                            bot_social_chatBot_v = String.valueOf(t_bot_social_chatBot_v);
                        }

                        // 双enabled开关，属性文件开关控制整个系统，DB开关控制单个租户
                        if (bot_social_chatBot_enabled == null || bot_social_chatBot_enabled.equals("true")) {
                            if (t_bot_social_chatBot_enabled) {

                                if (bot_social_chatBot_v == null || bot_social_chatBot_v.equals("1")) {
                                    if (bot == null || !bot.equalsIgnoreCase("false")) {
                                        // JSONObject jsonTu =
                                        // this.tubot(this.BOTURL_TL,
                                        // tenant.getbotKey(), q, userid);
                                        // jsonTu.put("kbid", "0");//
                                        // 0表明不在用户业务kb范围之内
                                        // jsonTu.put("ktype", "qa");// 默认是qa的知识
                                        // jsonTu.put("dailogid", dialogid);
                                        //
                                        // jsonTu.put("bot_social_chatBot_botKey",
                                        // tenant.getbotKey());
                                        // jsonTu.put("bot_social_chatBot_enabled",
                                        // bot_social_chatBot_enabled);
                                        // jsonTu.put("bot_social_chatBot_v",
                                        // bot_social_chatBot_v);
                                        // ro.setBotResponse(jsonTu);

                                        Map<String, String> mapParms = new HashMap<String, String>();
                                        String tl_url = "http://www.tuling123.com/openapi/api";
                                        String key = tenant.getbotKey();
                                        mapParms.put("key", key);
                                        mapParms.put("userid", userid);
                                        mapParms.put("info", q);

                                        TulingBot tl = new TulingBot();
                                        JSONObject jsonTu = tl.chat(tl_url, mapParms, null);
                                        jsonTu.put("kbid", "0");// 0表明不在用户业务kb范围之内
                                        jsonTu.put("ktype", "qa");// 默认是qa的知识
                                        jsonTu.put("dailogid", dialogid);

                                        jsonTu.put("bot_social_chatBot_botKey", tenant.getbotKey());
                                        jsonTu.put("bot_social_chatBot_enabled", bot_social_chatBot_enabled);
                                        jsonTu.put("bot_social_chatBot_v", bot_social_chatBot_v);
                                        ro.setBotResponse(jsonTu);
                                    }
                                } else if (bot_social_chatBot_v.equals("2")) {
                                    if (bot == null || !bot.equalsIgnoreCase("false")) {
                                        // 竹简机器人
                                        String url = "http://idc.emotibot.com/api/ApiKey/openapi.php";
                                        Map<String, String> mapParms = new HashMap<String, String>();
                                        mapParms.put("cmd", "chat");
                                        mapParms.put("appid", "ac374de14a6ae3c15126430f6cf539c9 ");
                                        mapParms.put("userid", userid);
                                        mapParms.put("text", q);
                                        // mapParms.put("location", "");
                                        // mapParms.put("iformat", "");
                                        // mapParms.put("oformat", "");

                                        JSONObject jsonEMT = new JSONObject();
                                        String text = "这个嘛～你说的我还不能完全理解，不过我会努力的。要不，你换个说法试一试。";
                                        String type = "text";
                                        EmotiBot em = new EmotiBot();
                                        String botRes = em.chat(url, mapParms);

                                        JSONObject jsonBotRes = JSONObject.parseObject(botRes);
                                        String status = jsonBotRes.getString("return");
                                        if (status.equals("0")) {
                                            JSONArray data = jsonBotRes.getJSONArray("data");
                                            if (data.size() > 0) {
                                                String answer = data.getJSONObject(0).getString("value");
                                                type = data.getJSONObject(0).getString("type");// 目前取值wei为text和url

                                                text = answer;
                                            }
                                        }
                                        if (type == null || type.equals("text")) {
                                            jsonEMT.put("text", text);
                                        } else if (type.equals("url")) {
                                            jsonEMT.put("url", text);
                                            jsonEMT.put("text", "您可以点击这里看看。");
                                        }
                                        jsonEMT.put("kbid", "0");// 0表明不在用户业务kb范围之内
                                        jsonEMT.put("ktype", "qa");// 默认是qa的知识
                                        jsonEMT.put("dailogid", dialogid);

                                        jsonEMT.put("bot_social_chatBot_botKey", tenant.getbotKey());
                                        jsonEMT.put("bot_social_chatBot_enabled", bot_social_chatBot_enabled);
                                        jsonEMT.put("bot_social_chatBot_v", bot_social_chatBot_v);
                                        ro.setBotResponse(jsonEMT);

                                    }
                                } else {
                                    // 暂时未支持的bot_social_chatBot
                                }
                            }
                        } else {
                            // 不接社会机器人，做出默认回答
                            JSONObject responseNoSocial = new JSONObject();
                            String text = "这个嘛～你说的我还不能完全理解，不过我会努力的。要不，你换个说法试一试。";
                            responseNoSocial.put("text", text);
                            responseNoSocial.put("kbid", "0");// 0表明不在用户业务kb范围之内
                            responseNoSocial.put("ktype", "qa");// 默认是qa的知识
                            responseNoSocial.put("dailogid", dialogid);

                            responseNoSocial.put("bot_social_chatBot_botKey", tenant.getbotKey());
                            responseNoSocial.put("bot_social_chatBot_enabled", bot_social_chatBot_enabled);
                            responseNoSocial.put("bot_social_chatBot_v", bot_social_chatBot_v);
                            ro.setBotResponse(responseNoSocial);
                        }
                    }
                }

            } catch (Exception e) {
                JSONObject botRes = new JSONObject();
                botRes.put("request_q", q);

                botRes.put("a", "我还不太明白您的意思");

                processBotResponse(ro, botRes, dialogid);
            }

        }

        // 5、添加q的统计
        QAManager qamgr = new QAManager();
        String a = ro.getBotResponse().getString("text");
        String qid = ro.getBotResponse().getString("qid");
        String q_tj_id = qamgr.addTongji(q_old, a, qid, tenant);
        JSONObject resH = ro.getResponseHeader();
        JSONObject _resH = ro.getResponseHeader();
        JSONObject param = resH.getJSONObject("param");
        param.put("qid", q_tj_id);

        String result = ro.getResult().toString();

        PrintWriter out = response.getWriter();
        // out.write(result);
        out.print(result);
        out.flush();
        out.close();
    }

    /**
     * 、
     * 
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
     *      response)
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // TODO Auto-generated method stub
        doGet(request, response);
    }

    private void processBotResponse(ResultObject ro, JSONObject uniqueQA, String dailogid) {
        String _url = uniqueQA.getString("url");
        String _a = uniqueQA.getString("a");
        String _qtype = uniqueQA.getString("qtype");
        String _ktype = uniqueQA.getString("ktype");
        String _kbid = uniqueQA.getString("kbid");
        // JSONObject q = (JSONObject)ro.get().get("response");
        // JSONArray c = (JSONArray)q.get("docs");
        // if(c!=null && c.size()!=0){
        // for(int i=0;i<c.size();i++){
        // JSONObject d = (JSONObject)c.get(i);
        // String title = (String) d.get("title");
        // if(title.equals(uniqueQA.getString("kb_q"))){
        // c.remove(i);
        // }
        // }
        // }
        ro.setBotResponse(new JSONObject());

        if (_url != null && !_url.equals("")) {
            ro.setBotResponseKV("code", "200000");// 链接类
            String _q = uniqueQA.getString("kb_q");
            // ro.setBotResponseKV("text", "为您找到文档：" + _q + "，" + _a);
            ro.setBotResponseKV("text", "".equals(_a) ? "为您找到一个链接" : _a);
            ro.setBotResponseKV("url", _url);
            ro.setBotResponseKV("qtype", _qtype);
            ro.setBotResponseKV("ktype", _ktype);
            if (_kbid == null)
                _kbid = "1";// 现在业务支持哭中默认值为1，社会支持库为0
            ro.setBotResponseKV("kbid", _kbid);
            ro.setBotResponseKV("qid", uniqueQA.getString("id"));
        } else {
            ro.setBotResponseKV("code", "100000");// 文本类
            ro.setBotResponseKV("text", _a);
            ro.setBotResponseKV("qtype", _qtype);
            ro.setBotResponseKV("ktype", _ktype);
            if (_kbid == null)
                _kbid = "1";// 现在业务支持哭中默认值为1，社会支持库为0
            ro.setBotResponseKV("kbid", "1");
            ro.setBotResponseKV("qid", uniqueQA.getString("id"));
        }
        ro.setBotResponseKV("simscore", uniqueQA.getString("simscore"));
        ro.setBotResponseKV("dailogid", dailogid);
    }

    private JSONObject tubot(String botURL, String botKey, String q, String botuserid) {
        try {
            // 如果是机器人请求
            MKBHttpClient httpclient = new MKBHttpClient();

            Map<String, String> createMap = new HashMap<String, String>();
            createMap.put("key", botKey);
            createMap.put("info", q);
            if (botuserid != null && !botuserid.equals("")) {
                createMap.put("userid", botuserid);
            }
            String charset = "utf-8";

            String botRes = httpclient.doPost(botURL, createMap, charset);

            JSONObject obj = JSONObject.parseObject(botRes);

            return obj;
        } catch (Exception e) {
            throw e;
        }
    }

    // private JSONObject emotionBot(String botKey, String q, String botuserid)
    // {
    // try {
    // // 如果是机器人请求
    // MKBHttpClient httpclient = new MKBHttpClient();
    //
    // Map<String, String> createMap = new HashMap<String, String>();
    // createMap.put("key", botKey);
    // createMap.put("info", q);
    // if (botuserid != null && !botuserid.equals("")) {
    // createMap.put("userid", botuserid);
    // }
    // String charset = "utf-8";
    //
    // String botRes = httpclient.doPost(BOTURL, createMap, charset);
    //
    // JSONObject obj = JSONObject.parseObject(botRes);
    // String text = obj.getString("text");
    // // text = "您是要问我知识库以外的问题？好吧，我想说的是" + text;
    // obj.put("text", text);
    // obj.put("kbid", "0");
    // return obj;
    // } catch (Exception e) {
    // throw e;
    // }
    // }

    private ResultObject setResultObject4TenantNoExist(ResultObject ro, String apiKey, String q) {
        // 没有租户信息
        if (ro == null) {
            ro = new ResultObject();
        }
        ro.setResponseHeaderKV("q", q);
        ro.setStatus(0);
        ro.setReason("not found the tenant info by apiKey=" + apiKey);
        ro.setNumFound(0);
        ro.setStart(0);
        return ro;
    }
    /*
     * private ResultObject returnWith0(ResultObject ro, JSONObject curJson){
     * ro.setStatus(0); JSONObject response = new JSONObject();
     * response.put("kbid", "0");// 0表明不在用户业务kb范围之内 response.put("ktype",
     * "qa");// 默认是qa的知识 response.put("dailogid", dailogid);
     * 
     * response.put("bot_social_chatBot_botKey", tenant.getbotKey());
     * response.put("bot_social_chatBot_enabled", bot_social_chatBot_enabled);
     * response.put("bot_social_chatBot_v", bot_social_chatBot_v);
     * ro.setBotResponse(response); }
     */

    private Tenant getTenantInfo(String apiKey) {
        Tenant tenant = null;
        try {
            CBOManager api = new CBOManager();
            tenant = api.getTenantInfo(apiKey);
        } catch (SQLException e1) {
            // TODO Auto-generated catch block
            MKBLogger.error("Exception:" + e1.toString());
        }
        return tenant;
    }

    private String handleKeywordFromSynonym(String q, HttpServletRequest request, Tenant tenant) {
        MKBSessionManager sessionMgr = new MKBSessionManager();
        if (tenant != null && tenant.getUseSynonym()) {
            String new_q = sessionMgr.findKeywordFromSynonym(q, request, tenant);
            if (new_q != null && !new_q.equals("")) {
                return new_q;
            }
        }
        return q;
    }

    private ResultObject createDefaultResultObject() {
        ResultObjectFactory rof = new ResultObjectFactory();
        ResultObject ro = rof.create(0);
        return ro;
    }
}
