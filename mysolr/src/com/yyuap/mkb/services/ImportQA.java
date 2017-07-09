package com.yyuap.mkb.services;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.yyuap.mkb.cbo.CBOManager;
import com.yyuap.mkb.cbo.Tenant;
import com.yyuap.mkb.pl.DBConfig;
import com.yyuap.mkb.processor.SolrManager;



/**
 * Servlet implementation class importQA
 */
@WebServlet("/importQA")
public class ImportQA extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public ImportQA() {
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

        String path = request.getParameter("path");// 文件服务器地址
        if (path != null && !path.equals("")) {

            String ip = request.getParameter("ip");
            String port = request.getParameter("port");
          
            String dbname = request.getParameter("dbname");
            String username = request.getParameter("username");
            String password = request.getParameter("password");
            String apiKey = request.getParameter("apiKey");
            
         // 1、获取租户信息
            Tenant tenant = null;
            CBOManager api = new CBOManager();
            try {
                tenant = api.getTenantInfo(apiKey);
            } catch (SQLException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            
            

            SolrManager mgr = new SolrManager();
            boolean success = mgr.importQA(path, tenant);
            if (success) {
                // 手动导入
                // mgr.addDocument(kbindex);
            }
        }
        response.getWriter().append("importQA Served at: ").append(request.getContextPath());

    }

}
