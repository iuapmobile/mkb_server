package com.yyuap.mkb.services;

import java.io.IOException;
import java.io.PrintWriter;
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
        // 这句话的意思，是让浏览器用utf8来解析返回的数据
        response.setHeader("Content-type", "application/json;charset=UTF-8");
        // 这句话的意思，是告诉servlet用UTF-8转码，而不是用默认的ISO8859
        response.setCharacterEncoding("UTF-8");

        ResultObjectFactory rof = new ResultObjectFactory();
        ResultObject ro = rof.create(0);

        String path = request.getParameter("path");// 文件服务器地址
        if (path != null && !path.equals("")) {

            String apiKey = request.getParameter("apiKey");

            // 1、获取租户信息
            Tenant tenant = null;
            CBOManager api = new CBOManager();
            try {
                tenant = api.getTenantInfo(apiKey);
            } catch (SQLException e1) {
                ro.setStatus(1000);
                ro.setReason("获取租户信息异常，apiKey=" + apiKey);
                e1.printStackTrace();
            }
            if (tenant != null) {
                try {
                    SolrManager mgr = new SolrManager(tenant.gettkbcore());
                    int num = mgr.importQA(path, tenant);

                    if (num > 0) {
                        // 手动导入
                        // mgr.addDocument(kbindex);
                        ro.setReason("成功导入" + num + "条记录!");
                        ro.setStatus(0);

                    } else {
                        ro.setReason("没有导入数据!");
                        ro.setStatus(-2);
                    }

                } catch (Exception e) {
                    ro.setReason("导入异常!" + e.toString());
                    ro.setStatus(-3);
                }
            }
        } else {
            // 没有指定倒入文件的地址
            ro.setStatus(1000);
            ro.setReason("未指定导入文件的路径（含文件名）");

        }

        String result = ro.getResult().toString();
        PrintWriter out = response.getWriter();
        // out.write(result);
        out.print(result);
        out.flush();
        out.close();
    }

}
