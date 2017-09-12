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
import com.yyuap.mkb.cbo.TenantInfo;

/**
 * Servlet implementation class UpdateBotSetting
 */
@WebServlet("/UpdateTenantInfo")
public class UpdateTenantInfo extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public UpdateTenantInfo() {
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
        String recommended = request.getParameter("recommended");
        tenant.setId(id);
        if("0".equals(recommended)){
        	 tenant.setRecommended(false);
        }else{
        	tenant.setRecommended(true);
        }

        ResultObjectFactory rof = new ResultObjectFactory();
        ResultObject ro = rof.create(0);
        boolean flag = false;
        CBOManager api = new CBOManager();
        String sql = "update mkb.u_tenant set recommended=? where id=?";
        try {
        	flag = api.updateTenantInfo(sql,tenant);
        } catch (SQLException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        if(flag){
        	ro.setStatus(0);
        	ro.setReason("更新成功");
        }else{
        	ro.setStatus(-1);
        	ro.setReason("更新失败");
        }

        response.getWriter().write(ro.toString());
    }

    
}
