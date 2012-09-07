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
 * 
 */
package op.care.values;

import com.jidesoft.pane.CollapsiblePane;
import com.jidesoft.pane.CollapsiblePanes;
import com.jidesoft.popup.JidePopup;
import com.jidesoft.swing.JideBoxLayout;
import com.jidesoft.swing.JideButton;
import com.toedter.calendar.JDateChooser;
import entity.BWerte;
import entity.BWerteTools;
import entity.info.Resident;
import entity.info.ResidentTools;
import entity.files.SYSFilesTools;
import entity.system.SYSPropsTools;
import op.OPDE;
import op.system.InternalClassACL;
import op.tools.DlgYesNo;
import op.threads.DisplayMessage;
import op.tools.*;
import org.apache.commons.collections.Closure;
import org.jdesktop.swingx.HorizontalLayout;
import org.jdesktop.swingx.VerticalLayout;
import tablemodels.TMWerte;
import tablerenderer.RNDHTML;

import javax.persistence.EntityManager;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * @author tloehr
 */
public class PnlVitalwerte extends NursingRecordsPanel {

    private Resident bewohner;

    private boolean initPhase;
    private JComboBox cmbAuswahl;
    private JPopupMenu menu;
    public static final String internalClassID = "nursingrecords.vitalparameters";

    private JScrollPane jspSearch;
    private CollapsiblePanes searchPanes;
    private JDateChooser jdcVon;
    private JToggleButton tbShowIDs, tbShowReplaced;

    /**
     * Creates new form pnlVitalwerte
     */
    public PnlVitalwerte(Resident bewohner, JScrollPane jspSearch) {
        initPhase = true;
        this.bewohner = bewohner;
        this.jspSearch = jspSearch;

        initComponents();
        prepareSearchArea();
        initPanel();
        switchResident(bewohner);
        initPhase = false;
    }

