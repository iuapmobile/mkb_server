package com.yyuap.mkb.api;

import java.io.IOException;
import java.sql.SQLException;

import com.yyuap.mkb.cbo.CBOManager;
import com.yyuap.mkb.cbo.TenantInfo;
import com.yyuap.mkb.log.MKBLogger;
import com.yyuap.mkb.services.ResultObject;
import com.yyuap.mkb.services.ResultObjectFactory;

/**
 * Servlet implementation class UpdateBotSetting
 */
@WebServlet("/UpdateTenantInfo")
public class SetBotIsRecommended extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public SetBotIsRecommended() {
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
        String apiKey = request.getParameter("apiKey");
        String recommended = request.getParameter("recommended");
        tenant.setApiKey(apiKey);
        if ("0".equals(recommended)) {
            tenant.setRecommended(false);
        } else {
            tenant.setRecommended(true);
        }

        ResultObjectFactory rof = new ResultObjectFactory();
        ResultObject ro = rof.create(0);
        boolean flag = false;
        CBOManager cboMgr = new CBOManager();

        try {
            flag = cboMgr.updateBotSettings(apiKey, "recommended", tenant.getRecommended());
        } catch (SQLException e1) {
            MKBLogger.error("Exception:" + e1.toString());
        }
        if (flag) {
            ro.setStatus(0);
            ro.setReason("更新成功");
        } else {
            ro.setStatus(-1);
            ro.setReason("更新失败");
        }

        response.getWriter().write(ro.toString());
    }

}
