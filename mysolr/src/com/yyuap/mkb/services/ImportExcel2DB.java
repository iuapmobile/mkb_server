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
import com.yyuap.mkb.entity.KBINDEXTYPE;
import com.yyuap.mkb.pl.DBManager;
import com.yyuap.mkb.processor.SolrManager;

/**
 * Servlet implementation class importExcel2DB
 */
@WebServlet("/importExcel2DB")
public class ImportExcel2DB extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public ImportExcel2DB() {
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
            if (tenant == null) {
                return;
            }

            String type = request.getParameter("type");
            if (type == null) {
                type = KBINDEXTYPE.KBINDEX.toString().toLowerCase();
            }

            SolrManager mgr = new SolrManager(tenant.gettkbcore());
            boolean success = mgr.saveExcelData2DB(path, type,tenant);
            if (success) {
                // 手动导入
                // mgr.addDocument(kbindex);
            }
        }
        response.getWriter().append("importExcel2DB Served at: ").append(request.getContextPath());
    }

}
