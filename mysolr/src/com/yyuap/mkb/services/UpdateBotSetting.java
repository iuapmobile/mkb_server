package com.yyuap.mkb.services;

import java.io.IOException;
import java.sql.SQLException;

import com.yyuap.mkb.cbo.CBOManager;
import com.yyuap.mkb.cbo.TenantInfo;
import com.yyuap.mkb.log.MKBLogger;

/**
 * Servlet implementation class UpdateBotSetting
 */
@WebServlet("/UpdateBotSetting")
public class UpdateBotSetting extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public UpdateBotSetting() {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
     *      response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // TODO Auto-generated method stub
        doPost(request, response);
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
     *      response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // TODO Auto-generated method stub
        request.setCharacterEncoding("UTF-8");
        // 这句话的意思，是让浏览器用utf8来解析返回的数据
        response.setHeader("Content-type", "application/json;charset=UTF-8");
        // 这句话的意思，是告诉servlet用UTF-8转码，而不是用默认的ISO8859
        response.setCharacterEncoding("UTF-8");
        
        

        TenantInfo tenant = new TenantInfo();
        String id = request.getParameter("id");
        String tname = request.getParameter("tname");
        String tdescript = request.getParameter("tdescript");
        String tusername = request.getParameter("tusername");
        String tpassword = request.getParameter("tpassword");
        String simscore = request.getParameter("simscore");
        String recommended = request.getParameter("recommended");
        String solr_qf = request.getParameter("solr_qf");
        String solr_sort = request.getParameter("solr_sort");
        String useSynonym = request.getParameter("useSynonym");
        String solr_useFilterQuerties = request.getParameter("sorl_useFilterQueries");
        tenant.setId(id);
        tenant.setTname(tname);
        tenant.setTdescript(tdescript);
        tenant.setTusername(tusername);
        tenant.setTpassword(tpassword);
        tenant.setSimscore(Float.parseFloat(simscore==null?"0.618":simscore));
        if("true".equals(recommended)){
        	 tenant.setRecommended(true);
        }else{
        	tenant.setRecommended(false);
        }
        tenant.setSolr_qf(solr_qf);
        tenant.setSolr_sort(solr_sort);
        if("true".equals(useSynonym)){
       	 	tenant.setUseSynonym(true);
        }else{
        	tenant.setUseSynonym(false);
        }
        if("true".equals(solr_useFilterQuerties)){
       	 	tenant.setSorl_useFilterQueries(true);
        }else{
        	tenant.setSorl_useFilterQueries(false);
        }
        

        ResultObjectFactory rof = new ResultObjectFactory();
        ResultObject ro = rof.create(0);
        boolean flag = false;
        CBOManager api = new CBOManager();
        try {
        	flag = api.updateBotInfo(tenant);
        } catch (SQLException e1) {
            // TODO Auto-generated catch block
            MKBLogger.error("Exception:" + e1.toString());
        }
        if(flag){
        	ro.setStatus(0);
        	ro.setReason("更新成功");
        }else{
        	ro.setStatus(-5);
        	ro.setReason("更新失败");
        }

        response.getWriter().write(ro.toString());
    }

    
}
