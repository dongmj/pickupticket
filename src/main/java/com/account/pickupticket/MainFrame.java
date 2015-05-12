package com.account.pickupticket;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.account.pickupticket.domain.ApplyForm;
import com.account.pickupticket.domain.GroupTicket;
import com.account.pickupticket.service.MainService;

public class MainFrame extends JFrame {
	private static final long serialVersionUID = -3225929238856454128L;
	private static final Log _log = LogFactory.getLog(MainFrame.class);
	MainService service = new MainService();
	private static final String APP_TITLE = "毛主席纪念堂刷票工具";
	private JComboBox<String> groupTicketList = new JComboBox<String>();
	private JTextField internalInput = new JTextField(10);
	private JTextField personCountInput = new JTextField(10);
	private JTextArea resultList = new JTextArea(18, 42);
	private JButton btnRefresh = new JButton("刷新");
	private JButton btnStop = new JButton("停止");
	private static boolean processing = false;
	private Thread refreshThread = null;
	public MainFrame() {
		super(APP_TITLE);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(520, 450);
		setLocationRelativeTo(null);
		setLayout(new FlowLayout());
		
		add(new JLabel("批次"));
		add(groupTicketList);
		add(new JLabel("人数"));
		personCountInput.setText("30");
		add(personCountInput);
		add(new JLabel("刷新间隔"));
		internalInput.setText("10");
		add(internalInput);
		resultList.setEditable(false);
		JScrollPane tempPane = new JScrollPane(resultList);
		tempPane.setAutoscrolls(true);
		add(tempPane);
		btnRefresh.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				if(processing) {
					JOptionPane.showMessageDialog(null, "刷新已经在运行！", "提示", JOptionPane.INFORMATION_MESSAGE);
					return;
				}
				String groupTicketKey = (String) groupTicketList.getSelectedItem();
				if(!ticketCache.containsKey(groupTicketKey)) {
					JOptionPane.showMessageDialog(null, "数据错误！", "警告", JOptionPane.WARNING_MESSAGE);
					return;
				}
				
				String personCount = personCountInput.getText();
				if(StringUtils.isBlank(personCount)) {
					JOptionPane.showMessageDialog(null, "订座人数不能为空 ！", "警告", JOptionPane.WARNING_MESSAGE);
					return;
				}
				int tempCount = Integer.parseInt(personCount);
				if(tempCount < 10 || tempCount > 100) {
					JOptionPane.showMessageDialog(null, "订座人数只能在[1~100]之间！", "警告", JOptionPane.WARNING_MESSAGE);
					return;
				}
				
				String internal = internalInput.getText();
				if(StringUtils.isBlank(internal)) {
					JOptionPane.showMessageDialog(null, "刷新时间间隔不能为空！", "警告", JOptionPane.WARNING_MESSAGE);
					return;
				}
				int tempInternal = Integer.parseInt(internal);
				if(tempInternal <= 3) {
					internalInput.setText("3");
					resultList.append("刷新时间间隔最少为3秒\n");
				}
				
				
				if(refreshThread == null || !refreshThread.isAlive()) {
					refreshThread = new Thread() {
						@Override
						public void run() {
							String groupTicketKey = (String) groupTicketList.getSelectedItem();
							GroupTicket gTicket = ticketCache.get(groupTicketKey);
							String personCount = personCountInput.getText();
							String internal = internalInput.getText();
							processing = true;
							ApplyForm form = new ApplyForm();
							form.setVisitDate(gTicket.getVisitDate());
							form.setVisitTimeStart(gTicket.getVisitTimeStart());
							form.setVisitTimeEnd(gTicket.getVisitTimeEnd());
							form.setExpectPersonCount(Integer.parseInt(personCount));
							int i = 1;
							while(true) {
								if(!processing) {
									resultList.append("停止订座！\n");
									break;
								}
								if(service.doApply(form)) {
									resultList.append("第 " + i + " 次，尝试订座成功！\n");
									break;
								} else {
									resultList.append("第 " + i + " 次，尝试订座失败！\n");
								}
								try {
									TimeUnit.SECONDS.sleep(Long.parseLong(internal));
								} catch (NumberFormatException e1) {
									_log.error("format error.", e1);
									break;
								} catch (InterruptedException e1) {
									_log.error("interrupted.", e1);
									resultList.append("停止订座！\n");
									break;
								}
								i++;
							}
							processing = false;			
						}
					};					
				}
				
				refreshThread.setDaemon(true);
				refreshThread.setUncaughtExceptionHandler(new UncaughtExceptionHandler() {

					public void uncaughtException(Thread t, Throwable e) {
						_log.error("刷新票主线程异常退出！", e);
					}
					
				});
				refreshThread.start();
			}
			
		});
		add(btnRefresh);
		btnStop.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				if(!processing)
					return;
				if(refreshThread != null && refreshThread.isAlive())
					refreshThread.interrupt();
				processing = false;
			}
			
		});
		add(btnStop);
	}
	
	Map<String, GroupTicket> ticketCache = new HashMap<String, GroupTicket>();
	
	public void refreshGroupTicketList() {
		List<GroupTicket> ticketList = service.doQueryTicket();
		if(CollectionUtils.isNotEmpty(ticketList)) {
			for(GroupTicket gTicket : ticketList) {
				String tempKey = gTicket.getVisitDate() + " " + gTicket.getVisitTimeStart();
				groupTicketList.addItem(tempKey);
				ticketCache.put(tempKey, gTicket);
			}
//			groupTicketList.addItem("随机时间");
		}
	}
}
