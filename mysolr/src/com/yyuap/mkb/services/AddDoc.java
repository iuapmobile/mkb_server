package com.yyuap.mkb.services;

import java.io.IOException;
import java.sql.SQLException;
import java.util.UUID;

import com.yyuap.mkb.cbo.CBOManager;
import com.yyuap.mkb.cbo.Tenant;
import com.yyuap.mkb.entity.KBIndex;
import com.yyuap.mkb.log.MKBLogger;
import com.yyuap.mkb.pl.DBManager;
import com.yyuap.mkb.processor.SolrManager;

/**
 * Servlet implementation class addDoc
 */
@WebServlet("/abc")
public class AddDoc extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public AddDoc() {
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
        request.setCharacterEncoding("UTF-8");

        try {
            String apiKey = request.getParameter("apiKey");

            // 1銆佽幏鍙栫鎴蜂俊鎭�
            Tenant tenant = null;
            CBOManager api = new CBOManager();
            try {
                tenant = api.getTenantInfo(apiKey);
            } catch (SQLException e1) {
                // TODO Auto-generated catch block
                MKBLogger.error("Exception:" + e1.toString());
            }
            if (tenant == null) {
                return;
            }

            KBIndex kbindex = new KBIndex();
            String title = request.getParameter("title");
            String descript = request.getParameter("descript");
            String descriptImg = request.getParameter("descriptImg");
            String keywords = request.getParameter("keywords");
            String url = request.getParameter("url");
            String text = request.getParameter("text");

            kbindex.setId(UUID.randomUUID().toString());
            kbindex.setTitle(title);
            kbindex.setDescript(descript);
            kbindex.setDescriptImg(descriptImg);
            kbindex.setKeywords(keywords);
            kbindex.setUrl(url);
            kbindex.setText(text);

            DBManager saveData2DB = new DBManager();
            boolean success = saveData2DB.addKBIndex(kbindex, tenant);

            if (success) {
                SolrManager solr = new SolrManager(tenant.gettkbcore());
                solr.addDoc(kbindex);
            }

        } catch (Exception e) {
            // TODO Auto-generated catch block
            MKBLogger.error("Exception:" + e.toString());
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
