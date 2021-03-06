package com.yyuap.mkb.services;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.tika.exception.TikaException;
import org.xml.sax.SAXException;

import com.yyuap.mkb.cbo.CBOManager;
import com.yyuap.mkb.cbo.Tenant;
import com.yyuap.mkb.entity.KBIndex;
import com.yyuap.mkb.fileUtil.ExcelXReader;
import com.yyuap.mkb.log.MKBLogger;
import com.yyuap.mkb.processor.SolrManager;

/**
 * Servlet implementation class indexFiles
 */
@WebServlet("/indexFiles")
public class IndexFiles extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public IndexFiles() {
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

        String fileName = "/Users/gct/work/附件2 移动应用平台建设项目需求书V1.9.pdf";

        String path = request.getParameter("path");

        String url = request.getParameter("url");

        String excelPath = request.getParameter("excelPath");
        String rootPath = request.getParameter("rootPath");

        // 1、如果有excelPath，表明这是一个excel，里面记录了要索引的File清单，excelPath是excel的地址
        // 2、如果没有excelPath，表明这是一个可以直接索引的文件，path是要索引文件的地址 url是其在线地址

        String apiKey = request.getParameter("apiKey");

        // 获取租户信息
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

        SolrManager mgr = new SolrManager(tenant.gettkbcore());
        if (excelPath != null && !excelPath.equals("")) {

            ExcelXReader reader = new ExcelXReader();
            List<KBIndex> list = reader.readFileListFromExcel(excelPath, rootPath);

            for (int i = 0, len = list.size(); i < len; i++) {
                KBIndex kb = list.get(i);
                try {
                    String _fullPath = rootPath + "/" + kb.getFilePath() + "/" + kb.getTitle();
                    while (_fullPath.contains("//")) {
                        _fullPath = _fullPath.replaceAll("//", "/");
                    }
                    mgr.indexTikaFile(_fullPath, kb, tenant);
                } catch (SAXException e) {
                    // TODO Auto-generated catch block
                    MKBLogger.error("Exception:" + e.toString());
                } catch (TikaException e) {
                    // TODO Auto-generated catch block
                    MKBLogger.error("Exception:" + e.toString());
                } catch (SQLException e) {
                    // TODO Auto-generated catch block
                    MKBLogger.error("Exception:" + e.toString());
                }
            }
        } else if (path != null && !path.equals("")) {
            try {
                KBIndex kb = new KBIndex();
                mgr.indexTikaFile(path, kb, tenant);
            } catch (SAXException e) {
                // TODO Auto-generated catch block
                MKBLogger.error("Exception:" + e.toString());
            } catch (TikaException e) {
                // TODO Auto-generated catch block
                MKBLogger.error("Exception:" + e.toString());
            } catch (SQLException e) {
                // TODO Auto-generated catch block
                MKBLogger.error("Exception:" + e.toString());
            }
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
