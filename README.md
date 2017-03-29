# khs-grokola-api-stats-client

Java servlet filter that will send API call statistics to GrokOla. 

Installation
____________
Add the following dependency to your POM.xml 

	<dependency>
	 <groupId>com.keyholesoftware</groupId>
	 <artifactId>khs-grokola-api-stats-client</artifactId>
	 <version>0.0.3-SNAPSHOT</version>
	</dependency>
	
Configure the Api Stats Filter through `web.xml` or a Spring Java Config

#### Spring Java Config 

	...
	public FilterRegistrationBean someFilterRegistration() {
		    FilterRegistrationBean registration = new FilterRegistrationBean();
		    registration.setFilter(new ApiFilter());
		    registration.addUrlPatterns("/*");
		    registration.setName("apifilter");
		    registration.setOrder(1);
		    registration.addInitParameter("api-pattern","/sherpa/.*");
	    	registration.addInitParameter("service-name","GrokOla");
	    	registration.addInitParameter("grokola-server","http://localhost:8080");
	    	registration.addInitParameter("token", "3a4f66ac-4793-4c1f-9c6d-4c5ee1afd0f1");
	    	registration.addInitParameter("reference-id","284");
		    return registration;
	} 
	
	...

#### Web.xml


	<filter> 
	   <filter-name>apiFilter</filter-name>
	   <filter-class>com.khs.api.stats.filter.ApiFilter</filter-class>    
	   <init-param>
	    <param-name>url-patterns</param-name>
	    <param-value>/*</param-value>
	  </init-param>
	  <init-param>
	    <param-name>service-name</param-name>
	    <param-value>myapi.com</param-value>
	  </init-param> 
	   <init-param>
	    <param-name>grokola-server</param-name>
	    <param-value>http://localhost:8080</param-value>
	  </init-param>
	    <init-param>
	    <param-name>reference-id</param-name>
	    <param-value>284</param-value>
	  </init-param>
	  <init-param>
	    <param-name>token</param-name>
	    <param-value>3a4f66ac-4793-4c1f-9c6d-4c5ee1afd0f1</param-value>
	  </init-param>
	</filter> 
	<filter-mapping> 
	   <filter-name>apiFilter</filter-name>
	   <url-pattern>/sherpa/.*</url-pattern> 
	</filter-mapping> 

Configuration Options
---------------------
Api Stats watch configuration options are described below.

`api-pattern` - Regular expression pattern to apply to URL route. Matching patterns will send API stats to GrokOla.

`service-name` - Name of service or application using an `API`. If not specified, server machine name will be used.

`grokola-server` - Server address of GrokOla instance where API stats will be sent.

`reference-id` - The id or the GrokOla Wiki Reference. This can be obtained from your GrokOla instance.

`token` - GrokOla integration token, available from the GrokOla Admin page.

`watch-threshold` - <OPTIONAL>, number of api calls before being published to GrokOla server. Default is 50. 

`sleep` - <OPTIONAL>, amount of time in milliseconds the filter will sleep before emitting API stats to server. Default is 10 seconds.
