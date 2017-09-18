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
import com.yyuap.mkb.processor.IntentPredictionManager;
import com.yyuap.mkb.processor.MKBSessionManager;
import com.yyuap.mkb.processor.QAManager;
import com.yyuap.mkb.processor.SolrManager;
import com.yyuap.mkb.services.util.MKBRequestProcessor;
import com.yyuap.mkb.services.util.RedisUtil;
import com.yyuap.mkb.turbot.MKBHttpClient;

import redis.clients.jedis.Jedis;

/**
 * Servlet implementation class mkbQuery
 */
@WebServlet("/mkbQuery")
public class Query extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final String BOTURL = "http://www.tuling123.com/openapi/api";
    private static final String BOTAPIKEY = "f08c391260304acc81f8fdb27de44832";// 雪儿
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

        String q = requestParam.getString("q");
        String q_old = requestParam.getString("q");
        String bot = requestParam.getString("bot");
        String apiKey = requestParam.getString("apiKey");
        String buserid = requestParam.getString("buserid");
        String dailog = requestParam.getString("dailog");
        String dailogid = requestParam.getString("dailogid");
        if(dailogid != null && !"".equals(dailogid)){
        	dailog = RedisUtil.getString(dailogid);
        }else{
			dailogid = UUID.randomUUID().toString();
        }
        
        String[] tag = request.getParameterValues("tag");//tag: "personinside" //内部为personinside 其余为空
        requestParam.put("tag", tag);
        // 一、获取租户信息
        Tenant tenant = null;
        CBOManager api = new CBOManager();
        try {
            tenant = api.getTenantInfo(apiKey);

            MKBSessionManager sessionMgr = new MKBSessionManager();
            if (tenant.getUseSynonym()) {
                String new_q = sessionMgr.findKeywordFromSynonym(q, request, tenant);
                if (new_q != null && !new_q.equals("")) {
                    q = new_q;
                    requestParam.put("q", q);
                }
            }
        } catch (SQLException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        ResultObjectFactory rof = new ResultObjectFactory();
        ResultObject ro = rof.create(0);
        // JSONObject ret = ro.getResult();
        // JSONObject res = ro.getResponse();

        if (tenant != null) {
            JSONObject uniqueQA = null;

            // 0、预测
            try {
                JSONObject botConfig = tenant.getBotSkillConfigJSON();
                if (botConfig != null) {
                    // {"skills": {"intent": true,"prediction": true}}
                    JSONObject skills = botConfig.getJSONObject("skills");
                    boolean prediction = skills.getBooleanValue("prediction");
                    if (prediction) {
                        IntentPredictionManager intentMgr = new IntentPredictionManager();
                        JSONObject obj = intentMgr.predictIntent(q,dailog);
                        if (obj != null) {
//                        	obj.put("dailogid", dailogid);
                        	if(obj.get("dilog")!=null){
                        		RedisUtil.setString(dailogid, obj.get("dilog").toString());
                        	}
                        	if("2".equals(obj.get("status")==null?"":obj.get("status").toString())){
                        		String scenename = obj.get("scenename").toString();
                        		String dataStr = "";
                        		JSONObject jsonObject = obj.getJSONObject("data");
                        		//遍历 ai系统返回参数中data  data中 有我们需要的值  拼成 value1 value2 value3 中间空格 隔开
                        		for (Map.Entry<String, Object> entry : jsonObject.entrySet()) {
                        			System.out.println(entry.getKey() + ":" + entry.getValue());
                        			dataStr = dataStr + " " + entry.getValue();
                        	    }
                        		dataStr = scenename + dataStr;
                        		q = dataStr;
                        		
                        	}else{
                        		obj.put("dailogid", dailogid);
                        		ro.setBotResponse(obj);
                                response.getWriter().write(ro.getResutlString());
                                return;
                        	}
                            
                        }
                    }

                    boolean intent = skills.getBooleanValue("intent");
                    if (intent) {
                        // 调用意图处理接口

                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }catch (Throwable a) {
	           	 ro.setStatus(21000);
	             ro.setReason(a.getMessage());
	             response.getWriter().write(ro.getResutlString());
	             return;
          }

            
            String id = null;
            // 1、查询唯一答案，根据相似性处理得出唯一答案
            QAManager qamgr = new QAManager();
            try {
                uniqueQA = qamgr.getUniqueAnswer(q, tenant,tag);
                if (uniqueQA != null && (!uniqueQA.getString(q).equals("") || !uniqueQA.getString("url").equals(""))) {
                    ro.setBotResponseKV("dailogid", dailogid);
                    id = uniqueQA.getString("id");
//                	processBotResponse(ro, uniqueQA,dailogid);
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
            }catch (Throwable a) {
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
                    JSONObject _ret = solrmng.query(requestParam, tenant,id);// 获取查询结果,，一个新的对象
                    ro.set(_ret);// 导致botResponse需要重新赋值
                } catch (Exception e) {
                    System.out.println("==========>推荐出错！q=" + q + " tname=" + tenant.gettname() + " apiKey=" + apiKey
                            + " " + e.toString());
                }
            }
            if (uniqueQA != null && (!uniqueQA.getString(q).equals("") || !uniqueQA.getString("url").equals(""))) {
            	processBotResponse(ro, uniqueQA,dailogid);
            }

            // 3、没有唯一答案时，外接bot处理
            try {
                if (uniqueQA == null || (uniqueQA.getString("a").equals("") && uniqueQA.getString("url").equals(""))) {
                    if (bot == null || !bot.equalsIgnoreCase("false")) {
                        JSONObject jsonTu = this.tubot(tenant.getbotKey(), q, buserid);
                        jsonTu.put("dailogid", dailogid);
                        if(null !=dailog&&!"".equals(dailog)){
                        	jsonTu.put("text", "您好，我找呀找还是没有找到您想要的内容！");
                        }
                        ro.setBotResponse(jsonTu);
                    }
                }
            } catch (Exception e) {
                JSONObject botRes = new JSONObject();
                botRes.put("request_q", q);

                botRes.put("a", "我还不太明白您的意思");

                processBotResponse(ro, botRes,dailogid);
            }

        } else {
            // 没有租户信息
            ro.setResponseHeaderKV("q", q);
            ro.setStatus(0);
            ro.setReason("not found the tenant info!");
            ro.setNumFound(0);
            ro.setStart(0);
        }

        // 5、添加q的统计
        QAManager qamgr = new QAManager();
        String a = ro.getBotResponse().getString("text");
        String q_tj_id = qamgr.addTongji(q_old, a, tenant);
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

    private void processBotResponse(ResultObject ro, JSONObject uniqueQA,String dailogid) {
        String _url = uniqueQA.getString("url");
        String _a = uniqueQA.getString("a");
        String _qtype = uniqueQA.getString("qtype");
//        JSONObject q = (JSONObject)ro.get().get("response");
//        JSONArray c = (JSONArray)q.get("docs");
//        if(c!=null && c.size()!=0){
//        	for(int i=0;i<c.size();i++){
//        		JSONObject d = (JSONObject)c.get(i);
//        		String title = (String) d.get("title");
//        		if(title.equals(uniqueQA.getString("kb_q"))){
//        			c.remove(i);
//        		}
//        	}
//        }
        ro.setBotResponse(new JSONObject());
        
        if (_url != null && !_url.equals("")) {
            ro.setBotResponseKV("code", "200000");// 链接类
            String _q = uniqueQA.getString("kb_q");
//            ro.setBotResponseKV("text", "为您找到文档：" + _q + "，" + _a);
            ro.setBotResponseKV("text", "".equals(_a)?_q:_a);
            ro.setBotResponseKV("url", _url);
            ro.setBotResponseKV("qtype", _qtype);
            ro.setBotResponseKV("kbid", "1");
        } else {
            ro.setBotResponseKV("code", "100000");// 文本类
            ro.setBotResponseKV("text", _a);
            ro.setBotResponseKV("qtype", _qtype);
            ro.setBotResponseKV("kbid", "1");
        }
        ro.setBotResponseKV("simscore", uniqueQA.getString("simscore"));
        ro.setBotResponseKV("dailogid", dailogid);
    }

    private JSONObject tubot(String botKey, String q, String botuserid) {
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

            String botRes = httpclient.doPost(BOTURL, createMap, charset);

            JSONObject obj = JSONObject.parseObject(botRes);
            String text = obj.getString("text");
            // text = "您是要问我知识库以外的问题？好吧，我想说的是" + text;
            obj.put("text", text);
            obj.put("kbid", "0");
            return obj;
        } catch (Exception e) {
            throw e;
        }
    }

}
