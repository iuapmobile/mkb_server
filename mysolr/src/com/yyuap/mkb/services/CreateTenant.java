package com.yyuap.mkb.services;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.joda.time.DateTime;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yyuap.mkb.cbo.CBOManager;
import com.yyuap.mkb.cbo.CommonSQL;
import com.yyuap.mkb.cbo.Tenant;
import com.yyuap.mkb.entity.KBIndex;
import com.yyuap.mkb.entity.KBQA;
import com.yyuap.mkb.pl.DBConfig;
import com.yyuap.mkb.pl.DBManager;
import com.yyuap.mkb.pl.KBDuplicateSQLException;
import com.yyuap.mkb.pl.KBInsertSQLException;
import com.yyuap.mkb.pl.KBSQLException;
import com.yyuap.mkb.processor.QAManager;
import com.yyuap.mkb.processor.SolrManager;
import com.yyuap.mkb.services.util.PropertiesUtil;

/**
 * Servlet implementation class CreateTenant
 * 此类是 开通租户类
 */
@WebServlet("/CreateTenant")
public class CreateTenant extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public CreateTenant() {
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

        // 这句话的意思，是让浏览器用utf8来解析返回的数据
        response.setHeader("Content-type", "application/json;charset=UTF-8");
        // 这句话的意思，是告诉servlet用UTF-8转码，而不是用默认的ISO8859
        response.setCharacterEncoding("UTF-8");
        
        
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs =null;

        String tname = request.getParameter("tname");//企业名称
        String tdescript = request.getParameter("tdescript");//企业名称
        String tusername = request.getParameter("tusername");//用户名
        String tkbcore = request.getParameter("tkbcore");//solr core
        String apiKey = request.getParameter("apiKey");// apiKey
        String botKey = request.getParameter("botKey");// botKey
        
        String dbname = "kb_u_"+tusername;
        
        if(apiKey == null || "".equals(apiKey)){
        	apiKey = UUID.randomUUID().toString();
        }
        
        ResultObjectFactory rof = new ResultObjectFactory();
        ResultObject ro = rof.create(0);

