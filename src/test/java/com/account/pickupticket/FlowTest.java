package com.account.pickupticket;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.apache.commons.httpclient.HttpException;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.EntityBuilder;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;

import junit.framework.TestCase;

public class FlowTest extends TestCase {
	static final String LOGIN_GET_URL = "http://mzxjnt.people.com.cn/jnt/user/user_logon.jsp";
	static final String LOGIN_POST_URL = "http://mzxjnt.people.com.cn/jnt/web/user/login.action";
	static final String USER = "1043339277@qq.com", PASSWORD = "830418";
	Random rand = new Random();
	
	public void test() throws HttpException, IOException, InterruptedException {
		CloseableHttpClient httpclient = HttpClients.createDefault();
		CloseableHttpResponse postResponse = null;
		CloseableHttpResponse getResponse = null;
		HttpPost httppost = new HttpPost(LOGIN_POST_URL);
		List<NameValuePair> formparams = new ArrayList<>();
		formparams.add(new BasicNameValuePair("email", USER));
		formparams.add(new BasicNameValuePair("password", PASSWORD));
        httppost.setEntity(EntityBuilder.create().setParameters(formparams).setContentType(ContentType.create(URLEncodedUtils.CONTENT_TYPE, Charset.forName("UTF-8"))).setContentEncoding("UTF-8").build());
        
        httppost.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/42.0.2311.90 Safari/537.36");
        httppost.addHeader("Host", "mzxjnt.people.com.cn");
        
        String cookie = null;
        
        do {
        try {
        	System.out.println("executing request " + httppost.getURI()); 
            postResponse = httpclient.execute(httppost);
            HttpEntity entity = postResponse.getEntity();  
            if (entity != null) {  
                System.out.println("--------------------------------------");  
                System.out.println("Request Header: " + Arrays.toString(httppost.getAllHeaders())); 
                System.out.println("Response Header: " + Arrays.toString(postResponse.getAllHeaders()));
                if(postResponse.getLastHeader("Set-Cookie") != null) {
                	cookie = postResponse.getLastHeader("Set-Cookie").getValue().substring(0, 43);
//                	httppost.addHeader("Cookie", cookie);
                }
            }
        } finally {
        	if(postResponse != null)
        		postResponse.close();  
        }
        System.out.println("POST Status Code: " + postResponse.getStatusLine().getStatusCode());
        if(postResponse.getStatusLine().getStatusCode() == 302) {
	        System.out.println("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
	        try {
		        String getUrl = postResponse.getLastHeader("Location").getValue();
		        System.out.println("GET URL: " + getUrl);
		        HttpGet httpgets = new HttpGet(getUrl);
		    	httpgets.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/42.0.2311.90 Safari/537.36");
		    	httpgets.addHeader("Host", "mzxjnt.people.com.cn");
//		    	httpgets.addHeader("Cookie", cookie);
//		    	httpgets.addHeader("Referer", getUrl);
//		    	httppost.addHeader("Referer", getUrl);
//		    	httppost.addHeader("Origin", "http://mzxjnt.people.com.cn");
		    	getResponse = httpclient.execute(httpgets);
		    	System.out.println("GET Request Header: " + Arrays.toString(httpgets.getAllHeaders()));
		    	System.out.println("GET Response Header: " + Arrays.toString(getResponse.getAllHeaders()));
		    	System.out.println("GET Status Code: " + getResponse.getStatusLine().getStatusCode());
	        } finally {
	        	if(getResponse != null)
	        		getResponse.close();
	        }
        }
    	System.out.println("--------------------------------------");
    	TimeUnit.SECONDS.sleep(3);
        } while(postResponse.getLastHeader("Location").getValue().indexOf("Login") > 0);
	}
}
