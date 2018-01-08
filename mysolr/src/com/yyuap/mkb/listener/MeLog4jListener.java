package com.yyuap.mkb.listener;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.PropertyConfigurator;

import com.yyuap.mkb.log.MKBLogger;
import com.yyuap.mkb.services.util.PropertiesUtil;

public class MeLog4jListener implements ServletContextListener
{
    
	@Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        ServletContext ctx = servletContextEvent.getServletContext();
        String prefix = ctx.getRealPath("/");
        // Log4J
        String log4jFile = ctx.getInitParameter("log4j");
        String log4jConfigPath = prefix + log4jFile;
        PropertyConfigurator.configure(log4jConfigPath);
        MKBLogger.info("initialized log4j finish");
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {

    }
}
