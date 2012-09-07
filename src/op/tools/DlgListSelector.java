/*
 * OffenePflege
 * Copyright (C) 2006-2012 Torsten Löhr
 * This program is free software; you can redistribute it and/or modify it under the terms of the 
 * GNU General Public License V2 as published by the Free Software Foundation
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even 
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General 
 * Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with this program; if not, write to 
 * the Free Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110, USA
 * www.offene-pflege.de
 * ------------------------ 
 * Auf deutsch (freie Übersetzung. Rechtlich gilt die englische Version)
 * Dieses Programm ist freie Software. Sie können es unter den Bedingungen der GNU General Public License, 
 * wie von der Free Software Foundation veröffentlicht, weitergeben und/oder modifizieren, gemäß Version 2 der Lizenz.
 *
 * Die Veröffentlichung dieses Programms erfolgt in der Hoffnung, daß es Ihnen von Nutzen sein wird, aber 
 * OHNE IRGENDEINE GARANTIE, sogar ohne die implizite Garantie der MARKTREIFE oder der VERWENDBARKEIT FÜR EINEN 
 * BESTIMMTEN ZWECK. Details finden Sie in der GNU General Public License.
 *
 * Sie sollten ein Exemplar der GNU General Public License zusammen mit diesem Programm erhalten haben. Falls nicht, 
 * schreiben Sie an die Free Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110, USA.
 */

package op.tools;

import com.jgoodies.forms.factories.*;
import com.jgoodies.forms.layout.*;
import op.OPDE;
import org.apache.commons.collections.Closure;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * @author tloehr
 */
public class DlgListSelector extends MyJDialog {
    private Closure applyClosure;
    private Object result;

    /**
     * Creates new form DlgFindeBW
     */
    public DlgListSelector(String topic, String detail, DefaultListModel dlm, Closure applyClosure) {
        super();
        initComponents();
        lstSelect.setModel(dlm);
        lstSelect.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        lstSelect.addListSelectionListener(new HandleSelections());
        btnApply.setEnabled(false);
        lblTopic.setText(topic);
        lblDetail.setText(detail);
        this.applyClosure = applyClosure;
    }

    /**
     * This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        lblTopic = new JLabel();
        lblDetail = new JLabel();
        jScrollPane1 = new JScrollPane();
        lstSelect = new JList();
        panel1 = new JPanel();
        btnApply = new JButton();
        btnCancel = new JButton();

        //======== this ========
        setResizable(false);
        setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
        Container contentPane = getContentPane();
        contentPane.setLayout(new FormLayout(
            "$rgap, $lcgap, default:grow, $lcgap, $rgap",
            "$rgap, 2*($lgap, fill:default), $lgap, fill:default:grow, $lgap, fill:default, $lgap, $rgap"));

        //---- lblTopic ----
        lblTopic.setFont(new Font("Dialog", Font.BOLD, 14));
        lblTopic.setText("jLabel1");
        contentPane.add(lblTopic, CC.xywh(3, 3, 2, 1));

        //---- lblDetail ----
        lblDetail.setFont(new Font("Dialog", Font.PLAIN, 12));
        lblDetail.setText("jLabel2");
        contentPane.add(lblDetail, CC.xywh(3, 5, 2, 1));

        //======== jScrollPane1 ========
        {

            //---- lstSelect ----
            lstSelect.setModel(new AbstractListModel() {
                String[] values = {
                    "Item 1",
                    "Item 2",
                    "Item 3",
                    "Item 4",
                    "Item 5"
                };
                @Override
                public int getSize() { return values.length; }
                @Override
                public Object getElementAt(int i) { return values[i]; }
            });
            lstSelect.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    lstSelectMouseClicked(e);
                }
            });
            jScrollPane1.setViewportView(lstSelect);
        }
        contentPane.add(jScrollPane1, CC.xywh(3, 7, 2, 1));

        //======== panel1 ========
        {
            panel1.setLayout(new BoxLayout(panel1, BoxLayout.LINE_AXIS));

            //---- btnApply ----
            btnApply.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/apply.png")));
            btnApply.setText("W\u00e4hlen");
            btnApply.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    btnApplyActionPerformed(e);
                }
            });
            panel1.add(btnApply);

            //---- btnCancel ----
            btnCancel.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/cancel.png")));
            btnCancel.setText("Abbrechen");
            btnCancel.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    btnCancelActionPerformed(e);
                }
            });
            panel1.add(btnCancel);
        }
        contentPane.add(panel1, CC.xy(3, 9, CC.RIGHT, CC.DEFAULT));
        setSize(600, 441);
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void lstSelectMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lstSelectMouseClicked
        if (evt.getClickCount() > 1) {
            btnApply.doClick();
        }
    }//GEN-LAST:event_lstSelectMouseClicked

    @Override
    public void dispose() {
        applyClosure.execute(result);
        super.dispose();
    }

    private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelActionPerformed
        result = null;
        dispose();
    }//GEN-LAST:event_btnCancelActionPerformed

//    public Object getSelection() {
//        super.setVisible(true);
//        Object result = null;
//        if (apply) {
//            ListModel lm = lstSelect.getModel();
//            ListSelectionModel lsm = lstSelect.getSelectionModel();
//            result = lm.getElementAt(lsm.getLeadSelectionIndex());
//        }
//        return result;
//    }

    private void btnApplyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnApplyActionPerformed
        ListModel lm = lstSelect.getModel();
        ListSelectionModel lsm = lstSelect.getSelectionModel();
        result = lm.getElementAt(lsm.getLeadSelectionIndex());
        dispose();

    }//GEN-LAST:event_btnApplyActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JLabel lblTopic;
    private JLabel lblDetail;
    private JScrollPane jScrollPane1;
    private JList lstSelect;
    private JPanel panel1;
    private JButton btnApply;
    private JButton btnCancel;
    // End of variables declaration//GEN-END:variables

    class HandleSelections implements ListSelectionListener {
        public void valueChanged(ListSelectionEvent lse) {
            // Erst reagieren wenn der Auswahl-Vorgang abgeschlossen ist.
            ListModel lm = lstSelect.getModel();

            if (lm.getSize() <= 0) {
                return;
            }

            if (!lse.getValueIsAdjusting()) {
                ListSelectionModel lsm = lstSelect.getSelectionModel();
                if (lsm.isSelectionEmpty()) {
                    btnApply.setEnabled(false);
                    //int selection = lsm.getLeadSelectionIndex();

                } else {
                    btnApply.setEnabled(true);
                }
            }

        }
    } // class HandleSelections
}
