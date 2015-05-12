package com.account.pickupticket;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import org.apache.commons.lang.StringUtils;

import com.account.pickupticket.service.MainService;

public class LoginDialog extends JDialog {
	private static final long serialVersionUID = -7708135475261834863L;
	MainService service = new MainService();
	private JLabel loginEmail = new JLabel("登录邮箱");
	private JTextField loginEmailInput = new JTextField(20);
	private JLabel loginPwd = new JLabel("登录密码");
	private JTextField loginPwdInput = new JTextField(20);
	private JButton btnLogin = new JButton("登录");
//	private File userFile = new File("user.db");
	public LoginDialog(final JFrame parent) {
		super(parent, "登录", true);
		setLayout(new FlowLayout());
		setSize(300, 180);
		setLocationRelativeTo(null);
		
		add(loginEmail);
		
//		List<User> oldUsers = new ArrayList<User>();
//		if(!userFile.exists()) {
//			userFile.mkdir();
//		} else {
//			Properties props = new Properties();
//			try {
//				InputStream in = new BufferedInputStream(new FileInputStream(userFile));
//				props.load(in);
//				if(props.size() > 0) {
//					Enumeration en = props.propertyNames();
//					while(en.hasMoreElements()) {
//						User u = new User();
//						u.setLoginName((String) en.nextElement());
//						u.setLoginPwd(props.getProperty(u.getLoginName()));
//					}
//				}
//			} catch (FileNotFoundException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
//		
		add(loginEmailInput);
		add(loginPwd);
		add(loginPwdInput);
		btnLogin.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent arg0) {
				String loginEmail = loginEmailInput.getText();
				String loginPwd = loginPwdInput.getText();
				if(StringUtils.isEmpty(loginEmail) || StringUtils.isEmpty(loginPwd)) {
					JOptionPane.showMessageDialog(null, "登录邮箱或密码不能为空！", "注意", JOptionPane.ERROR_MESSAGE);
					return;
				}
				if(service.doLogin(loginEmail, loginPwd)) {
					((MainFrame)parent).refreshGroupTicketList();
					dispose();
				} else
					JOptionPane.showMessageDialog(null, "登录失败！", "注意", JOptionPane.ERROR_MESSAGE);
			}
			
		});
		add(btnLogin);
	}
}
