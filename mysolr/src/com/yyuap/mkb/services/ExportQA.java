package com.yyuap.mkb.services;

import java.io.IOException;
import java.sql.SQLException;

import com.yyuap.mkb.cbo.CBOManager;
import com.yyuap.mkb.cbo.Tenant;
import com.yyuap.mkb.fileUtil.ExcelExport;
import com.yyuap.mkb.log.MKBLogger;



/**
 * Servlet implementation class exportQA
 */
@WebServlet("/exportQA")
public class ExportQA extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public ExportQA() {
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


            String apiKey = request.getParameter("apiKey");
            
         // 1、获取租户信息
            Tenant tenant = null;
            CBOManager api = new CBOManager();
            try {
                tenant = api.getTenantInfo(apiKey);
            } catch (SQLException e1) {
                // TODO Auto-generated catch block
                MKBLogger.error("Exception:" + e1.toString());
            }
            
            
            ResultObjectFactory rof = new ResultObjectFactory();
            ResultObject ro = rof.create(0);
            ExcelExport export = new ExcelExport();
            boolean success = export.exportQAForExcel(tenant);
            if (success) {
            	ro.setStatus(0);  
            }else{
            	ro.setStatus(-1);
            }
            response.getWriter().write(ro.toString());

    }

}
