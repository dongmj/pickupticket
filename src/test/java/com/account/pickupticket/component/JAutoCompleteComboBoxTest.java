package com.account.pickupticket.component;

import java.util.Arrays;

import javax.swing.JComboBox;
import javax.swing.JFrame;

public class JAutoCompleteComboBoxTest {

	public static void main(String[] args) {
	    JFrame frame = new JFrame();
	    Object[] items = new Object[] {
	        "zzz","zba","aab","abc", "aab","dfg","aba", "hpp", "pp", "hlp"};
	    //排序内容
	    //java.util.ArrayList list = new java.util.ArrayList(Arrays.asList(items));
	    //Collections.sort(list);
	    //JComboBox cmb = new JAutoCompleteComboBox(list.toArray());
	    Arrays.sort(items);
	    JComboBox cmb = new JAutoCompleteComboBox(items);
	    cmb.setSelectedIndex(-1);
	    frame.getContentPane().add(cmb);
	    frame.setSize(400, 80);
	    frame.setVisible(true);
	    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
}
