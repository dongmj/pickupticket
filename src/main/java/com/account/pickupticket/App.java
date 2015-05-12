package com.account.pickupticket;


/**
 * Hello world!
 *
 */
public class App 
{
	public static void main(String[] args) {
		MainFrame main = new MainFrame();
		main.setVisible(true);
		LoginDialog loginDlg = new LoginDialog(main);
		loginDlg.setVisible(true);
	}
}
