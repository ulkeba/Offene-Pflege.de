package com.jidesoft.wizard;

import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextPane;

import com.jidesoft.dialog.PageList;

public class WizardDialog extends Dialog {

	public WizardDialog(JFrame jFrame, boolean b) {
		super(jFrame);
		// TODO Auto-generated constructor stub
	}

	public JPanel getContentPane() {
		// TODO Auto-generated method stub
		return null;
	}

	public void addText(String xx, Font arial14) {
		// TODO Auto-generated method stub

	}

	public void addSpace() {
		// TODO Auto-generated method stub

	}

	public void addComponent(JTextPane txt2, boolean b) {
		// TODO Auto-generated method stub

	}

	public void pack() {
		// TODO Auto-generated method stub

	}

	public class WidthObject {
		public int width;
	}

	public Dimension getPreferredSize() {
		// WidthObject Auto-generated method stub
		return null;
	}

	public WizardDialog getButtonPanel() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setPageList(PageList model) {
		// TODO Auto-generated method stub

	}

	public void setFinishAction(AbstractAction abstractAction) {
		// TODO Auto-generated method stub

	}

	public void setCancelAction(AbstractAction abstractAction) {
		// TODO Auto-generated method stub

	}

	public Object getButtonByName(String s) {
		return null;
	}

	public boolean closeCurrentPage(Object buttonByName) {
		// TODO Auto-generated method stub
		return false;
	}
}
