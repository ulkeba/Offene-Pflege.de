/*
 * OffenePflege
 * Copyright (C) 2008 Torsten Löhr
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
 * 
 */

package op.care.med;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import entity.EntityTools;
import entity.verordnungen.DarreichungTools;
import entity.verordnungen.MedPackung;
import entity.verordnungen.MedPackungTools;
import op.tools.SYSTools;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.math.BigDecimal;
import java.text.DecimalFormat;

/**
 * @author tloehr
 */
public class DlgPack extends javax.swing.JDialog {
    private MedPackung packung;

    /**
     * Creates new form DlgPack
     */
    public DlgPack(JFrame parent, String title, MedPackung packung) {
        super(parent, true);
        initComponents();
        setTitle(title);
        this.packung = packung;
        cmbGroesse.setModel(new DefaultComboBoxModel(MedPackungTools.GROESSE));

        if (packung.getMpid() != null) {
            txtPZN.setText(SYSTools.catchNull(packung.getPzn()));
            txtInhalt.setText(packung.getInhalt().setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString());
            cmbGroesse.setSelectedIndex(packung.getGroesse());
        }
        lblPackEinheit.setText(DarreichungTools.getPackungsEinheit(packung.getDarreichung()));
        SYSTools.centerOnParent(parent, this);
        pack();
        setVisible(true);
    }

    /**
     * This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Erzeugter Quelltext ">//GEN-BEGIN:initComponents
    private void initComponents() {
        jLabel1 = new JLabel();
        jSeparator1 = new JSeparator();
        lblPZN = new JLabel();
        cmbGroesse = new JComboBox();
        jLabel3 = new JLabel();
        txtPZN = new javax.swing.JFormattedTextField(new DecimalFormat("0000000"));;
        lblInhalt = new JLabel();
        txtInhalt = new javax.swing.JFormattedTextField(new DecimalFormat("#####.##"));;
        jSeparator2 = new JSeparator();
        btnCancel = new JButton();
        btnOK = new JButton();
        lblPackEinheit = new JLabel();

        //======== this ========
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setResizable(false);
        Container contentPane = getContentPane();
        contentPane.setLayout(new FormLayout(
            "$rgap, $lcgap, default, 2*($lcgap, default:grow), $lcgap, default, $lcgap, $rgap",
            "6*(fill:default, $lgap), fill:default"));

        //---- jLabel1 ----
        jLabel1.setFont(new Font("Dialog", Font.BOLD, 14));
        jLabel1.setText("Packung");
        contentPane.add(jLabel1, CC.xywh(3, 1, 7, 1));
        contentPane.add(jSeparator1, CC.xywh(3, 3, 7, 1));

        //---- lblPZN ----
        lblPZN.setText("PZN:");
        contentPane.add(lblPZN, CC.xy(3, 5));

        //---- cmbGroesse ----
        cmbGroesse.setModel(new DefaultComboBoxModel(new String[] {
            "Item 1",
            "Item 2",
            "Item 3",
            "Item 4"
        }));
        contentPane.add(cmbGroesse, CC.xywh(5, 7, 5, 1));

        //---- jLabel3 ----
        jLabel3.setText("Gr\u00f6\u00dfe:");
        contentPane.add(jLabel3, CC.xy(3, 7));

        //---- txtPZN ----
        txtPZN.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                txtPZNFocusGained(e);
            }
        });
        contentPane.add(txtPZN, CC.xywh(5, 5, 5, 1));

        //---- lblInhalt ----
        lblInhalt.setText("Inhalt:");
        contentPane.add(lblInhalt, CC.xy(3, 9));

        //---- txtInhalt ----
        txtInhalt.setHorizontalAlignment(SwingConstants.RIGHT);
        txtInhalt.setText("0");
        txtInhalt.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                txtInhaltFocusGained(e);
            }
        });
        contentPane.add(txtInhalt, CC.xywh(5, 9, 3, 1));
        contentPane.add(jSeparator2, CC.xywh(3, 11, 7, 1));

        //---- btnCancel ----
        btnCancel.setIcon(new ImageIcon(getClass().getResource("/artwork/16x16/cancel.png")));
        btnCancel.setText("Abbrechen");
        btnCancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                btnCancelActionPerformed(e);
            }
        });
        contentPane.add(btnCancel, CC.xywh(7, 13, 3, 1));

        //---- btnOK ----
        btnOK.setIcon(new ImageIcon(getClass().getResource("/artwork/16x16/apply.png")));
        btnOK.setText("OK");
        btnOK.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                btnOKActionPerformed(e);
            }
        });
        contentPane.add(btnOK, CC.xy(5, 13));

        //---- lblPackEinheit ----
        lblPackEinheit.setText("jLabel5");
        contentPane.add(lblPackEinheit, CC.xy(9, 9));
        pack();
        setLocationRelativeTo(getOwner());
    }// </editor-fold>//GEN-END:initComponents

    private void txtInhaltFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtInhaltFocusGained
        ((JTextField) evt.getSource()).selectAll();
    }//GEN-LAST:event_txtInhaltFocusGained

    private void txtPZNFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtPZNFocusGained
        ((JTextField) evt.getSource()).selectAll();
    }//GEN-LAST:event_txtPZNFocusGained

    private void btnOKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOKActionPerformed

        String pzn = MedPackungTools.checkNewPZN(txtPZN.getText().trim(), packung.getMpid() != null ? packung : null);
        BigDecimal inhalt = SYSTools.parseBigDecimal(txtInhalt.getText());
        if (inhalt.compareTo(BigDecimal.ZERO) <= 0){
            inhalt = null;
        }

        String txt = "";

        if (pzn == null) {
            lblPZN.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/bw/editdelete.png")));
        } else {
            lblPZN.setIcon(null);
        }

        if (inhalt == null) {
            lblInhalt.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/bw/editdelete.png")));
        } else {
            lblInhalt.setIcon(null);
        }


        if (pzn != null && inhalt != null) {
            packung.setPzn(txtPZN.getText());
            packung.setGroesse((short) cmbGroesse.getSelectedIndex());
            packung.setInhalt(inhalt);

            if (packung.getMpid() != null) {
                packung = EntityTools.merge(packung);
            } else {
                EntityTools.persist(packung);

            }
            dispose();
        }


//        HashMap hm = new HashMap();
//        Number Inhalt = (Number) txtInhalt.getValue();
//        hm.put("PZN", txtPZN.getText());
//        hm.put("Groesse", cmbGroesse.getSelectedIndex());
//        hm.put("Inhalt", Inhalt.doubleValue());

    }//GEN-LAST:event_btnOKActionPerformed

    private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelActionPerformed
        dispose();
    }//GEN-LAST:event_btnCancelActionPerformed


    // Variablendeklaration - nicht modifizieren//GEN-BEGIN:variables
    private JLabel jLabel1;
    private JSeparator jSeparator1;
    private JLabel lblPZN;
    private JComboBox cmbGroesse;
    private JLabel jLabel3;
    private JFormattedTextField txtPZN;
    private JLabel lblInhalt;
    private JTextField txtInhalt;
    private JSeparator jSeparator2;
    private JButton btnCancel;
    private JButton btnOK;
    private JLabel lblPackEinheit;
    // Ende der Variablendeklaration//GEN-END:variables

}
