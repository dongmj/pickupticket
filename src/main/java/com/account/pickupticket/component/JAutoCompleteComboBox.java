package com.account.pickupticket.component;

import java.util.List;
import java.util.Vector;

import javax.swing.ComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JTextField;

public class JAutoCompleteComboBox extends JComboBox {
	private AutoCompletor completer;
	
	public JAutoCompleteComboBox() {
		super();
		addCompleter();
	}
	
	private void addCompleter() {
		setEditable(true);
		completer = new AutoCompletor(this);
	}
	
	public void autoComplete(String str) {
		this.completer.autoComplete(str, str.length());
	}
	
	public JAutoCompleteComboBox(ComboBoxModel cm) {
		super(cm);
		addCompleter();
	}
	
	public JAutoCompleteComboBox(Object[] items) {
		super(items);
		addCompleter();
	}
	
	public JAutoCompleteComboBox(List v) {
		super((Vector) v);
		addCompleter();
	}
	
	public String getText() {
		return ( (JTextField) getEditor().getEditorComponent()).getText();
	}
	
	public void setText(String text) {
		( (JTextField) getEditor().getEditorComponent()).setText(text);
	}
	
	public boolean containsItem(String itemString) {
		for (int i = 0; i < this.getModel().getSize(); i++) {
			String _item = "" + this.getModel().getElementAt(i);
			if (_item.equals(itemString)) {
				return true;
			}
		}
		
		return false;
	}
}