    private void initPanel() {
        tblVital.setSelectionMode(javax.swing.ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
    }

    /**
     * This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        jspTblVW = new JScrollPane();
        tblVital = new JTable();

        //======== this ========
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

        //======== jspTblVW ========
        {
            jspTblVW.addComponentListener(new ComponentAdapter() {
                @Override
                public void componentResized(ComponentEvent e) {
                    jspTblVWComponentResized(e);
                }
            });

            //---- tblVital ----
            tblVital.setModel(new DefaultTableModel(
                    new Object[][]{
                            {null, null, null, null},
                            {null, null, null, null},
                            {null, null, null, null},
                            {null, null, null, null},
                    },
                    new String[]{
                            "Title 1", "Title 2", "Title 3", "Title 4"
                    }
            ));
            tblVital.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    tblVitalMousePressed(e);
                }
            });
            jspTblVW.setViewportView(tblVital);
        }
        add(jspTblVW);
    }// </editor-fold>//GEN-END:initComponents

    @Override
    public void switchResident(Resident bewohner) {
        this.bewohner = bewohner;
        OPDE.getDisplayManager().setMainMessage(ResidentTools.getLabelText(bewohner));
        reloadTable();
    }

    @Override
    public void reload() {
        reloadTable();
    }

    private void printWerte(int[] sel) {
        try {
            TMWerte tm = (TMWerte) tblVital.getModel();
            ArrayList<BWerte> printlist = tm.getContent();

            if (sel != null) {
                printlist = new ArrayList<BWerte>(sel.length);
                for (int row : sel) {
                    printlist.add(tm.getBWert(row));
                }
            }

            // Create temp file.
            File temp = File.createTempFile(internalClassID, ".html");

            // Delete temp file when program exits.
            temp.deleteOnExit();

            // Write to temp file
            BufferedWriter out = new BufferedWriter(new FileWriter(temp));


            out.write(BWerteTools.getBWerteAsHTML(printlist));

            out.close();
            SYSFilesTools.handleFile(temp, Desktop.Action.OPEN);
        } catch (IOException e) {
        }

    }

    @Override
    public void cleanup() {
        SYSTools.unregisterListeners(this);
    }

    private void jspTblVWComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_jspTblVWComponentResized
        JViewport jv = (JViewport) tblVital.getParent();
        JScrollPane jsp = (JScrollPane) jv.getParent();
        Dimension dim = jsp.getSize();
        // Größe der Massnahmen Spalten ändern.
        int width = dim.width - 200; // größe - der fixen spalten
        TableColumnModel tcm1 = tblVital.getColumnModel();

        // Zu Beginn der Applikation steht noch ein standardmodell drin.
        // das hat nur 4 Spalten. solange braucht sich dieser handler nicht
        // damit zu befassen.
        if (tcm1.getColumnCount() < 3) {
            return;
        }

        tcm1.getColumn(0).setPreferredWidth(200);
        tcm1.getColumn(1).setPreferredWidth(width / 3 * 1);
        tcm1.getColumn(2).setPreferredWidth(width / 3 * 2);

        tcm1.getColumn(0).setHeaderValue(OPDE.lang.getString(internalClassID + ".tabheader1"));
        tcm1.getColumn(1).setHeaderValue(OPDE.lang.getString(internalClassID + ".tabheader2"));
        tcm1.getColumn(2).setHeaderValue(OPDE.lang.getString(internalClassID + ".tabheader3"));


    }//GEN-LAST:event_jspTblVWComponentResized

    private void reloadTable() {
        OPDE.getMainframe().setBlocked(true);
        OPDE.getDisplayManager().setProgressBarMessage(new DisplayMessage(OPDE.lang.getString("misc.msg.wait"), -1, 100));
        TableModel oldmodel = tblVital.getModel();
        tblVital.setModel(new DefaultTableModel());
        if (oldmodel != null && oldmodel instanceof TMWerte) {
            ((TMWerte) oldmodel).cleanup();
        }

        SwingWorker worker = new SwingWorker() {
            TableModel model;

            @Override
            protected Object doInBackground() throws Exception {
                model = new TMWerte(jdcVon.getDate(), bewohner, cmbAuswahl.getSelectedIndex(), tbShowReplaced.isSelected(), tbShowIDs.isSelected());
                return null;
            }

            @Override
            protected void done() {
                OPDE.getDisplayManager().setProgressBarMessage(null);
                OPDE.getMainframe().setBlocked(false);
                tblVital.setModel(model);
                tblVital.getColumnModel().getColumn(0).setCellRenderer(new RNDHTML());
                tblVital.getColumnModel().getColumn(1).setCellRenderer(new RNDHTML());
                tblVital.getColumnModel().getColumn(2).setCellRenderer(new RNDHTML());
                jspTblVW.dispatchEvent(new ComponentEvent(jspTblVW, ComponentEvent.COMPONENT_RESIZED));
            }
        };
        worker.execute();
    }

    private void tblVitalMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblVitalMousePressed

        Point p = evt.getPoint();
        Point p2 = evt.getPoint();
        // Convert a coordinate relative to a component's bounds to screen coordinates
        SwingUtilities.convertPointToScreen(p2, tblVital);
        final Point screenposition = p2;

        ListSelectionModel lsm = tblVital.getSelectionModel();

        boolean singleRowSelected = lsm.getMaxSelectionIndex() == lsm.getMinSelectionIndex();

        final int row = tblVital.rowAtPoint(p);
        final int col = tblVital.columnAtPoint(p);

        if (singleRowSelected) {
            lsm.setSelectionInterval(row, row);
        }

        SYSTools.unregisterListeners(menu);
        menu = new JPopupMenu();

        TMWerte tm = (TMWerte) tblVital.getModel();
        if (tm.getRowCount() > 0 && row > -1) {
            final BWerte wert = tm.getBWert(lsm.getLeadSelectionIndex());
            boolean bearbeitenMoeglich = !wert.isReplaced() && !wert.isDeleted() && singleRowSelected;

            if (evt.isPopupTrigger()) {

                if (OPDE.getAppInfo().userHasAccessLevelForThisClass(internalClassID, InternalClassACL.UPDATE)) {

//                // KORRIGIEREN
                    JMenuItem itemPopupEdit = new JMenuItem(OPDE.lang.getString("misc.commands.edit"), new ImageIcon(getClass().getResource("/artwork/22x22/bw/edit.png")));
                    itemPopupEdit.addActionListener(new java.awt.event.ActionListener() {

                        public void actionPerformed(java.awt.event.ActionEvent evt) {

                            final JidePopup popup = new JidePopup();
                            popup.setMovable(false);
                            popup.getContentPane().setLayout(new BoxLayout(popup.getContentPane(), BoxLayout.PAGE_AXIS));

                            final JComponent editor;

                            switch (col) {
                                case TMWerte.COL_PIT: {
                                    editor = new PnlUhrzeitDatum(wert.getPit());
                                    break;
                                }

                                case TMWerte.COL_CONTENT: {

                                    if (wert.getType() == BWerteTools.RR) {
                                        editor = new PnlWerte123(wert.getWert(), wert.getWert2(), wert.getWert3(), BWerteTools.RRSYS, BWerteTools.EINHEIT[BWerteTools.RR], BWerteTools.RRDIA, BWerteTools.EINHEIT[BWerteTools.RR], BWerteTools.WERTE[BWerteTools.PULS], BWerteTools.EINHEIT[BWerteTools.PULS]);
                                    } else if (wert.isOhneWert()) {
                                        editor = null;
                                    } else {
                                        editor = new PnlWerte123(wert.getWert(), BWerteTools.WERTE[wert.getType()], BWerteTools.EINHEIT[wert.getType()]);
                                    }
                                    break;
                                }

                                case TMWerte.COL_COMMENT: {
                                    editor = new JTextArea(SYSTools.catchNull(wert.getBemerkung()), 10, 40);
                                    ((JTextArea) editor).setLineWrap(true);
                                    ((JTextArea) editor).setWrapStyleWord(true);
                                    ((JTextArea) editor).setEditable(true);
                                    break;
                                }
                                default: {
                                    editor = null;
                                }
                            }

                            if (editor != null) {

                                JScrollPane pnlEditor = new JScrollPane(editor);

                                JPanel pnl = new JPanel(new BorderLayout(10, 10));

                                pnl.add(pnlEditor, BorderLayout.CENTER);


                                final JButton saveButton = new JButton(new ImageIcon(getClass().getResource("/artwork/22x22/apply.png")));
                                saveButton.addActionListener(new ActionListener() {
                                    @Override
                                    public void actionPerformed(ActionEvent actionEvent) {

                                        BWerte newOne = wert.clone();
                                        popup.hidePopup();

                                        switch (col) {
                                            case TMWerte.COL_PIT: {
                                                newOne.setPit(((PnlUhrzeitDatum) editor).getPIT());
                                                break;
                                            }
                                            case TMWerte.COL_CONTENT: {
                                                PnlWerte123 pnl123 = (PnlWerte123) editor;
                                                newOne.setWert(pnl123.getWert1());
                                                newOne.setWert2(pnl123.getWert2());
                                                newOne.setWert3(pnl123.getWert3());
                                                break;
                                            }
                                            case TMWerte.COL_COMMENT: {
                                                newOne.setBemerkung(((JTextArea) editor).getText().trim());
                                                break;
                                            }
                                            default: {
                                                newOne = null;
                                            }
                                        }

                                        if (newOne.isWrongValues()) {
                                            OPDE.getDisplayManager().addSubMessage(new DisplayMessage(OPDE.lang.getString("misc.msg.wrongentry"), DisplayMessage.WARNING));
                                        } else if (col == TMWerte.COL_COMMENT && newOne.getBemerkung().equals(wert.getBemerkung())) {
                                            OPDE.getDisplayManager().addSubMessage(new DisplayMessage(OPDE.lang.getString("misc.msg.nochanges"), DisplayMessage.WARNING));
                                        } else {
                                            newOne = BWerteTools.changeWert(wert, newOne);
                                            reloadTable();
                                        }
                                    }
                                });

                                saveButton.setHorizontalAlignment(SwingConstants.RIGHT);

                                JPanel buttonPanel = new JPanel();
                                buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.LINE_AXIS));
                                buttonPanel.add(saveButton);
                                pnl.setBorder(new EmptyBorder(10, 10, 10, 10));
                                pnl.add(buttonPanel, BorderLayout.SOUTH);

                                popup.setOwner(tblVital);
                                popup.removeExcludedComponent(tblVital);
                                popup.getContentPane().add(pnl);

                                popup.setDefaultFocusComponent(editor);
                                popup.showPopup(screenposition.x, screenposition.y);
                            }
                        }

                    });
                    menu.add(itemPopupEdit);
                    itemPopupEdit.setEnabled(bearbeitenMoeglich && (!wert.isOhneWert() || col != TMWerte.COL_CONTENT));
                }


                // Löschen
                if (OPDE.getAppInfo().userHasAccessLevelForThisClass(internalClassID, InternalClassACL.DELETE)) {


                    JMenuItem itemPopupDelete = new JMenuItem(OPDE.lang.getString("misc.commands.delete"), new ImageIcon(getClass().getResource("/artwork/22x22/bw/trashcan_empty.png")));
                    itemPopupDelete.addActionListener(new java.awt.event.ActionListener()

                    {

                        public void actionPerformed
                                (java.awt.event.ActionEvent
                                         evt) {
                            new DlgYesNo(OPDE.lang.getString("misc.questions.delete"), new ImageIcon(getClass().getResource("/artwork/48x48/bw/trashcan_empty.png")), new Closure() {
                                @Override
                                public void execute(Object answer) {
                                    if (answer.equals(JOptionPane.YES_OPTION)) {
                                        BWerte mywert = BWerteTools.deleteWert(wert);
                                        ((TMWerte) tblVital.getModel()).setBWert(row, mywert);

                                        if (!tbShowReplaced.isSelected()) {
                                            reloadTable();
                                        }
                                    }
                                }
                            });
                        }
                    }

                    );
                    menu.add(itemPopupDelete);
                    itemPopupDelete.setEnabled(bearbeitenMoeglich);
                }

                if (OPDE.getAppInfo().userHasAccessLevelForThisClass(internalClassID, InternalClassACL.PRINT)) {
                    JMenuItem itemPopupPrint = new JMenuItem("Markierte Werte drucken", new ImageIcon(getClass().getResource("/artwork/22x22/bw/printer.png")));
                    itemPopupPrint.addActionListener(new java.awt.event.ActionListener() {

                        public void actionPerformed(java.awt.event.ActionEvent evt) {
                            int[] sel = tblVital.getSelectedRows();
                            printWerte(sel);
                        }
                    });
                    menu.add(itemPopupPrint);
                }
            }


//                if (!alreadyEdited && singleRowSelected) {
//                    menu.add(new JSeparator());
//                    // #0000003
//                    menu.add(op.share.process.DBHandling.getVorgangContextMenu(parent, "BWerte", bwid, currentBW, fileActionListener));
//
//                    Query query = em.createNamedQuery("BWerte.findByBwid");
//                    query.setParameter("bwid", bwid);
//                    entity.BWerte bwert = (entity.BWerte) query.getSingleResult();
//                    menu.add(SYSFilesTools.getSYSFilesContextMenu(parent, bwert, fileActionListener));
//
//                    // #0000035
//                    //menu.add(SYSFiles.getOPFilesContextMenu(parent, "BWerte", bwid, currentBW, tblVital, true, true, SYSFiles.CODE_BERICHTE, fileActionListener));
//                }


//                if (OPDE.getAppInfo().userHasAccessLevelForThisClass(internalClassID, InternalClassACL.SELECT) && !alreadyEdited && singleRowSelected) {
//                    menu.add(new JSeparator());
//                    menu.add(QProcessTools.getVorgangContextMenu(parent, aktuellerWert, bewohner, standardActionListener));
//                }


        }

        menu.show(evt.getComponent(), (int) p.getX(), (int) p.getY());
    }//GEN-LAST:event_tblVitalMousePressed

    private void prepareSearchArea() {
        searchPanes = new CollapsiblePanes();
        searchPanes.setLayout(new JideBoxLayout(searchPanes, JideBoxLayout.Y_AXIS));
        jspSearch.setViewportView(searchPanes);

        searchPanes.add(addCommands());
        searchPanes.add(addFilters());

        searchPanes.addExpansion();
    }

    private CollapsiblePane addFilters() {
        JPanel labelPanel = new JPanel();
        labelPanel.setBackground(Color.WHITE);
        labelPanel.setLayout(new VerticalLayout(5));


        jdcVon = new JDateChooser();
        jdcVon.setDate(SYSCalendar.addField(SYSCalendar.today_date(), -2, GregorianCalendar.WEEK_OF_MONTH));
        jdcVon.setMaxSelectableDate(new Date());
        jdcVon.addPropertyChangeListener("date", new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
                reloadTable();
            }
        });

        labelPanel.add(jdcVon);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setLayout(new HorizontalLayout(5));
        buttonPanel.setBorder(new EmptyBorder(0, 0, 0, 0));

        JButton homeButton = new JButton(new ImageIcon(getClass().getResource("/artwork/32x32/bw/player_start.png")));
        homeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                BWerte first = BWerteTools.getFirstWert(bewohner);
                jdcVon.setDate(first == null ? SYSCalendar.addField(SYSCalendar.today_date(), -2, GregorianCalendar.WEEK_OF_MONTH) : first.getPit());
            }
        });
        homeButton.setPressedIcon(new ImageIcon(getClass().getResource("/artwork/32x32/bw/player_start_pressed.png")));
        homeButton.setBorder(null);
        homeButton.setBorderPainted(false);
        homeButton.setOpaque(false);
        homeButton.setContentAreaFilled(false);
        homeButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JButton backButton = new JButton(new ImageIcon(getClass().getResource("/artwork/32x32/bw/player_back.png")));
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                jdcVon.setDate(SYSCalendar.addDate(jdcVon.getDate(), -1));
            }
        });
        backButton.setPressedIcon(new ImageIcon(getClass().getResource("/artwork/32x32/bw/player_back_pressed.png")));
        backButton.setBorder(null);
        backButton.setBorderPainted(false);
        backButton.setOpaque(false);
        backButton.setContentAreaFilled(false);
        backButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));


        JButton fwdButton = new JButton(new ImageIcon(getClass().getResource("/artwork/32x32/bw/player_play.png")));
        fwdButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                jdcVon.setDate(SYSCalendar.addDate(jdcVon.getDate(), 1));
            }
        });
        fwdButton.setPressedIcon(new ImageIcon(getClass().getResource("/artwork/32x32/bw/player_play_pressed.png")));
        fwdButton.setBorder(null);
        fwdButton.setBorderPainted(false);
        fwdButton.setOpaque(false);
        fwdButton.setContentAreaFilled(false);
        fwdButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JButton endButton = new JButton(new ImageIcon(getClass().getResource("/artwork/32x32/bw/player_end.png")));
        endButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                jdcVon.setDate(SYSCalendar.addField(SYSCalendar.today_date(), -2, GregorianCalendar.WEEK_OF_MONTH));
            }
        });
        endButton.setPressedIcon(new ImageIcon(getClass().getResource("/artwork/32x32/bw/player_end_pressed.png")));
        endButton.setBorder(null);
        endButton.setBorderPainted(false);
        endButton.setOpaque(false);
        endButton.setContentAreaFilled(false);
        endButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));


        buttonPanel.add(homeButton);
        buttonPanel.add(backButton);
        buttonPanel.add(fwdButton);
        buttonPanel.add(endButton);


        labelPanel.add(buttonPanel);


        DefaultComboBoxModel dcbm = new DefaultComboBoxModel(BWerteTools.WERTE);
        dcbm.removeElementAt(0);
        dcbm.insertElementAt("<i>"+OPDE.lang.getString("misc.commands.noselection")+"</i>", 0);
        cmbAuswahl = new JComboBox(dcbm);
        cmbAuswahl.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent itemEvent) {
                if (initPhase || itemEvent.getStateChange() != ItemEvent.SELECTED) return;
                SYSPropsTools.storeState(internalClassID + ":cmbAuswahl", cmbAuswahl);
                reloadTable();
            }
        });
        labelPanel.add(cmbAuswahl);
        SYSPropsTools.restoreState(internalClassID + ":cmbAuswahl", cmbAuswahl);

        tbShowReplaced = GUITools.getNiceToggleButton(OPDE.lang.getString("misc.filters.showreplaced"));
        tbShowReplaced.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent itemEvent) {
                if (initPhase || itemEvent.getStateChange() != ItemEvent.SELECTED) return;
                SYSPropsTools.storeState(internalClassID + ":tbShowReplaced", tbShowReplaced);
                reloadTable();
            }
        });
        labelPanel.add(tbShowReplaced);
        SYSPropsTools.restoreState(internalClassID + ":tbShowReplaced", tbShowReplaced);
        tbShowReplaced.setHorizontalAlignment(SwingConstants.LEFT);

        tbShowIDs = GUITools.getNiceToggleButton(OPDE.lang.getString("misc.filters.showpks"));
        tbShowIDs.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent itemEvent) {
                if (initPhase || itemEvent.getStateChange() != ItemEvent.SELECTED) return;
                SYSPropsTools.storeState(internalClassID + ":tbShowIDs", tbShowIDs);
                reloadTable();
            }
        });
        tbShowIDs.setHorizontalAlignment(SwingConstants.LEFT);

        labelPanel.add(tbShowIDs);
        SYSPropsTools.restoreState(internalClassID + ":tbShowIDs", tbShowIDs);

        CollapsiblePane panelFilter = new CollapsiblePane(OPDE.lang.getString("misc.msg.Filter"));
        panelFilter.setStyle(CollapsiblePane.PLAIN_STYLE);
        panelFilter.setCollapsible(false);
        panelFilter.setContentPane(labelPanel);

        return panelFilter;
    }

    private CollapsiblePane addCommands() {
        JPanel mypanel = new JPanel();
        mypanel.setLayout(new VerticalLayout());
        mypanel.setBackground(Color.WHITE);

        CollapsiblePane searchPane = new CollapsiblePane(OPDE.lang.getString(internalClassID));
        searchPane.setStyle(CollapsiblePane.PLAIN_STYLE);
        searchPane.setCollapsible(false);

        try {
            searchPane.setCollapsed(false);
        } catch (PropertyVetoException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        if (OPDE.getAppInfo().userHasAccessLevelForThisClass(internalClassID, InternalClassACL.INSERT)) {
            JideButton addButton = GUITools.createHyperlinkButton(OPDE.lang.getString("misc.commands.new"), new ImageIcon(getClass().getResource("/artwork/22x22/bw/add.png")), new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {

                    new DlgWert(new BWerte(bewohner, OPDE.getLogin().getUser()), new Closure() {
                        @Override
                        public void execute(Object o) {
                            if (o != null) {
                                EntityManager em = OPDE.createEM();
                                try {
                                    em.getTransaction().begin();
                                    em.merge(o);
                                    em.getTransaction().commit();
                                } catch (Exception e) {
                                    if (em.getTransaction().isActive()) {
                                        em.getTransaction().rollback();
                                    }
                                    OPDE.fatal(e);
                                } finally {
                                    em.close();
                                }
                                reloadTable();
                            }
                        }
                    });
                }
            });
            mypanel.add(addButton);
        }

        if (OPDE.getAppInfo().userHasAccessLevelForThisClass(internalClassID, InternalClassACL.PRINT)) {
            JideButton printButton = GUITools.createHyperlinkButton(OPDE.lang.getString("misc.commands.print"), new ImageIcon(getClass().getResource("/artwork/22x22/bw/printer.png")), new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    printWerte(null);
                }
            });
            mypanel.add(printButton);
        }

        searchPane.setContentPane(mypanel);
        return searchPane;
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JScrollPane jspTblVW;
    private JTable tblVital;
    // End of variables declaration//GEN-END:variables
}
