package com.account.pickupticket.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.httpclient.Cookie;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.filters.HasAttributeFilter;
import org.htmlparser.nodes.TagNode;
import org.htmlparser.tags.DefinitionList;
import org.htmlparser.tags.DefinitionListBullet;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

import com.account.pickupticket.domain.ApplyForm;
import com.account.pickupticket.domain.GroupTicket;
import com.account.pickupticket.utils.HttpUtils;
import com.account.pickupticket.utils.TimeUtils;

public class MainService {
	private static final Log _log = LogFactory.getLog(MainService.class);
	private static String _cookies = StringUtils.EMPTY;
	private static final String loginURL = "http://mzxjnt.people.com.cn/jnt/web/user/login.action";
	private static final String COOKIE_SEPERATOR = ";";
	public boolean doLogin(String loginEmail, String loginPwd) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("email", loginEmail);
		params.put("password", loginPwd);
		String result = HttpUtils.getInstance().doPost(loginURL, params, StringUtils.EMPTY);
		System.out.println(result);
		Cookie[] cookies = HttpUtils.getInstance().getClientInstance().getState().getCookies();
		if(ArrayUtils.isEmpty(cookies)) {
			_log.warn("login fail at " + TimeUtils.getCurrentDateWithCommonFormat());
			return false;
		}
		_log.info(Arrays.toString(cookies));
		if(ArrayUtils.isNotEmpty(cookies)) {
			for(Cookie c : cookies) {
				_cookies += c.toString() + COOKIE_SEPERATOR;
			}
		}
		return true;
	}
	
	private boolean checkCookie() {
		if(StringUtils.isBlank(_cookies)) {
			_log.warn("cookies is empty.");
			return false;
		} else 
			return true;
	}
	
	private static final String insertGroupTicketURL = "http://mzxjnt.people.com.cn/jnt/web/ticket/insertGroupTicket.action";
	private static final String SUCCESS_LABEL = "成功提示";
	private static final String FAIL_LABEL = "预约失败";
	public boolean doApply(ApplyForm form) {
		if(!checkCookie()) return false;
		Map<String, String> params = new HashMap<String, String>();
		params.put("groupTicket.visitDate", form.getVisitDate());
		params.put("groupTicket.visitTimeStart", form.getVisitTimeStart());
		params.put("groupTicket.visitTimeEnd", form.getVisitTimeEnd());
		params.put("groupTicket.expectPersonCount", Integer.toString(form.getExpectPersonCount()));
		String postResult = HttpUtils.getInstance().doPost(insertGroupTicketURL, params, _cookies);
		if(postResult.indexOf(SUCCESS_LABEL) > 0)
			return true;
		else if(postResult.indexOf(FAIL_LABEL) != -1)
			return false;
		else {
			_log.warn("unrecoginzed response body: " + postResult);
			return false;
		}
	}
	
	private static final String listGroupTicketURL = "http://mzxjnt.people.com.cn/jnt/web/ticket/queryGroupTicketBook.action";
	public List<GroupTicket> doQueryTicket() {
		if(!checkCookie()) return null;
		String getResult = HttpUtils.getInstance().doGet(listGroupTicketURL, _cookies);
		if(StringUtils.isEmpty(getResult))
			return null;
		else {
			return convertString2Object(getResult);
		}
	}
	
	private static final int VISIT_TIME = 30 * 60 * 1000;
	public List<GroupTicket> convertString2Object(String source) {
		try {
			List<GroupTicket> resultList = new ArrayList<GroupTicket>();
			Parser parser = new Parser();
			parser.setInputHTML(source);
			Map<String, String> dateMap = new HashMap<String, String>();
			NodeFilter filter = new HasAttributeFilter("class", "p1_tab01");
			NodeList nodes = parser.extractAllNodesThatMatch(filter);
			if(nodes != null && nodes.size() > 0) {
				for(Node node : nodes.toNodeArray()) {
					NodeList childList = node.getChildren();
					if(childList != null && childList.size() > 0) {
						for(Node cNode : childList.toNodeArray()) {
							if(cNode.getText().indexOf("dateBookSelect") > 0) {
								String nodeText = cNode.getText();
								int startPosition = nodeText.indexOf("dateBookSelect('");
								int endPosition = nodeText.indexOf("')", startPosition);
								String childText = cNode.getChildren().elementAt(0).getText();
								int endPosition2 = childText.indexOf(" ");
								dateMap.put(nodeText.substring(startPosition + "dateBookSelect('".length(), endPosition), TimeUtils.getCurrentWithFormat("yyyy-") + childText.substring(0, endPosition2));
							}
						}
					}
				}
			}
			parser.reset();
			filter = new HasAttributeFilter("class", "p1_tab02");
			nodes = parser.extractAllNodesThatMatch(filter);
			// 标识是否有明天的订单
			boolean hasTomorrowOrder = false;
			List<GroupTicket> tomorrowTemplate = new ArrayList<GroupTicket>();
			if(nodes != null && nodes.size() > 0) {
				for(Node node : nodes.toNodeArray()) {
					NodeList childList = node.getChildren();
					String nodeText = node.getText();
					if(node instanceof DefinitionList) {
					} else {
						continue;
					}
					DefinitionList listNode = (DefinitionList) node;
					String idAttribute = listNode.getAttribute("id");
					if(StringUtils.isBlank(idAttribute)) {
						_log.warn("id atrribute is empty of " + node.getText());
					}
					int beginPosition = idAttribute.indexOf("leftBook");
					if(beginPosition == -1) {
						beginPosition = idAttribute.indexOf("rightBook");
						if(beginPosition == -1) {
							_log.warn("unrecognized node: " + nodeText);
							continue;
						} else
							beginPosition += "rightBook".length();
					} else 
						beginPosition += "leftBook".length();
					String dateKey = idAttribute.substring(beginPosition);
					if(!dateMap.containsKey(dateKey)) {
						_log.warn("invalid date key: " + dateKey);
						continue;
					}
					List<Node> applyTimeList = new ArrayList<Node>();
					if(childList != null && childList.size() > 0) {
						for(Node cNode : childList.toNodeArray()) {
							if(cNode instanceof DefinitionListBullet) {
								NodeList ccList = cNode.getChildren();
								if(ccList != null && ccList.size() > 0)
									for(int i = 0; i < ccList.size(); i++) {
										Node ccNode = ccList.elementAt(i);
										if(ccNode instanceof TagNode && ((TagNode)ccNode).getTagName().equals("I")) {
											Node tempCCNode = ccList.elementAt(i + 2);
											if(tempCCNode instanceof TagNode && ((TagNode)tempCCNode).getTagName().equals("I")) {
												applyTimeList.add(ccList.elementAt(++i));
											}
										}
									}
							}
						}
					}
					if(CollectionUtils.isEmpty(applyTimeList)) {
						_log.warn("invalid node: " + childList.toHtml());
						continue;
					}
					for(Node tempNode : applyTimeList) {
						GroupTicket tmp = new GroupTicket();
						tmp.setVisitDate(dateMap.get(dateKey));
						Calendar current = Calendar.getInstance(Locale.ROOT);
						current.roll(Calendar.DAY_OF_MONTH, 1);
						if(tmp.getVisitDate().equals(TimeUtils.convertDate2String(current.getTime(), "yyyy-MM-dd"))) {
							hasTomorrowOrder = true;
						} else if(tmp.getVisitDate().equals(TimeUtils.getCurrentWithFormat("yyyy-MM-dd"))) {
							tomorrowTemplate.add(tmp);
						}
						
						Date temp = TimeUtils.convertString2Date(tmp.getVisitDate() + " " + tempNode.getText(), "yyyy-MM-dd HH:mm");
						long tempTime = temp.getTime();
						tempTime += VISIT_TIME;
						tmp.setVisitTimeStart(tempNode.getText());
						tmp.setVisitTimeEnd(TimeUtils.convertDate2String(new Date(tempTime), "HH:mm"));
						
						resultList.add(tmp);						
					}
				}
				
				if(CollectionUtils.isEmpty(tomorrowTemplate)) {
					tomorrowTemplate = loadTemplate();
				} else {
					saveTemplate(tomorrowTemplate);
				}
				
				if(!hasTomorrowOrder && CollectionUtils.isNotEmpty(tomorrowTemplate)) {
					for(GroupTicket todayTicket : tomorrowTemplate) {
						GroupTicket temp = new GroupTicket();
						Calendar current = Calendar.getInstance(Locale.ROOT);
						current.roll(Calendar.DAY_OF_MONTH, 1);
						temp.setVisitDate(TimeUtils.convertDate2String(current.getTime(), "yyyy-MM-dd"));
						temp.setVisitTimeStart(todayTicket.getVisitTimeStart());
						temp.setVisitTimeEnd(todayTicket.getVisitTimeEnd());
						
						resultList.add(temp);
					}
				}
			}
			
			return resultList;
		} catch (ParserException e) {
			_log.error("invalid source: " + source);
			return null;
		}
	}
	
	private static final String TICKET_TEMPLATE = "template.db";
	private void saveTemplate(List<GroupTicket> template) {
		File templatedb = new File(TICKET_TEMPLATE);
		ObjectOutputStream out = null;
		try {
			out = new ObjectOutputStream(new FileOutputStream(templatedb));
			out.writeObject(template);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
			if(out != null)
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
	}
	
	@SuppressWarnings("unchecked")
	private List<GroupTicket> loadTemplate() {
		File templatedb = new File(TICKET_TEMPLATE);
		if(!templatedb.exists())
			return new ArrayList<GroupTicket>();
		ObjectInputStream in = null;
		List<GroupTicket> result = new ArrayList<GroupTicket>();
		try {
			in = new ObjectInputStream(new FileInputStream(templatedb));
			result = (List<GroupTicket>) in.readObject();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} finally {
			if(in != null)
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
		
		return result;
	}
}
