package com.account.pickupticket.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import com.account.pickupticket.domain.GroupTicket;

import junit.framework.Assert;
import junit.framework.TestCase;

public class MainServiceTest extends TestCase {
	MainService service = new MainService();
	public void testConvertHtml2ObjectWithDT() throws IOException {
		StringBuilder sbSource = new StringBuilder();
		InputStream is = this.getClass().getResourceAsStream("团队预约.htm");
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		String data = null;
		while((data = br.readLine()) != null) {
			sbSource.append(data);
		}
		List<GroupTicket> resultList = service.convertString2Object(sbSource.toString());
		System.out.println(resultList);
		Assert.assertEquals(4, resultList.size());
	}

	public void testConvertHtml2ObjectWithDD() throws IOException {
		StringBuilder sbSource = new StringBuilder();
		InputStream is = this.getClass().getResourceAsStream("queryGroupTicketBook2.txt");
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		String data = null;
		while((data = br.readLine()) != null) {
			sbSource.append(data);
		}
		List<GroupTicket> resultList = service.convertString2Object(sbSource.toString());
		System.out.println(resultList);
		Assert.assertEquals(3, resultList.size());
	}
}
