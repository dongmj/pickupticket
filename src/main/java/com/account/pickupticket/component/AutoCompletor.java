package com.account.pickupticket.component;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.List;
import java.util.Vector;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JTextField;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class AutoCompletor implements KeyListener, ItemListener {
	private static final Log _log = LogFactory.getLog(AutoCompletor.class);
	private JComboBox owner = null;
	private JTextField editor = null;
	private ComboBoxModel model = null;
	
	public AutoCompletor(JComboBox comboBox) {
		owner = comboBox;
		editor = (JTextField)comboBox.getEditor().getEditorComponent();
		editor.addKeyListener(this);
		model = comboBox.getModel();
		owner.addItemListener(this);
	}
	
	public void itemStateChanged(ItemEvent event) {
		if (event.getStateChange() == ItemEvent.SELECTED) {
			int caretPosition = editor.getCaretPosition();
			if (caretPosition != -1) {
				try {
					editor.moveCaretPosition(caretPosition);
				}catch (IllegalArgumentException ex) {
					_log.error("move caret position fail.", ex);
				}
			}
		}
	}

	public void keyPressed(KeyEvent arg0) {}

	public void keyReleased(KeyEvent e) {
		char ch = e.getKeyChar();
		if(ch == KeyEvent.CHAR_UNDEFINED || Character.isISOControl(ch)
				|| ch == KeyEvent.VK_DELETE) 
			return;
		int caretPosition = editor.getCaretPosition();
		String str = editor.getText();
		if(str.length() == 0)
			return;
		autoComplete(str, caretPosition);
	}

	protected void autoComplete(String strf, int caretPosition) {
		Object[] opts;
		opts = getMatchingOptions(strf.substring(0, caretPosition));
		if(owner != null) {
			model = new DefaultComboBoxModel(opts);
			owner.setModel(model);
		}
		if(opts.length > 0) {
			String str = opts[0].toString();
			if(caretPosition>editor.getText().length()) return;
			editor.setCaretPosition(caretPosition);
			editor.setText(editor.getText().trim().substring(0,caretPosition));
			if(owner != null) {
				try {
					owner.showPopup();
				} catch(Exception ex) {
					_log.error("show popup fail.", ex);
				}
			}
		}
	}
	
	protected Object[] getMatchingOptions(String str) {
		List v = new Vector();
		List v2 = new Vector();
		
		for(int k = 0; k < model.getSize(); k++) {
			Object itemObj = model.getElementAt(k);
			if(itemObj != null) {
				String item = itemObj.toString().toLowerCase();
				if(item.startsWith(str.toLowerCase()))
					v.add(model.getElementAt(k));
				else
					v2.add(model.getElementAt(k));
			}
		}
		for(int i = 0; i < v2.size(); i++)
			v.add(v2.get(i));
		if(v.isEmpty()) 
			v.add(str);
		
		return v.toArray();
	}
	
	public void keyTyped(KeyEvent arg0) {}

}
