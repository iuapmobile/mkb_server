package com.yyuap.mkb.services.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Properties;

import javax.servlet.ServletContextEvent;
import javax.servlet.http.HttpServletRequest;

import sun.print.resources.serviceui;

public class PropertiesUtil {
	private static Properties jdbcProp = new Properties();
	private static InputStream ise = null;
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
	            	System.out.println("加载properties"+new FileInputStream(arg0.getServletContext().getRealPath("/WEB-INF/properties/jdbc.properties")));
	                ise = new FileInputStream(arg0.getServletContext().getRealPath("/WEB-INF/properties/jdbc.properties"));
	                
	                jdbcProp.load(ise);
	            }
	            finally
	            {
	                if (ise != null)
	                {
	                    ise.close();
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
	    public static void main(String[] args) {
//	    	System.out.println(request.getContextPath());
			System.out.println(Thread.currentThread().getContextClassLoader().getResourceAsStream("D:/software/apache-tomcat-8.5.15/webapps/kb/WEB-INF/lib/jdbc.properties"));
			try {
				InputStream a = new FileInputStream("/D:/software/apache-tomcat-8.5.15/webapps/kb/WEB-INF/properties/jdbc.properties");
				jdbcProp.load(a);
				System.out.println(getJdbcString("port"));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }

}
