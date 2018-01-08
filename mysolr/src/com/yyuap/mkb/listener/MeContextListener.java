package com.yyuap.mkb.listener;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.yyuap.mkb.log.MKBLogger;
import com.yyuap.mkb.services.util.PropertiesUtil;

public class MeContextListener implements ServletContextListener
{
    
    @Override
	public void contextDestroyed(ServletContextEvent arg0)
    {
    }
    
    @Override
	public void contextInitialized(ServletContextEvent arg0)
    {
    	    MKBLogger.info(arg0.getServletContext().getContextPath());
//            Thread t = new Thread(new Runnable()
//            {
//                
//                @Override
//                public void run()
//                {
//                    try
//                    {
//                    	MKBLogger.info(arg0.getServletContext().getContextPath());
//                        Thread.sleep(10 * 1000);
//                    }
//                    catch (InterruptedException e)
//                    {
//                    }
//                    PropertiesUtil.loadProperties(arg0);
//                }
//            });
//            t.start();
    	    PropertiesUtil.loadProperties(arg0);
     }
}
