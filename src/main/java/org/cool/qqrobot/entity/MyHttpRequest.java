package org.cool.qqrobot.entity;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.client.methods.HttpGet;


/**
 * 封装请求信息
 * @author zhoukl
 *
 */
public class MyHttpRequest {
	private String url;
	// 默认GET请求
	private String method = HttpGet.METHOD_NAME;
	@SuppressWarnings("serial")
	private Map<String, String> headerMap = new HashMap<String, String>(){
			{
				// 可以省略
				put("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/49.0.2623.75 Safari/537.36");
			}
		};
	// 初始化以避免空引用 适用于Content-Type:application/x-www-form-urlencoded
	private Map<String, String> postMap = new HashMap<String, String>();
	// 
	private String paramString;
	
	public MyHttpRequest() {
		super();
	}
	
	public MyHttpRequest(String method) {
		super();
		this.method = method;
	}
	
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getMethod() {
		return method;
	}
	public Map<String, String> getHeaderMap() {
		return headerMap;
	}
	public Map<String, String> getPostMap() {
		return postMap;
	}
	public void setPostMap(Map<String, String> postMap) {
		this.postMap = postMap;
	}

	public String getParamString() {
		return paramString;
	}

	public void setParamString(String paramString) {
		this.paramString = paramString;
	}
}
