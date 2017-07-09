package com.yyuap.mkb.services;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	    doPost(request, response);
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		
		 String path = request.getParameter("path");//文件服务器地址
	        if(path!=null && !path.equals("")) {
	           
	           String ip = request.getParameter("ip");
	           String port = request.getParameter("port");
	           String driver = request.getParameter("driver");
	           String dbname = request.getParameter("dbname");
	           String username = request.getParameter("username");
	           String password = request.getParameter("password");
	           
	           String type = request.getParameter("type");
	           if(type==null){
	               type = KBINDEXTYPE.KBINDEX.toString().toLowerCase();
	           }
	           /*
	           public static final String DRIVER = "com.mysql.jdbc.Driver";
	           public static final String DB_NAME = "iuapkb";
	           public static final String USERNAME = "root";
	           public static final String PASSWORD = "1234qwer";
	           public static final String IP = "127.0.0.1";
	           public static final String PORT = "3306";
	           */
	           String url = "jdbc:mysql://" + ip + ":" + port + "/" + dbname +"?useUnicode=true&characterEncoding=utf-8";
	           
	           SolrManager mgr = new SolrManager();
	           boolean success = mgr.saveExcelData2DB(path, type);
	           if(success){
	               //手动导入
	               //mgr.addDocument(kbindex);
	           }
	        }
	        response.getWriter().append("importExcel2DB Served at: ").append(request.getContextPath());
	}

}
