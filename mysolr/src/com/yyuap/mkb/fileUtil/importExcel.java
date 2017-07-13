package com.yyuap.mkb.fileUtil;

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

/**
 * Servlet implementation class importExcel
 */
@WebServlet("/importExcel")
public class importExcel extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public importExcel() {
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
        // response.setContentType("application/msexcel;charset=UTF-8");

        String path = request.getParameter("path");// 文件服务器地址
        if (path != null && !path.equals("")) {

            String type = request.getParameter("type");
            if (type == null) {
                type = KBINDEXTYPE.KBINDEX.toString().toLowerCase();
            }

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

            DBManager saveData2DB = new DBManager();
            try {
                if (type.equalsIgnoreCase("kbindex")) {
                    saveData2DB.saveKB(path, KBINDEXTYPE.KBINDEX, tenant);
                } else if (type.equalsIgnoreCase("qa")) {
                    saveData2DB.saveKB(path, KBINDEXTYPE.QA, tenant);
                }
            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            System.out.println("end");
        }
        response.getWriter().append("Served at: ").append(request.getContextPath());
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
     *      response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // TODO Auto-generated method stub
        doGet(request, response);
    }

}
