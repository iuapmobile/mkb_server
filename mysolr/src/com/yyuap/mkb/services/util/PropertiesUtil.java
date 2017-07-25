package com.yyuap.mkb.services.util;

import java.io.InputStream;
import java.util.Properties;

public class PropertiesUtil {
	private static Properties jdbcProp = new Properties();
	private static InputStream ise = null;
	 static
	    {
	        try
	        {
	            try
	            {
	                ise = PropertiesUtil.class.getResourceAsStream("/com/yyuap/mkb/resource/jdbc.properties");
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
}
