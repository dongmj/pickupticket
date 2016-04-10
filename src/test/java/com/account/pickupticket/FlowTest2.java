package com.account.pickupticket;

import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.lang.StringUtils;

import junit.framework.TestCase;

public class FlowTest2 extends TestCase {
	static final String LOGIN_POST_URL = "http://mzxjnt.people.com.cn/jnt/web/user/login.action";
	static final String USER = "1043339277@qq.com", PASSWORD = "830418";
	static final HttpClient _httpclient = new HttpClient();
	
	String cookies = null;
	PostMethod post = null;
	public void test() throws HttpException, IOException, InterruptedException {
		do {
			System.out.println("--------------------------------------");
			post = new PostMethod(LOGIN_POST_URL);
			post.addParameter("email", USER);
			post.addParameter("password", PASSWORD);
			int result = _httpclient.executeMethod(post);
			System.out.println("POST result: " + result);
			System.out.println("POST request header: " + Arrays.toString(post.getRequestHeaders()));
			System.out.println("POST response header: " + Arrays.toString(post.getResponseHeaders()));
			if(post.getResponseHeader("Set-Cookie") != null) {
				cookies = post.getResponseHeader("Set-Cookie").getValue().substring(0, 43);
			}
			if(result == 302) {
				System.out.println("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
				String getUrl = post.getResponseHeader("Location").getValue();
				System.out.println("GET Url: " + getUrl);
				GetMethod get = new GetMethod(getUrl);
				result = _httpclient.executeMethod(get);
				System.out.println("GET result: " + result);
				System.out.println("GET request header: " + Arrays.toString(get.getRequestHeaders()));
				System.out.println("GET response header: " + Arrays.toString(get.getResponseHeaders()));
			}
			System.out.println("--------------------------------------");
			TimeUnit.SECONDS.sleep(3);
		} while(post.getResponseHeader("Location").getValue().indexOf("UserLogin") > 0);
		System.out.println(post.getResponseHeader("Location").getValue());
	}
}
