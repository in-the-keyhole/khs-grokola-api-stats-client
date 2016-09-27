package com.khs.api.stats.filter;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

public class ApiFilter implements Filter {
		
	private final static Logger LOG = Logger.getLogger(ApiPublishThread.class
			.getName());
	
	private ApiPublishThread thread = null;
	private String apiPattern = "/sherpa/.*";
	private String serviceName = null;
	private long threshold = 10; // publish every 10 api calls
	private String apiServer = null;
	private boolean valid = false;
	private String token = null;
	private long referenceId = -1;
	
	@Override
	public void destroy() {
	}

	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
			throws IOException, ServletException {
	
		final HttpServletRequest request = (HttpServletRequest) servletRequest;
 
		//check for Auth tokens...
		String userid = request.getHeader("userid");
		String token = request.getHeader("token");
		
		String method = request.getMethod();
		String uri = request.getRequestURI();
	

	    if (thread == null && this.valid) {		
	       thread = new ApiPublishThread(); 	
	       thread.setServiceName(this.serviceName);
	       thread.setServer(this.apiServer);
	       thread.setThreshold(this.threshold);
	       thread.setReferenceId(this.referenceId);
		   Thread t = new Thread(thread);
		   t.setDaemon(true);	
		   t.start();		
		   LOG.info("API Watch Thread Started, will publish API's for every "+this.threshold+" encounters... ");
	    }
	
		
	    if (uri.matches(apiPattern) && this.valid) {
	       thread.add(uri, method);
	    }
		
		filterChain.doFilter(servletRequest, servletResponse);
		
	
		
	}

	@Override
	public void init(FilterConfig config) throws ServletException {
	
	    this.apiPattern = config.getInitParameter("pattern");
	    this.serviceName = config.getInitParameter("service");
	    this.apiServer = config.getInitParameter("server");
	    String refid = config.getInitParameter("reference");
	    
	    if (refid == null) {
	    	LOG.info("GrokOla Reference Id must be defined...");
	    } else {
	       this.referenceId = new Long(refid);
	    }
	    
	    if (this.apiServer == null) {
	       LOG.info("server must be specified in ApiFilter config...Api's will not be watched");	
	    }
	    
	    // mark true is api watch filter has a valid configuration
	    
	    this.valid = this.apiServer != null && this.referenceId >= 0; 
	   
	
	}


	
}
