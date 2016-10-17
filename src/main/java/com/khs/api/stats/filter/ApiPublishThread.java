package com.khs.api.stats.filter;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.Stack;
import java.util.logging.Logger;

public class ApiPublishThread implements Runnable {

	private final static Logger LOG = Logger.getLogger(ApiPublishThread.class
			.getName());
	private Stack<String[]> apis = new Stack<String[]>();
	private String serviceName = null;
	private String grokola = "http://127.0.0.1:8080/sherpa/api/stats/test";
	private long threshold = 10;
	private String server;
	private long referenceId = 0;
	private String uri = "sherpa/api/stats";
	private String token = null;
	private long sleep = 10000;
		
	public long getSleep() {
		return sleep;
	}

	public void setSleep(long sleep) {
		this.sleep = sleep;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public long getReferenceId() {
		return referenceId;
	}

	public void setReferenceId(long referenceId) {
		this.referenceId = referenceId;
	}

	public String getServer() {
		return server;
	}

	public void setServer(String server) {
		this.server = server;
	}

	public long getThreshold() {
		return threshold;
	}

	public void setThreshold(long threshold) {
		this.threshold = threshold;
	}
	

	public String serviceName() {

		if (serviceName == null) {
			serviceName = hostName();
		}

		return serviceName;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public void add(String api, String method, long milliseconds) {
		apis.add(new String[] { api, method, ""+milliseconds });
	}

	public void emit() {

		String request = this.server+"/"+this.uri+"/"+this.referenceId;
		URL url = null;
		try {
			url = new URL(request);

			HttpURLConnection conn = null;

			conn = (HttpURLConnection) url.openConnection();

			byte[] postData = json().getBytes(StandardCharsets.UTF_8);
			int postDataLength = postData.length;

			conn.setDoOutput(true);
			//conn.setInstanceFollowRedirects(false);

			conn.setRequestMethod("POST");

			conn.setRequestProperty("Content-Type",
					"application/json");
			conn.setRequestProperty("charset", "utf-8");
			conn.setRequestProperty("Content-Length",
					Integer.toString(postDataLength));
			conn.setUseCaches(false);

			conn.setRequestProperty("token",this.token);
			
			DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
			wr.write(postData);
			wr.flush();
			wr.close();
			
			InputStream input = conn.getInputStream();
			
			int i = 0;
			while ((i = input.read()) >= 0 ) {
				
				LOG.info(""+(char) i);
			}
			
			
			LOG.info("API Stats published");

		} catch (IOException e) {

			LOG.info("Error accessing: " + grokola);
			LOG.info(e.toString());
		}

	}

	private String hostName() {

		InetAddress ip = null;
		String hostname = null;
		try {
			ip = InetAddress.getLocalHost();
			hostname = ip.getHostName();

		} catch (UnknownHostException e) {

			e.printStackTrace();
		}

		return hostname;

	}

	public String json() {

		StringBuffer json = new StringBuffer();
		long size = apis.size();
		json.append("[");
		while (size > 0) {
		 if (!apis.isEmpty()) {
			String[] v = apis.pop();
			json.append("{uri:\"" +v[0] + "\", method: \"" +v[1]+"\", duration: \"" + v[2]+"\", service: \"" + this.serviceName() +"\"}");
			size = apis.size();
			if (size > 0) {json.append(",");}
		 }
		}
		json.append("]");
		
		System.out.println("JSON - "+json.toString());

		return json.toString();
	}

	@Override
	public void run() {

		while (true) {

			if (apis.size() > threshold) {
				emit();
			}
					
			try {
				Thread.sleep(sleep);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

}
