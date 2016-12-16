package org.cool.qqrobot.entity;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.apache.http.cookie.Cookie;

/**
 * 封装返回信息
 * @author zhoukl
 *
 */
public class MyHttpResponse {
	public static final int S_OK = 200;
	public static final int S_NOT_MODIFIED = 304;
	public static final int S_BAD_REQUEST = 400;
	public static final int S_NOT_AUTHORIZED = 401;
	public static final int S_FORBIDDEN = 403;
	public static final int S_NOT_FOUND = 404;
	public static final int S_NOT_ACCEPTABLE = 406;
	public static final int S_INTERNAL_SERVER_ERROR = 500;
	public static final int S_BAD_GATEWAY = 502;
	public static final int S_SERVICE_UNAVAILABLE = 503;
	public static final String CONTENT_TYPE_IMAGE = "image";
	public static final String CONTENT_TYPE_SCRIPT = "script";
	public static final String CONTENT_TYPE_HTML = "html";
	
	private int status;
	private String contentType;
	// 返回json格式则直接转换成map
	private Map<String, Object> jsonMap;
	// 返回html,script等文本类型，则转换成字符串
	private String textStr;
	// 返回二进制文件
	private byte[] imageCode;
	private List<Cookie> cookies;
	
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public String getContentType() {
		return contentType;
	}
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
	public Map<String, Object> getJsonMap() {
		return jsonMap;
	}
	public void setJsonMap(Map<String, Object> jsonMap) {
		this.jsonMap = jsonMap;
	}
	public String getTextStr() {
		return textStr;
	}
	public void setTextStr(String textStr) {
		this.textStr = textStr;
	}
	public byte[] getImageCode() {
		return imageCode;
	}
	public void setImageCode(byte[] imageCode) {
		this.imageCode = imageCode;
	}
	public List<Cookie> getCookies() {
		return cookies;
	}
	public void setCookies(List<Cookie> cookies) {
		this.cookies = cookies;
	}
	public String getCookiesValue(String key) {
		for (Cookie cookie : cookies) {
			if (key.equals(cookie.getName())) {
				 return cookie.getValue();
			}
		}
		return null;
	}
}
