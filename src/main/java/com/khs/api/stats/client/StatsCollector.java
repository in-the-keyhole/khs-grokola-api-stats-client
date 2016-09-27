package com.khs.api.stats.client;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

public class StatsCollector {

	private static StatsCollector INSTANCE = null;

	private Map<String,ApiMonitor> monitors = new TreeMap<String,ApiMonitor>();
	
	private StatsCollector() {
	}

	public static StatsCollector getInstance() {

		if (INSTANCE == null) {

			INSTANCE = new StatsCollector();
		}

		return INSTANCE;

	}
	

	public void add(String url, String type) {
		
		
		
		
	}
	
	public Collection<ApiMonitor> pull(String api) {
		
		Collection<ApiMonitor> results = new ArrayList<ApiMonitor>();
		
		
	
	  return results;	
		
		
	}
	
	
	
	
	
}
