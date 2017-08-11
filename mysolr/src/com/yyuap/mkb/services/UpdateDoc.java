package com.yyuap.mkb.services;

import java.io.IOException;
import java.sql.SQLException;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.yyuap.mkb.cbo.CBOManager;
import com.yyuap.mkb.cbo.Tenant;
import com.yyuap.mkb.entity.KBIndex;
import com.yyuap.mkb.pl.DBManager;
import com.yyuap.mkb.pl.KBSQLException;
import com.yyuap.mkb.processor.SolrManager;

/**
 * Servlet implementation class updateDoc
 */
@WebServlet("/updateDoc")
public class UpdateDoc extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public UpdateDoc() {
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
        String apiKey = request.getParameter("apiKey");
        String q = request.getParameter("q");
        ResultObjectFactory rof = new ResultObjectFactory();
        ResultObject ro = rof.create(0);
        
        // 1、获取租户信息
        Tenant tenant = null;
        CBOManager api = new CBOManager();
        try {
            tenant = api.getTenantInfo(apiKey);
            if (tenant == null) {
                String _rea = "找不到aipKey[" + apiKey + "]对应的租户";
                throw new KBSQLException(_rea);
            }
        } catch (Exception e1) {
            ro.setStatus(1000);
            ro.setReason(e1.getMessage());
            response.getWriter().write(ro.toString());
            return;
        }
        if (tenant == null) {
            // 没有租户信息
            ro.setResponseHeaderKV("q", q);
            ro.setStatus(0);
            ro.setReason("not found the tenant info!");
            ro.setNumFound(0);
            ro.setStart(0);
            return;
        }

        SolrManager solr = new SolrManager(tenant.gettkbcore());

        String id = request.getParameter("id");
        String title = request.getParameter("title");
        String descript = request.getParameter("descript");
        String descriptImg = request.getParameter("descriptImg");
        String keywords = request.getParameter("keywords");
        String url = request.getParameter("url");
        String text = request.getParameter("text");

        KBIndex kbindex = new KBIndex();
        kbindex.setId(id);
        kbindex.setTitle(title);
        kbindex.setDescript(descript);
        kbindex.setDescriptImg(descriptImg);
        kbindex.setKeywords(keywords);
        kbindex.setUrl(url);
        kbindex.setText(text);

       
        if (apiKey == null || "".equals(apiKey)) {

            ro.setReason("apikey为空");
            ro.setStatus(-1);
            response.getWriter().write(ro.toString());
            return;
        }
       
      
       
        //  1、先更新DB
        DBManager saveData2DB = new DBManager();
        boolean success = false;

        try {
            success = saveData2DB.updateKBIndex(kbindex, tenant);
        } catch (SQLException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        if (success) {
            try {
                solr.delDocById(id);
                solr.addDoc(kbindex);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        response.getWriter().append("Served at: ").append(request.getContextPath());
    }

}
