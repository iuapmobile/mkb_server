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
import java.util.Map;
import java.util.UUID;

import org.dom4j.Document;
import org.dom4j.Element;
import org.joda.time.DateTime;

import com.yyuap.mkb.cbo.CBOManager;
import com.yyuap.mkb.cbo.CommonSQL;
import com.yyuap.mkb.cbo.Tenant;
import com.yyuap.mkb.fileUtil.FileCopyUtil;
import com.yyuap.mkb.fileUtil.XMLParseDataConfig;
import com.yyuap.mkb.log.MKBLogger;
import com.yyuap.mkb.pl.DBConfig;
import com.yyuap.mkb.pl.KBSQLException;
import com.yyuap.mkb.services.util.PropertiesUtil;
import com.yyuap.mkb.socialChatBot.MKBHttpClient;

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

        ResultObjectFactory rof = new ResultObjectFactory();
        ResultObject ro = rof.create(0);

        String sourceCorePath = PropertiesUtil.getConfigPropString("sourceCorePath");
        if(sourceCorePath==null){
            ro.setReason("config.properties里没有sourceCorePath的配置！");
            response.getWriter().write(ro.toString());
            return;
        }
        
        String solrhomePath = PropertiesUtil.getConfigPropString("solrhomePath");
        if(solrhomePath==null){
            ro.setReason("config.properties里没有solrhomePath的配置！");
            response.getWriter().write(ro.toString());
            return;
        }
        
        
        
        String solrPath = PropertiesUtil.getConfigPropString("solrPath");
        if(solrPath==null){
            ro.setReason("config.properties里没有solrPath的配置！");
            response.getWriter().write(ro.toString());
            return;
        }
        
        String db_ip = PropertiesUtil.getJdbcString("db_ip");
        if(db_ip==null){
            ro.setReason("jdbc.properties里没有db_ip的配置！");
            response.getWriter().write(ro.toString());
            return;
        }
        
        String db_port = PropertiesUtil.getJdbcString("db_port");
        if(db_port==null){
            ro.setReason("jdbc.properties里没有db_port的配置！");
            response.getWriter().write(ro.toString());
            return;
        }
        String db_password = PropertiesUtil.getJdbcString("db_password");
        if(db_password==null){
            ro.setReason("jdbc.properties里没有db_password的配置！");
            response.getWriter().write(ro.toString());
            return;
        }
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
			conn = DriverManager.getConnection(CommonSQL.DB_MKB_URL, CommonSQL.USERNAME, CommonSQL.PASSWORD);
			
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
		        ps.setString(10, db_ip);
		        ps.setString(11, db_port);
		        ps.setString(12, dbname);
		        ps.setString(13, "root");
		        ps.setString(14, db_password);//1qazZAQ!
		        ps.setString(15, "ad2c9f8b0ec544e2a4354ffd2f2f30f1");
		        ps.setString(16, null);
		        ps.setString(17, datetime);
		        ps.setString(18, datetime);
		        ps.setString(19, "0.618");
		        ps.setString(20, "1");
		        ps.setString(21, null);
		        ps.setString(22, null);
		        boolean success = ps.execute();
		        tkbcore = "kb_u_"+tusername+"_core";
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
	            
	            //复制core
	            FileCopyUtil  fileCopy = new FileCopyUtil();
	            
	            if(!solrhomePath.endsWith("/")){
	                solrhomePath+="/";
	            }
	            String destCorePath = solrhomePath + tkbcore;
	            File file = new File(sourceCorePath);
	            File file2 = new File(destCorePath);
	            fileCopy.copyFile(file, file2);
	            XMLParseDataConfig  xmlParse = new XMLParseDataConfig();
	            String xmlpath = destCorePath+File.separator+"conf"+File.separator+"data-config.xml";
	            
	            String url="jdbc:mysql://"+db_ip+":"+db_port+"/"+dbname+"?characterEncoding=utf-8&amp;autoReconnect=true";   
	            //获取document
	            File xmlFile = xmlParse.getXmlFile(xmlpath);
	            Document document = xmlParse.getDocument(xmlFile);
	            //获取 指定元素 dataSource
	            String dataSourcepath ="//dataSource[@type='JdbcDataSource']";
	            Element dataSource = xmlParse.getElement(document, dataSourcepath);
	            xmlParse.editAttribute(dataSource, "name",dbname );
	            xmlParse.editAttribute(dataSource, "url",url );
	            //获取 指定元素 kbindexinfo entity
	            String kbindexpath ="//entity[@name='kbIndexInfo']";
	            Element kbindexElement = xmlParse.getElement(document, kbindexpath);
	            xmlParse.editAttribute(kbindexElement, "dataSource",dbname );
	            //获取 指定元素 qa entity
	            String qapath ="//entity[@name='qa']";
	            Element qaElement = xmlParse.getElement(document, qapath);
	            xmlParse.editAttribute(qaElement, "dataSource",dbname );
	            
	            //获取 指定元素 qasimilar entity
	            String qasimilarpath ="//entity[@name='qa_similar']";
	            Element qasimilarElement = xmlParse.getElement(document, qasimilarpath);
	            xmlParse.editAttribute(qasimilarElement, "dataSource",dbname );
	            //保存
	            xmlParse.saveDocument(document, xmlFile);
	            
	            //通过 HTTP 创建core
	            
	            // config=solrconfig.xml&schema=schema.xml&dataDir=data  &config={2}&schema={3}&dataDir={4}
	            solrPath = solrPath + "?action=CREATE&name={0}&instanceDir={1}";
	            MKBHttpClient httpclient = new MKBHttpClient();
	            String result = httpclient.doHttpGet(solrPath, tkbcore,tkbcore);
	            ro.setStatus(0);
	            if(null == result){
	            	ro.setReason("创建solr服务引擎失败，请手动创建！");
	            }else{
	            	ro.setReason("开通成功，可以使用用户名："+tusername+"，密码是:"+tusername+"进行登陆管理系统！");
	            }
	           
	            response.getWriter().write(ro.toString());
			}
		} catch (Exception e) {
			MKBLogger.error("Exception:" + e.toString());
		}finally {
            if (rs != null) {
                try {
					rs.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					MKBLogger.error("Exception:" + e.toString());
				}
            }
            if (ps != null) {
                try {
					ps.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					MKBLogger.error("Exception:" + e.toString());
				}
            }
            if (conn != null) {
                try {
					conn.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					MKBLogger.error("Exception:" + e.toString());
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
            MKBLogger.info("执行DDL出错"+e);
        }
        finally
        {
        	if (stat != null) {
                try {
                	stat.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					MKBLogger.error("Exception:" + e.toString());
				}
            }
        }
    }
    
    private File getFilePath(HttpServletRequest request){
            String resource = request.getServletContext().getRealPath("/WEB-INF/bot_kb_sql");
            //request.getServletContext().getRealPath("/WEB-INF/mkbsql");
            MKBLogger.info(resource);
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
    
    private String checkPropertyConfig(Map<String,String> map, String filename){
        for(String key:map.keySet()){
            if(key==null || key == ""){
                String reason = map.get(key) + "里没有"+key+"的配置！";
                return reason;
            }else{
                return null;
            }
        }
        return null;
    }
    public static void main(String[] args) {
    	 //通过 HTTP 创建core
        String httpUrl = "http://127.0.0.1:8080/kb/admin/cores";
        // config=solrconfig.xml&schema=schema.xml&dataDir=data  &config={2}&schema={3}&dataDir={4}
        httpUrl = httpUrl + "?action=CREATE&name={0}&instanceDir={1}";
        MKBHttpClient httpclient = new MKBHttpClient();
        String destCorePath = "D:\\software\\apache-tomcat-8.5.15\\webapps\\kb\\solrhome\\kb_u_wanne_core\\";
        try {
			httpclient.doHttpGet(httpUrl, "kb_u_wanne_core","kb_u_wanne_core");
//        	httpclient.doHttpUriGet("http://127.0.0.1:8080/kb/admin/cores?action=STATUS&core=yycloudkbcore");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			MKBLogger.error("Exception:" + e.toString());
		}
	}

}
