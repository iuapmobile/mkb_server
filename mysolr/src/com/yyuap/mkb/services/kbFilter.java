package com.yyuap.mkb.services;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.hadoop.ipc.protocolPB.RefreshCallQueueProtocolClientSideTranslatorPB;

/**
 * Servlet Filter implementation class kbFilter
 */
@WebFilter("/kbFilter")
public  class kbFilter implements Filter {

    /**
     * Default constructor. 
     */
    public kbFilter() {
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see Filter#destroy()
	 */
	public void destroy() {
		// TODO Auto-generated method stub
	}
	

	/**
	 * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
	 */
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		// TODO Auto-generated method stub
		// place your code here
	    
	    /*
	    HttpServletRequest req = (HttpServletRequest)request;
        HttpServletResponse res = (HttpServletResponse)response;
	    String tenantid = request.getParameter("tenantid");
	    String reqPath = req.getRequestURL().toString()+"?"+req.getQueryString();
	    if(tenantid.equals("um8001")){
	        String url = "172.20.7.97:8001";
	        res.sendRedirect(reqPath.replace("123.103.9.206:7100", url));
	    }else if(tenantid.equals("um8002")){
	        String url = "172.20.7.97:8002";
            res.sendRedirect(reqPath.replace("123.103.9.206:7100", url));
	    }
	    */
		// pass the request along the filter chain
		chain.doFilter(request, response);
	}
    
    public void init(FilterConfig arg0) throws ServletException {
        // TODO Auto-generated method stub
        
    }


}
