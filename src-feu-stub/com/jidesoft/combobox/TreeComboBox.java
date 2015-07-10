package com.jidesoft.combobox;

import java.awt.Component;
import java.util.List;

import javax.swing.ComboBoxModel;
import javax.swing.JComponent;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import op.tools.PopupPanel;

public class TreeComboBox extends Component {

	protected String convertElementToString(Object object) {

		return null;
	}

	public PopupPanel createPopupComponent() {
		return null;

	}

	protected JTree createTree(TreeModel model) {
		return null;

	}

	public TreeCellRenderer getCellRenderer() {
		return null;

	}

	protected JComponent getDelegateTarget() {
		return null;

	}

	public int getMaximumRowCount() {
		return 0;
	}

	public JTree getTree() {
		return null;
	}

	public TreeModel getTreeModel() {
		return null;
	}

	protected void initComponent(ComboBoxModel model) {
	}

	public boolean isDoubleClickExpand() {
		return false;
	}

	protected boolean isValidSelection(TreePath path) {
		return false;
	}

	protected List<TreePath> populateTreePaths(JTree tree) {
		return null;
	}

	public void setCellRenderer(TreeCellRenderer aRenderer) {
	}

	public void setDoubleClickExpand(boolean doubleClickExpand) {
	}

	public void setMaximumRowCount(int count) {
	}

	public void setTreeModel(TreeModel treeModel) {
	}

	protected void setupTree(JTree tree) {
	}

	public Object getSelectedItem() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setSelectedItem(DefaultMutableTreeNode node) {
		// TODO Auto-generated method stub
		
	}
}
