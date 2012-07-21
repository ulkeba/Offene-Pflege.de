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
package op.care.planung;

import java.awt.event.*;
import javax.swing.*;
import javax.swing.GroupLayout;
import javax.swing.LayoutStyle;
import javax.swing.border.*;
import com.toedter.calendar.*;
import op.OPDE;
import op.tools.DlgException;
import op.tools.SYSCalendar;
import op.tools.SYSTools;

import java.awt.*;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;

/**
 * @author tloehr
 */
public class DlgPKontrolle extends javax.swing.JDialog {

    private long planid;

    /**
     * Creates new form DlgPKontrolle
     */
    public DlgPKontrolle(Frame parent, long planid) {
        super(parent, true);
        this.planid = planid;
        initComponents();

        Date datum = SYSCalendar.nowDBDate();

        jdcControl.setMinSelectableDate(SYSCalendar.addDate(datum, -7));
        jdcControl.setMaxSelectableDate(datum);
        jdcControl.setDate(datum);


        jdcNext.setMinSelectableDate(SYSCalendar.addDate(datum, 1));
        jdcNext.setMaxSelectableDate(SYSCalendar.addDate(datum, 365));
        jdcNext.setDate(SYSCalendar.addDate(datum, 60));
        this.setTitle("Planung überprüfen");
        lblTitel.setText("Kontrolle der Pflegeplanung");

        SYSTools.centerOnParent(parent, this);
        this.setVisible(true);
    }

    /**
     * This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        lblTitel = new JLabel();
        btnCancel = new JButton();
        btnApply = new JButton();
        jPanel1 = new JPanel();
        jScrollPane1 = new JScrollPane();
        txtBemerkung = new JTextArea();
        jdcControl = new JDateChooser();
        jdcNext = new JDateChooser();

        //======== this ========
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        Container contentPane = getContentPane();

        //---- lblTitel ----
        lblTitel.setFont(new Font("Dialog", Font.BOLD, 18));
        lblTitel.setText("Kontrolle der Pflegeplanung");

        //---- btnCancel ----
        btnCancel.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/cancel.png")));
        btnCancel.setText("Abbrechen");
        btnCancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                btnCancelActionPerformed(e);
            }
        });

        //---- btnApply ----
        btnApply.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/apply.png")));
        btnApply.setText("\u00dcbernehmen");
        btnApply.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                btnApplyActionPerformed(e);
            }
        });

        //======== jPanel1 ========
        {
            jPanel1.setBorder(new SoftBevelBorder(SoftBevelBorder.RAISED));

            //======== jScrollPane1 ========
            {

                //---- txtBemerkung ----
                txtBemerkung.setColumns(20);
                txtBemerkung.setLineWrap(true);
                txtBemerkung.setRows(5);
                txtBemerkung.setToolTipText("Ergebnis der \u00dcberpr\u00fcfung");
                txtBemerkung.setWrapStyleWord(true);
                jScrollPane1.setViewportView(txtBemerkung);
            }

            //---- jdcControl ----
            jdcControl.setToolTipText("Datum der \u00dcberpr\u00fcfung");

            GroupLayout jPanel1Layout = new GroupLayout(jPanel1);
            jPanel1.setLayout(jPanel1Layout);
            jPanel1Layout.setHorizontalGroup(
                jPanel1Layout.createParallelGroup()
                    .addGroup(GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                            .addComponent(jScrollPane1, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 437, Short.MAX_VALUE)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jdcControl, GroupLayout.PREFERRED_SIZE, 154, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 140, Short.MAX_VALUE)
                                .addComponent(jdcNext, GroupLayout.PREFERRED_SIZE, 143, GroupLayout.PREFERRED_SIZE)))
                        .addContainerGap())
            );
            jPanel1Layout.setVerticalGroup(
                jPanel1Layout.createParallelGroup()
                    .addGroup(GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane1, GroupLayout.DEFAULT_SIZE, 219, Short.MAX_VALUE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                            .addComponent(jdcNext, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addComponent(jdcControl, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                        .addContainerGap())
            );
        }

        GroupLayout contentPaneLayout = new GroupLayout(contentPane);
        contentPane.setLayout(contentPaneLayout);
        contentPaneLayout.setHorizontalGroup(
            contentPaneLayout.createParallelGroup()
                .addGroup(contentPaneLayout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(contentPaneLayout.createParallelGroup()
                        .addComponent(jPanel1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(lblTitel, GroupLayout.DEFAULT_SIZE, 467, Short.MAX_VALUE)
                        .addGroup(GroupLayout.Alignment.TRAILING, contentPaneLayout.createSequentialGroup()
                            .addComponent(btnApply)
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(btnCancel)))
                    .addContainerGap())
        );
        contentPaneLayout.setVerticalGroup(
            contentPaneLayout.createParallelGroup()
                .addGroup(contentPaneLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(lblTitel)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(jPanel1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(contentPaneLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                        .addComponent(btnCancel)
                        .addComponent(btnApply))
                    .addContainerGap())
        );
        setSize(501, 394);
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelActionPerformed
        this.setVisible(false);
        this.dispose();
    }//GEN-LAST:event_btnCancelActionPerformed

    public void dispose() {
        jdcControl.cleanup();
        jdcNext.cleanup();
        super.dispose();
    }

    private void btnApplyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnApplyActionPerformed
        try {

            // Neue Kontrolle
            PreparedStatement stmt;
            stmt = OPDE.getDb().db.prepareStatement("INSERT INTO PlanKontrolle (PlanID, Bemerkung, UKennung, Datum, Abschluss) VALUES (?,?,?,?,0)");
            stmt.setLong(1, this.planid);
            stmt.setString(2, txtBemerkung.getText());
            stmt.setString(3, OPDE.getLogin().getUser().getUKennung());
            stmt.setDate(4, new java.sql.Date(jdcControl.getDate().getTime()));
            stmt.executeUpdate();

            // Nächstes Kontrolldatum in der Planung eintragen.
            PreparedStatement stmt1;
            stmt1 = OPDE.getDb().db.prepareStatement("UPDATE Planung SET NKontrolle=? WHERE PlanID=?");
            stmt1.setDate(1, new java.sql.Date(jdcNext.getDate().getTime()));
            stmt1.setLong(2, this.planid);
            stmt1.executeUpdate();

        } catch (SQLException sql) {
            new DlgException(sql);
            sql.printStackTrace();
        }

        this.dispose();
        this.setVisible(false);
    }//GEN-LAST:event_btnApplyActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JLabel lblTitel;
    private JButton btnCancel;
    private JButton btnApply;
    private JPanel jPanel1;
    private JScrollPane jScrollPane1;
    private JTextArea txtBemerkung;
    private JDateChooser jdcControl;
    private JDateChooser jdcNext;
    // End of variables declaration//GEN-END:variables
}
