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
package op.care.vital;

import com.jgoodies.forms.factories.*;
import com.jgoodies.forms.layout.*;
import com.jidesoft.pane.CollapsiblePane;
import com.jidesoft.pane.CollapsiblePanes;
import com.jidesoft.swing.DefaultOverlayable;
import com.jidesoft.swing.InfiniteProgressPanel;
import com.jidesoft.swing.JideBoxLayout;
import com.jidesoft.swing.JideButton;
import com.toedter.calendar.JDateChooser;
import entity.BWerte;
import entity.BWerteTools;
import entity.Bewohner;
import entity.system.SYSPropsTools;
import op.OPDE;
import op.threads.DisplayMessage;
import op.tools.*;
import org.apache.commons.collections.Closure;
import org.jdesktop.swingx.HorizontalLayout;
import org.jdesktop.swingx.VerticalLayout;
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
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * @author tloehr
 */
public class PnlVitalwerte extends NursingRecordsPanel {

    private Bewohner bewohner;

    private boolean initPhase;
    private JPopupMenu menu;
    public static final String internalClassID = "nursingrecords.vitalparameters";

    private JScrollPane jspSearch;
    private CollapsiblePanes searchPanes;
    private JDateChooser jdcVon;
    private JToggleButton tbShowIDs, tbShowReplaced;
    private DefaultOverlayable overlayTable;
    private InfiniteProgressPanel progressPanel;

