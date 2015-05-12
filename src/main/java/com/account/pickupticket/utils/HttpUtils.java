package com.account.pickupticket.utils;

import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class HttpUtils {
	private static final HttpClient _httpclient = new HttpClient();
	
	private HttpUtils() {}
	
	private static final HttpUtils _single_instance = new HttpUtils();
	
	private static final Log _log = LogFactory.getLog(HttpUtils.class);
	
	public static HttpUtils getInstance() {
		return _single_instance;
	}
	
	public HttpClient getClientInstance() {
		return _httpclient;
	}
	
	public String doPost(String url, Map<String, String> params, String cookies) {
		PostMethod post = new PostMethod(url);
		if(MapUtils.isNotEmpty(params)) {
			_log.info("doPost parameters: " + params);
			for(Entry<String, String> param : params.entrySet()) {
				post.addParameter(param.getKey(), param.getValue());
			}
		}
		if(StringUtils.isNotBlank(cookies)) 
			post.setRequestHeader("cookie", cookies);
		try {
			int result = _httpclient.executeMethod(post);
			_log.info("post " + url + "\nresult: " + result);
			_log.info(post.getResponseBodyAsStream());
			return post.getResponseBodyAsString();
		} catch (HttpException e) {
			_log.error("invoke post fail.", e);
		} catch (IOException e) {
			_log.error("invoke post fail.", e);
		} finally {
			post.releaseConnection();
		}
		return StringUtils.EMPTY;
	}
	
	public String doGet(String url, String cookies) {
		GetMethod get = new GetMethod(url);
		if(StringUtils.isNotBlank(cookies)) 
			get.setRequestHeader("cookie", cookies);
		try {
			int result = _httpclient.executeMethod(get);
			_log.info("get " + url + "\nresult: " + result);
			_log.info(get.getResponseBodyAsStream());
			return get.getResponseBodyAsString();
		} catch (HttpException e) {
			_log.error("invoke get fail.", e);
		} catch (IOException e) {
			_log.error("invoke get fail.", e);
		}
		return StringUtils.EMPTY;
	}
}
