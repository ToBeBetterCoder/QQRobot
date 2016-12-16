package org.cool.qqrobot.http;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.cool.qqrobot.common.Const;
import org.cool.qqrobot.entity.MyHttpRequest;
import org.cool.qqrobot.entity.MyHttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

public class MyHttpClient {
	private static final Logger logger = LoggerFactory.getLogger(MyHttpClient.class);
	private static final int MAX_TOTAL = Const.MAX_TOTAL;
	private static final int MAX_PER_ROUTE = Const.MAX_PER_ROUTE;
	private static final String ENCODING = Const.ENCODING_UTF_8;
	private static final Gson gson = new Gson();
	
	private PoolingHttpClientConnectionManager cm;
	private BasicCookieStore cookieStore;
	private CloseableHttpClient httpclient;
	/**
	 * 之前采用单例模式（网上都这么写），后来发现单例cookie就共享了，如果有多个用户登录，无法区分，所以现在每个用户管理自己的httpClient
	 * 个人理解：httpclient相当于一个浏览器，而httpclient线程池中的线程相当于一个浏览器的每一次请求
	 */
	private MyHttpClient() {
		super();
		this.cm = new PoolingHttpClientConnectionManager();
		cm.setMaxTotal(MAX_TOTAL);
		cm.setDefaultMaxPerRoute(MAX_PER_ROUTE);
		
		this.cookieStore = new BasicCookieStore();
		this.httpclient = HttpClients.custom()
				.setDefaultCookieStore(cookieStore)
				.setConnectionManager(cm)
				.build();
	}

	public static MyHttpClient getNewInstance() {
		return new MyHttpClient();	
	}
	/**
	 * 抛出异常是为了取消登录成功标志
	 * @param myHttpRequest
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public MyHttpResponse execute(MyHttpRequest myHttpRequest) throws Exception {
		logger.debug("----------------------------------------------------------------------request start----------------------------------------------------------------------");
		long startTime = System.currentTimeMillis();
		
		MyHttpResponse myHttpResponse = new MyHttpResponse();
		HttpRequestBase httpRequest = null;
		CloseableHttpResponse response = null;
		HttpEntity entity;
		String contentType;
		String entityStr;
		// GET请求
		if (HttpGet.METHOD_NAME.equals(myHttpRequest.getMethod())) {
			httpRequest = new HttpGet(myHttpRequest.getUrl());
		}
		// POST请求
		if (HttpPost.METHOD_NAME.equals(myHttpRequest.getMethod())) {
			httpRequest = new HttpPost(myHttpRequest.getUrl());
			// 入参设置（键值对）
			List<NameValuePair> nvps = new ArrayList<NameValuePair>();
			for (Entry<String, String> entry : myHttpRequest.getPostMap().entrySet()) {
				nvps.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
			}
			// paramString
			String paramString = myHttpRequest.getParamString();
			try {
				if (!nvps.isEmpty()) {
					((HttpPost) httpRequest).setEntity(new UrlEncodedFormEntity(nvps, ENCODING));
				}
				if (StringUtils.isNotBlank(paramString)) {
					((HttpPost) httpRequest).setEntity(new StringEntity(paramString, ENCODING));
				}
			} catch (UnsupportedEncodingException e) {
				logger.error("httpPost 入参编码异常", e);
				throw e;
			}
			try {
				logger.debug("requestParams:{}", EntityUtils.toString(((HttpPost) httpRequest).getEntity()));
			} catch (ParseException | IOException e) {
				logger.error("httpPost请求入参解析异常", e);
				throw e;
			}
		}
		// 设置header
		for (Entry<String, String> entry : myHttpRequest.getHeaderMap().entrySet()) {
			httpRequest.addHeader(entry.getKey(), entry.getValue());
		}
		logger.debug("httpRequest:{}", httpRequest.getURI().toString());
		try {
			response = httpclient.execute(httpRequest);
			myHttpResponse.setStatus(response.getStatusLine().getStatusCode());
			logger.debug("httpResponseStatus:{}", response.getStatusLine());
			entity = response.getEntity();
			// 图灵机器人API返回没有contentType
			contentType = entity.getContentType() == null ? "" : entity.getContentType().getValue().toLowerCase();
			myHttpResponse.setContentType(contentType);
			// 返回图片资源
			if (contentType.contains(MyHttpResponse.CONTENT_TYPE_IMAGE)) {
				myHttpResponse.setImageCode(EntityUtils.toByteArray(entity));
				logger.debug("httpResponse:{}", contentType);
			} else {
				// 返回文本信息
				entityStr = EntityUtils.toString(entity, ENCODING);
				// 非JSON结构
				if (contentType.contains(MyHttpResponse.CONTENT_TYPE_HTML) || contentType.contains(MyHttpResponse.CONTENT_TYPE_SCRIPT)) {
					myHttpResponse.setTextStr(entityStr);
				} else {
					// 原始JSON
					myHttpResponse.setTextStr(entityStr);
					// JSON结构转换成Map
					myHttpResponse.setJsonMap(gson.fromJson(entityStr, Map.class));
				}
				logger.debug("httpResponse:{}", entityStr);
			}
			myHttpResponse.setCookies(cookieStore.getCookies());
//			logger.debug("cookies:{}", myHttpResponse.getCookies());
			EntityUtils.consume(entity);
		} catch (ParseException | IOException e) {
			logger.error("httpRequest请求/解析异常", e);
			throw e;
		} finally {
			try {
				if (response != null) {
					response.close();
				}
			} catch (IOException e) {
				logger.error("httpGet response关闭异常", e);
				throw e;
			}
		}
		long endTime = System.currentTimeMillis();
		logger.debug("Time:{}ms", endTime - startTime);
		logger.debug("----------------------------------------------------------------------request end----------------------------------------------------------------------");
		return myHttpResponse;
	}
}