        StringBuffer sbf = new StringBuffer();
        sbf.append(" insert into u_tenant( ");
        sbf.append(" id, ");
        sbf.append(" tid, ");
        sbf.append(" tname, ");
        sbf.append(" apiKey, ");
        sbf.append(" tdescript, ");
        sbf.append(" tusername, ");
        sbf.append(" tpassword, ");
        sbf.append(" tkbcore, ");
        sbf.append(" tqakbcore, ");
        sbf.append(" dbip, ");
        sbf.append(" dbport, ");
        sbf.append(" dbname, ");
        sbf.append(" dbusername, ");
        sbf.append(" dbpassword, ");
        sbf.append(" botKey, ");
        sbf.append(" botname, ");
        sbf.append(" createTime, ");
        sbf.append(" updateTime, ");
        sbf.append(" simscore, ");
        sbf.append(" recommended, ");
        sbf.append(" solr_qf, ");
        sbf.append(" solr_sort ");
        sbf.append(" ) ");
        sbf.append(" VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
        String datetime = DateTime.now().toString("yyyy-MM-dd HH:mm:ss");
        
        try {
			Class.forName(CommonSQL.DRIVER);
			conn = DriverManager.getConnection(CommonSQL.URL, CommonSQL.USERNAME, CommonSQL.PASSWORD);
			
//			ps = conn.prepareStatement("select * from u_tenant where dbname = ?");
//			ps.setString(1, dbname);
//			rs = rs = ps.executeQuery();
			DatabaseMetaData dbm = conn.getMetaData();
			rs = dbm.getCatalogs();
			boolean flag = true;
			while (rs.next()) {
				if (dbname.equalsIgnoreCase(rs.getString("TABLE_CAT")))
				{
					flag = false;
				}
			}
			if(!flag){
				ro.setReason("企业ID已存在，请重新输入！");
	            ro.setStatus(-2);
	            response.getWriter().write(ro.toString());
			}else{
				String createSql = "CREATE DATABASE " + dbname + " DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci";
	    		excuteDDL(createSql,conn);
				
		        ps = conn.prepareStatement(sbf.toString());
		        ps.setString(1, UUID.randomUUID().toString());
		        ps.setString(2, UUID.randomUUID().toString());
		        ps.setString(3, tname);
		        ps.setString(4, apiKey);
		        ps.setString(5, tdescript);
		        ps.setString(6, tusername);
		        ps.setString(7, tusername);
		        ps.setString(8, "kb_u_"+tusername+"_core");
		        ps.setString(9, "kb_u_"+tusername+"_core");
		        ps.setString(10, "127.0.0.1");
		        ps.setString(11, "3306");
		        ps.setString(12, dbname);
		        ps.setString(13, "root");
		        ps.setString(14, PropertiesUtil.getJdbcString("password"));//1qazZAQ!
		        ps.setString(15, "ad2c9f8b0ec544e2a4354ffd2f2f30f1");
		        ps.setString(16, null);
		        ps.setString(17, datetime);
		        ps.setString(18, datetime);
		        ps.setString(19, "0.618");
		        ps.setString(20, "1");
		        ps.setString(21, null);
		        ps.setString(22, null);
		        boolean success = ps.execute();
		        
		        // 1、获取租户信息
		        Tenant tenant = null;
		        CBOManager api = new CBOManager();
		        try {
		            tenant = api.getTenantInfo(apiKey);
		            if (tenant == null) {
		                String _rea = "找不到aipKey[" + apiKey + "]对应的租户";
		                throw new KBSQLException(_rea);
		            }
		        } catch (Exception e1) {
		            ro.setStatus(1000);
		            ro.setReason(e1.getMessage());
		            response.getWriter().write(ro.toString());
		            return;
		        }
		        
		        File dir = getFilePath(request);
		        DBConfig dbconf = this.getDBConfigByTenant(tenant);
		        Connection tenantconn = null;
		        String _username = dbconf.getUsername();
	            String _psw = dbconf.getPassword();//;"1234qwer"
	            String _url = dbconf.getUlr();
	            tenantconn = DriverManager.getConnection(_url, _username, _psw);
	            excuteSql(dir,tenantconn);
	            ro.setStatus(0);
	            ro.setReason("开通成功，请使用吧！用户名："+tusername+"，密码是:"+tusername);
	            response.getWriter().write(ro.toString());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
            if (rs != null) {
                try {
					rs.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
            if (ps != null) {
                try {
					ps.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
            if (conn != null) {
                try {
					conn.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
        }

    }
    
    private void excuteDDL(String sqlText,Connection con)
    {
        if (isEmpty(sqlText))
            return;
        Statement stat = null;
        try
        {
            stat = con.createStatement();
            for (String sql : sqlText.split(";"))
            {
                if (!isEmpty(sql) && !sql.equalsIgnoreCase("commit"))
                {
                    stat.addBatch(sql);
                }
            }
            stat.executeBatch();
        }
        catch (SQLException e)
        {
            System.out.println("执行DDL出错"+e);
        }
        finally
        {
        	if (stat != null) {
                try {
                	stat.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
        }
    }
    
    private File getFilePath(HttpServletRequest request){
            String resource = request.getServletContext().getRealPath("/WEB-INF/mkbsql");
            //request.getServletContext().getRealPath("/WEB-INF/mkbsql");
            System.out.println(resource);
            File file = new File(resource);
            return file;
    }
    
    private void excuteSql(File dir,Connection con)
    {
    	
        for (File file : dir.listFiles())
        {
            String name = file.getName();
            int indexOfDot = name.indexOf(".");
            if (indexOfDot == -1)
                continue;
            if (!"sql".equalsIgnoreCase(name.substring(indexOfDot + 1)))
                continue;
            if (!name.toLowerCase().contains("ddl"))
                continue;
            try
            {
                String sqlText = this.readFile(file);
                excuteDDL(sqlText,con);
            }
            catch (Exception e)
            {
            }
        }
        
    }
    
    public static boolean isEmpty(String str)
    {
        return str == null || str.trim().length() == 0;
    }
    
    public static String readFile(File file)
    {
        StringBuffer sb = new StringBuffer();
        BufferedReader br = null;
        FileInputStream fis = null;
        InputStreamReader isr = null;
        try
        {
            fis = new FileInputStream(file);
            isr = new InputStreamReader(fis, "UTF-8");
            br = new BufferedReader(isr);
            String lineStr = null;
            while ((lineStr = br.readLine()) != null)
            {
                sb.append(lineStr);
            }
            br.close();
            
        }
        catch (FileNotFoundException e)
        {
        }
        catch (IOException e)
        {
        }
        finally
        {
            closeStream(fis);
            closeStream(isr);
            closeStream(br);
        }
        return sb.toString();
    }
    public static void closeStream(Closeable stream)
    {
        try
        {
            if (stream != null)
            {
                stream.close();
            }
        }
        catch (IOException e)
        {
        }
    }
    
    private DBConfig getDBConfigByTenant(Tenant tenant) {
        DBConfig dbc = new DBConfig();
        dbc.setIp(tenant.getdbip());
        dbc.setPort(tenant.getdbport());
        dbc.setDbName(tenant.getdbname());

        dbc.setUsername(tenant.getdbusername());
        dbc.setPassword(tenant.getdbpassword());

        return dbc;
    }
    
    public static void main(String[] args) {
    	 String resource = CreateTenant.class.getResource("/com/yyuap/mkb/resource").getPath();
    	 File file = new File("/com/yyuap/mkb/resource/jsbc.properties");
    	 file.listFiles();
    	 System.out.println();
	}

}
