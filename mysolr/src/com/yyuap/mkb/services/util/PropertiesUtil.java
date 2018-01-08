package com.yyuap.mkb.services.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Properties;

import javax.servlet.ServletContextEvent;
import javax.servlet.http.HttpServletRequest;

import com.yyuap.mkb.log.MKBLogger;

import sun.print.resources.serviceui;

public class PropertiesUtil {
	private static Properties jdbcProp = new Properties();
	private static InputStream ise = null;
	
	private static Properties prop = new Properties();
	private static InputStream propise = null;
//	 static
//	    {
//	        try
//	        {
//	            try
//	            {
//	            	ise = Thread.currentThread().getContextClassLoader().getResourceAsStream("jdbc.properties");
//	                //ise = PropertiesUtil.class.getResourceAsStream("/com/yyuap/mkb/resource/jdbc.properties");
//	                jdbcProp.load(ise);
//	            }
//	            finally
//	            {
//	                if (ise != null)
//	                {
//	                    ise.close();
//	                }
//	            }
//	        }
//	        catch (Exception e)
//	        {
//	        }
//	    }
	 public static void loadProperties(ServletContextEvent arg0){
		 try
	        {
	            try
	            {
	            	MKBLogger.info("加载jdbcproperties"+new FileInputStream(arg0.getServletContext().getRealPath("/WEB-INF/properties/jdbc.properties")));
	                ise = new FileInputStream(arg0.getServletContext().getRealPath("/WEB-INF/properties/jdbc.properties"));
	                
	                jdbcProp.load(ise);
	                
	                MKBLogger.info("加载config.properties"+new FileInputStream(arg0.getServletContext().getRealPath("/WEB-INF/properties/config.properties")));
	                propise = new FileInputStream(arg0.getServletContext().getRealPath("/WEB-INF/properties/config.properties"));
	                
	                prop.load(propise);
	                
	            }
	            finally
	            {
	                if (ise != null)
	                {
	                    ise.close();
	                }
	                if (propise != null)
	                {
	                	propise.close();
	                }
	            }
	        }
	        catch (Exception e)
	        {
	        }
	 }
	 
	 /**
	     * 获取jdbc.properties文件里的属性
	     * 
	     * @param key key
	     * @return value
	     */
	    public static String getJdbcString(String key)
	    {
	        return jdbcProp.getProperty(key);
	    } 
	    
	    
	    public static String getConfigPropString(String key)
	    {
	        return prop.getProperty(key);
	    } 
	    
	    public static void main(String[] args) {
//	    	MKBLogger.info(request.getContextPath());
			//MKBLogger.info(Thread.currentThread().getContextClassLoader().getResourceAsStream("D:/software/apache-tomcat-8.5.15/webapps/kb/WEB-INF/lib/jdbc.properties"));
			try {
				InputStream a = new FileInputStream("/D:/software/apache-tomcat-8.5.15/webapps/kb/WEB-INF/properties/jdbc.properties");
				jdbcProp.load(a);
				MKBLogger.info(getJdbcString("db_port"));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }

}
