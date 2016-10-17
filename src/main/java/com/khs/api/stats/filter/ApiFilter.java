package com.khs.api.stats.filter;

import java.io.IOException;
import java.util.logging.Level;
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
	private String apiServer = null;
	private boolean valid = false;
	private String token = null;
	private long referenceId = -1;
	private long watchThreshold = 10;
	private long sleep = 10000;
	
	@Override
	public void destroy() {
	}

	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
			throws IOException, ServletException {
	
		final HttpServletRequest request = (HttpServletRequest) servletRequest;
		
		String method = request.getMethod();
		String uri = request.getRequestURI();
	

	    if (thread == null && this.valid) {		
	       thread = new ApiPublishThread(); 	
	       thread.setServiceName(this.serviceName);
	       thread.setServer(this.apiServer);
	       thread.setReferenceId(this.referenceId);
	       thread.setThreshold(this.watchThreshold);
	       thread.setSleep(this.sleep);
		   Thread t = new Thread(thread);
		   t.setDaemon(true);	
		   t.start();		
		   LOG.info("API Watch Thread Started, will publish API's for every "+this.watchThreshold+" encounters... ");
	    }	
		
	     long start = System.currentTimeMillis();
		
		 filterChain.doFilter(servletRequest, servletResponse);
		
		 if (uri.matches(apiPattern) && this.valid) {
		       thread.add(uri, method, System.currentTimeMillis() - start);
		  }
		
		
		
	
		
	}

	@Override
	public void init(FilterConfig config) throws ServletException {
	
	    this.apiPattern = config.getInitParameter("api-pattern");
	    this.serviceName = config.getInitParameter("service-name");
	    this.apiServer = config.getInitParameter("grokola-server");
	    this.token = config.getInitParameter("token");
	    String slp = config.getInitParameter("sleep");
	    String threshold = config.getInitParameter("watch-threshold");
	    String refid = config.getInitParameter("reference-id");
	    
	    if (threshold != null) {
	    	try {
	    	 this.watchThreshold = new Long(threshold);
	    	} catch(NumberFormatException e) {
	    		LOG.log(Level.WARNING,"watch-threshold must be an integer" );   		
	    	}
	    }
	    
	    if (slp != null) {
	    	
	    	try {
	    		
	    		this.sleep = new Long(slp);
	    		
	    	} catch (NumberFormatException e) {
	    		LOG.log(Level.WARNING,"Sleep must be an number...");
	    	}
	    	
	    }
	    
	    if (token == null) {
	    	LOG.log(Level.SEVERE,"Integration Token required for API filter to be enabled...");	    	
	    }
	    
	    if (refid == null) {
	    	LOG.log(Level.SEVERE,"GrokOla Reference Id must be defined...");
	    } else {
	       this.referenceId = new Long(refid);
	    }
	    
	    if (this.apiServer == null) {
	       LOG.log(Level.SEVERE,"server must be specified in ApiFilter config...Api's will not be watched");	
	    }
	    
	    // mark true is api watch filter has a valid configuration
	    
	    this.valid = this.apiServer != null && this.referenceId >= 0 && this.token != null; 
	   
	
	}


	
}
