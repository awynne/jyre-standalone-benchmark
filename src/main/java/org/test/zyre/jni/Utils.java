package org.test.zyre.jni;

import java.util.HashMap;

import com.fasterxml.jackson.databind.ObjectMapper;

public class Utils {
	
	@SuppressWarnings("unchecked")
	public static HashMap<String,String> jsonZmsgToMap(String json) {
		HashMap<String, String> map = new HashMap<String,String>();
		try {
			map = new ObjectMapper().readValue(json, HashMap.class);
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
		
		return map;
	}

}