    /**
     * Creates new form pnlVitalwerte
     */
    public PnlVitalwerte(Bewohner bewohner, JScrollPane jspSearch) {
        initPhase = true;

        this.jspSearch = jspSearch;

        initComponents();
        prepareSearchArea();
//        initPanel();
        change2Bewohner(bewohner);
        initPhase = false;
    }

//    private void jdcDatumFocusLost(java.awt.event.FocusEvent evt) {
//        if (jdcDatum.getDate() == null) {
//            jdcDatum.setDate(SYSCalendar.addField(SYSCalendar.today_date(), -2, GregorianCalendar.WEEK_OF_MONTH));
//        }
//        jdcDatum.firePropertyChange("date", 0, 0);
//    }

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
                new Object[][] {
                    {null, null, null, null},
                    {null, null, null, null},
                    {null, null, null, null},
                    {null, null, null, null},
                },
                new String[] {
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

//    private void initPanel() {
//        overlayTable = new DefaultOverlayable(jspTblVW);
//
//        progressPanel = new InfiniteProgressPanel(){
//            @Override
//            public Dimension getPreferredSize() {
//                return new Dimension(200,200);
//            }
//        };
////        progressPanel.setBounds(300, 300, 200, 200);
//
//        overlayTable.addOverlayComponent(progressPanel);
//        progressPanel.stop();
//        overlayTable.setOverlayVisible(false);
//        remove(jspTblVW);
//        add(overlayTable);
//    }

    @Override
    public void change2Bewohner(Bewohner bewohner) {
        this.bewohner = bewohner;
        reloadTable();
    }

    @Override
    public void reload() {
        reloadTable();
    }

    private void printWerte(int[] sel) {
        try {
            // Create temp file.
            File temp = File.createTempFile(internalClassID, ".html");

            // Delete temp file when program exits.
            temp.deleteOnExit();

            // Write to temp file
            BufferedWriter out = new BufferedWriter(new FileWriter(temp));

            TMWerte tm = (TMWerte) tblVital.getModel();
            out.write(BWerteTools.getBWerteAsHTML(tm.getContent()));

            out.close();
            SYSPrint.handleFile(temp.getAbsolutePath(), Desktop.Action.OPEN);
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
        if (tcm1.getColumnCount() < 2) {
            return;
        }

        tcm1.getColumn(0).setPreferredWidth(200);
        tcm1.getColumn(1).setPreferredWidth(width);

        tcm1.getColumn(0).setHeaderValue("Datum / PflegerIn");
        tcm1.getColumn(1).setHeaderValue("Wert");
    }//GEN-LAST:event_jspTblVWComponentResized

    private void reloadTable() {
        OPDE.getMainframe().setBlocked(true);
        OPDE.getDisplayManager().setProgressBarMessage(new DisplayMessage(OPDE.lang.getString("misc.msg.wait"), -1, 100));

        SwingWorker worker = new SwingWorker() {
            @Override
            protected Object doInBackground() throws Exception {
                TMWerte oldModel = null;

                tblVital.setModel(new TMWerte(jdcVon.getDate(), bewohner, tbShowReplaced.isSelected(), tbShowIDs.isSelected()));
                if (oldModel != null) {
                    oldModel.cleanup();
                }
                tblVital.setSelectionMode(javax.swing.ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
                //tblTB.setDefaultRenderer(String.class, new TBTableRenderer());

                jspTblVW.dispatchEvent(new ComponentEvent(jspTblVW, ComponentEvent.COMPONENT_RESIZED));

                // Hier kann die Klasse RNDBerichte verwendet werden. Sie ist einfach in
                // HTML Renderer ohne Zebra Muster. Genau was wir hier wollen.
                tblVital.getColumnModel().getColumn(0).setCellRenderer(new RNDHTML());
                tblVital.getColumnModel().getColumn(1).setCellRenderer(new RNDHTML());

                return null;
            }

            @Override
            protected void done() {
                OPDE.getDisplayManager().setProgressBarMessage(null);
                OPDE.getMainframe().setBlocked(false);
            }
        };
        worker.execute();
    }

    private void btnLogoutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLogoutActionPerformed
//        OPDE.ocmain.lockOC();
    }//GEN-LAST:event_btnLogoutActionPerformed

    private void tblVitalMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblVitalMousePressed

        Point p = evt.getPoint();
        ListSelectionModel lsm = tblVital.getSelectionModel();

        boolean singleRowSelected = lsm.getMaxSelectionIndex() == lsm.getMinSelectionIndex();

        int row = tblVital.rowAtPoint(p);
        if (singleRowSelected) {
            lsm.setSelectionInterval(row, row);
        }

        SYSTools.unregisterListeners(menu);
        menu = new JPopupMenu();

        TableModel tm = tblVital.getModel();
//        if (tm.getRowCount() > 0 && row > -1) {
//            final Object[] o = (Object[]) tm.getValueAt(lsm.getLeadSelectionIndex(), TMWerte.TBL_OBJECT);
//            long replacedby = (Long) o[TMWerte.COL_REPLACEDBY];
//            final long bwid = (Long) o[TMWerte.COL_BWID];
//
//            boolean alreadyEdited = replacedby != 0;
//            //boolean sameUser = (ukennung.compareTo(OPDE.getLogin().getUser().getUKennung()) == 0);
//
//            boolean bearbeitenMöglich = !alreadyEdited && singleRowSelected;
//
//            if (evt.isPopupTrigger()) {
//
//                // KORRIGIEREN
//                JMenuItem itemPopupEdit = new JMenuItem("Korrigieren");
//                itemPopupEdit.addActionListener(new java.awt.event.ActionListener() {
//
//                    public void actionPerformed(java.awt.event.ActionEvent evt) {
//                        new DlgVital(parent, o);
//                        reloadTable();
//                    }
//                });
//                menu.add(itemPopupEdit);
//
//                JMenuItem itemPopupDelete = new JMenuItem("Löschen");
//                itemPopupDelete.addActionListener(new java.awt.event.ActionListener() {
//
//                    public void actionPerformed(java.awt.event.ActionEvent evt) {
//                        if (JOptionPane.showConfirmDialog(parent, "Möchten Sie diesen Eintrag wirklich löschen ?",
//                                "Wert löschen ?", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
//                            HashMap hm = new HashMap();
//                            hm.put("ReplacementFor", bwid);
//                            hm.put("ReplacedBy", bwid);
//                            hm.put("EditBy", OPDE.getLogin().getUser().getUKennung());
//                            op.tools.DBHandling.updateRecord("BWerte", hm, "BWID", bwid);
//                            reloadTable();
//                        }
//                    }
//                });
//                menu.add(itemPopupDelete);
//
//                JMenuItem itemPopupPrint = new JMenuItem("Markierte Werte drucken");
//                itemPopupPrint.addActionListener(new java.awt.event.ActionListener() {
//
//                    public void actionPerformed(java.awt.event.ActionEvent evt) {
//                        int[] sel = tblVital.getSelectedRows();
//                        printWerte(sel);
//                    }
//                });
//                menu.add(itemPopupPrint);
//
//                ocs.setEnabled(this, "itemPopupEdit", itemPopupEdit, bearbeitenMöglich);
//                ocs.setEnabled(this, "itemPopupDelete", itemPopupDelete, bearbeitenMöglich);
//
////                if (!alreadyEdited && singleRowSelected) {
////                    menu.add(new JSeparator());
////                    // #0000003
////                    menu.add(op.share.vorgang.DBHandling.getVorgangContextMenu(parent, "BWerte", bwid, currentBW, fileActionListener));
////
////                    Query query = em.createNamedQuery("BWerte.findByBwid");
////                    query.setParameter("bwid", bwid);
////                    entity.BWerte bwert = (entity.BWerte) query.getSingleResult();
////                    menu.add(SYSFilesTools.getSYSFilesContextMenu(parent, bwert, fileActionListener));
////
////                    // #0000035
////                    //menu.add(SYSFiles.getOPFilesContextMenu(parent, "BWerte", bwid, currentBW, tblVital, true, true, SYSFiles.CODE_BERICHTE, fileActionListener));
////                }
//
//                EntityManager em = OPDE.createEM();
//                BWerte aktuellerWert = em.find(BWerte.class, bwid);
//
//                if (OPDE.getAppInfo().userHasAccessLevelForThisClass(internalClassID, InternalClassACL.SELECT) && !alreadyEdited && singleRowSelected) {
//                    menu.add(new JSeparator());
//                    menu.add(SYSFilesTools.getSYSFilesContextMenu(parent, aktuellerWert, standardActionListener));
//                }
//
//                if (OPDE.getAppInfo().userHasAccessLevelForThisClass(internalClassID, InternalClassACL.SELECT) && !alreadyEdited && singleRowSelected) {
//                    menu.add(new JSeparator());
//                    menu.add(VorgaengeTools.getVorgangContextMenu(parent, aktuellerWert, bewohner, standardActionListener));
//                }
//                em.close();
//
//            }
//        }
//        menu.show(evt.getComponent(), (int) p.getX(), (int) p.getY());
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

        CollapsiblePane panelFilter = new CollapsiblePane(OPDE.lang.getString("misc.filter"));
        panelFilter.setStyle(CollapsiblePane.PLAIN_STYLE);
        panelFilter.setCollapsible(false);

        tbShowReplaced = GUITools.getNiceToggleButton(OPDE.lang.getString("misc.showreplaced"));
        tbShowReplaced.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                SYSPropsTools.storeState(internalClassID + ":tbShowReplaced", tbShowReplaced);
                reloadTable();
            }
        });
        labelPanel.add(tbShowReplaced);
        SYSPropsTools.restoreState(internalClassID + ":tbShowIDs", tbShowReplaced);
        tbShowReplaced.setHorizontalAlignment(SwingConstants.LEFT);

        tbShowIDs = GUITools.getNiceToggleButton(OPDE.lang.getString("misc.showpks"));
        tbShowIDs.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                SYSPropsTools.storeState(internalClassID + ":tbShowIDs", tbShowIDs);
                reloadTable();
            }
        });
        tbShowIDs.setHorizontalAlignment(SwingConstants.LEFT);

        labelPanel.add(tbShowIDs);
        SYSPropsTools.restoreState(internalClassID + ":tbShowIDs", tbShowIDs);


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

                    new DlgWert(new BWerte(bewohner), new Closure() {
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
                            }
                            reloadTable();
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
