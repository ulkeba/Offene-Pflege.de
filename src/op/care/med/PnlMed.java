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


package op.care.med;

import com.jidesoft.pane.CollapsiblePane;
import com.jidesoft.pane.CollapsiblePanes;
import com.jidesoft.popup.JidePopup;
import com.jidesoft.swing.JideBoxLayout;
import com.jidesoft.swing.JideButton;
import com.jidesoft.wizard.WizardDialog;
import entity.prescription.*;
import op.OPDE;
import op.care.med.prodassistant.MedProductWizard;
import op.care.med.vorrat.DlgBestand;
import op.system.InternalClassACL;
import op.tools.*;
import org.apache.commons.collections.Closure;
import org.jdesktop.swingx.JXSearchField;
import org.jdesktop.swingx.VerticalLayout;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.util.ArrayList;

/**
 * @author tloehr
 */
public class PnlMed extends CleanablePanel {

    public static final String internalClassID = "opde.medication";
    private DefaultTreeModel tree;
    private MedProdukte produkt;
    private JPopupMenu menu;
    private CollapsiblePanes searchPanes;
    private JScrollPane jspSearch;
    private JXSearchField txtSuche;
    private JList lstPraep;

    /**
     * Creates new form FrmMed
     */
    public PnlMed(JScrollPane jspSearch) {
        this.jspSearch = jspSearch;
        initComponents();
        initDialog();
    }

    @Override
    public void cleanup() {
        SYSTools.unregisterListeners(menu);
        menu = null;
        SYSTools.unregisterListeners(this);
    }

    @Override
    public void reload() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    private void initDialog() {
        prepareSearchArea();
        produkt = null;
        treeMed.setModel(new DefaultTreeModel(new DefaultMutableTreeNode()));
        treeMed.setVisible(false);
    }

    /**
     * This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        jScrollPane1 = new JScrollPane();
        treeMed = new JTree();

        //======== this ========
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

        //======== jScrollPane1 ========
        {

            //---- treeMed ----
            treeMed.setFont(new Font("Arial", Font.PLAIN, 14));
            jScrollPane1.setViewportView(treeMed);
        }
        add(jScrollPane1);
    }// </editor-fold>//GEN-END:initComponents


    private void lstPraepValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_lstPraepValueChanged
        if (!evt.getValueIsAdjusting() && lstPraep.getSelectedValue() != null) {
            produkt = (MedProdukte) lstPraep.getSelectedValue();
            createTree();
        }
    }//GEN-LAST:event_lstPraepValueChanged

    private void txtSucheActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtSucheActionPerformed
//        treeMed.setCellRenderer(new DefaultTreeCellRenderer());
        treeMed.setModel(new DefaultTreeModel(new DefaultMutableTreeNode()));
//        treeMed.setVisible(false);
        if (txtSuche.getText().isEmpty()) {
            lstPraep.setModel(new DefaultListModel());
        } else {
            EntityManager em = OPDE.createEM();
            Query query = em.createNamedQuery("MedProdukte.findByBezeichnungLike");
            query.setParameter("bezeichnung", "%" + txtSuche.getText() + "%");
            lstPraep.setModel(SYSTools.list2dlm(query.getResultList()));
            em.close();
        }
    }//GEN-LAST:event_txtSucheActionPerformed

    private void treeMedMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_treeMedMousePressed
        if (produkt == null) return;
        if (evt.isPopupTrigger()) {
            // Dieses Popupmenu für den Table
            SYSTools.unregisterListeners(menu);
            menu = new JPopupMenu();
            JMenuItem itemdaf = new JMenuItem("Neue Darreichungsform");
            itemdaf.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    btnNeuDAF(evt);
                }
            });
            menu.add(itemdaf);

            if (treeMed.getRowForLocation(evt.getX(), evt.getY()) != -1) {
                JMenuItem itemedit = null;
                JMenuItem itemdelete = null;
                JMenuItem itemnew = null;
                JMenuItem itempack = null;
                TreePath curPath = treeMed.getPathForLocation(evt.getX(), evt.getY());
                DefaultMutableTreeNode dmtn = (DefaultMutableTreeNode) curPath.getLastPathComponent();
                treeMed.setSelectionPath(curPath);
//                final ListElement le = (ListElement) dmtn.getUserObject();
//                int nodetype = ((Integer) le.getObject()).intValue();


//                if (dmtn.getUserObject() instanceof Darreichung) {
//                    final Darreichung darreichung = (Darreichung) dmtn.getUserObject();
//                    itemedit = new JMenuItem("Bearbeiten");
//                    itemedit.addActionListener(new java.awt.event.ActionListener() {
//                        public void actionPerformed(java.awt.event.ActionEvent evt) {
//                            new DlgDAF(thisFrame, "", darreichung);
//                            createTree();
//                        }
//                    });
//                    itemdelete = new JMenuItem("Entfernen");
//                    itemdelete.addActionListener(new java.awt.event.ActionListener() {
//                        public void actionPerformed(java.awt.event.ActionEvent evt) {
//                            btnDeleteDAF(darreichung);
//                        }
//                    });
//                    itempack = new JMenuItem("Neue Verpackung");
//                    itempack.addActionListener(new java.awt.event.ActionListener() {
//                        public void actionPerformed(java.awt.event.ActionEvent evt) {
//                            MedPackung mypack = new MedPackung(darreichung);
//                            new DlgPack(thisFrame, "Neu", mypack);
////                            OPDE.getEMF().getCache().evict(Darreichung.class, darreichung.getDafID());
//                            createTree();
//                        }
//                    });
//                } else if (dmtn.getUserObject() instanceof MedPackung) {
//                    final MedPackung packung = (MedPackung) dmtn.getUserObject();
//                    itemedit = new JMenuItem("Bearbeiten");
//                    itemedit.addActionListener(new java.awt.event.ActionListener() {
//                        public void actionPerformed(java.awt.event.ActionEvent evt) {
//                            new DlgPack(thisFrame, "Bearbeiten", packung);
//                            createTree();
//                        }
//                    });
//                }

                if (itemedit != null || itemdelete != null || itemnew != null) {
                    menu.add(new JSeparator(JSeparator.HORIZONTAL));
                }
                if (itemnew != null) menu.add(itemnew);
                if (itempack != null) menu.add(itempack);
                if (itemedit != null) menu.add(itemedit);
                if (itemdelete != null) menu.add(itemdelete);
            }
            menu.show(evt.getComponent(), evt.getX(), evt.getY());
        }
    }//GEN-LAST:event_treeMedMousePressed

    private void btnNeuDAF(java.awt.event.ActionEvent evt) {

//        Darreichung darreichung = new Darreichung(produkt);
//
//        new DlgDAF(this, "", darreichung);
//        createTree();
    }


    private void btnEditDAF(TradeForm darreichung) {
//        new DlgDAF(this, "", darreichung);
//        createTree();
    }

    private void btnDeleteDAF(TradeForm darreichung) {
//        if (JOptionPane.showConfirmDialog(this, "Damit werden auch alle Zuordnungen und Packungen gelöscht.\n\nSind Sie sicher ?", DarreichungTools.toPrettyString(darreichung) + " entfernen", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
//            return;
//        }
//
//        EntityTools.delete(darreichung);
//        createTree();
    }

    private void createTree() {
        if (produkt == null) return;
        treeMed.setVisible(true);
        tree = new DefaultTreeModel(getRoot());
        treeMed.setModel(tree);
        treeMed.setCellRenderer(new TreeRenderer());
        SYSTools.expandAll(treeMed);
    }

    private DefaultMutableTreeNode getRoot() {
        DefaultMutableTreeNode root;
        root = new DefaultMutableTreeNode(produkt);
        SYSTools.addAllNodes(root, getDAF());
        return root;
    }

    private java.util.List getDAF() {
        java.util.List result = new ArrayList();

        EntityManager em = OPDE.createEM();
        Query query = em.createNamedQuery("Darreichung.findByMedProdukt");
        query.setParameter("medProdukt", produkt);

        java.util.List<TradeForm> listDAF = query.getResultList();
        em.close();


        for (TradeForm daf : listDAF) {
            DefaultMutableTreeNode nodeDAF = new DefaultMutableTreeNode(daf);
            SYSTools.addAllNodes(nodeDAF, getPackung(daf));
            result.add(nodeDAF);
        }


        return result;
    }

    private java.util.List getPackung(TradeForm darreichung) {
        java.util.List result = new ArrayList();
        for (MedPackung packung : darreichung.getPackungen()) {
            result.add(new DefaultMutableTreeNode(packung));
        }


        return result;
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JScrollPane jScrollPane1;
    private JTree treeMed;
    // End of variables declaration//GEN-END:variables

    private class TreeRenderer extends DefaultTreeCellRenderer {
        TreeRenderer() {
            super();
        }

        @Override
        public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
            JLabel component = (JLabel) super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
            if (node.getUserObject() instanceof MedProdukte) {
                component.setIcon(new ImageIcon(getClass().getResource("/artwork/16x16/info.png")));
                MedProdukte myprod = (MedProdukte) node.getUserObject();
                component.setText(myprod.getBezeichnung() + ", " + myprod.getHersteller().getFirma() + ", " + myprod.getHersteller().getOrt());
            } else if (node.getUserObject() instanceof TradeForm) {
                component.setIcon(new ImageIcon(getClass().getResource("/artwork/16x16/medical.png")));
                component.setText(TradeFormTools.toPrettyStringMedium((TradeForm) node.getUserObject()));
            } else if (node.getUserObject() instanceof MedPackung) {
                component.setIcon(new ImageIcon(getClass().getResource("/artwork/16x16/package.png")));
                component.setText(MedPackungTools.toPrettyString((MedPackung) node.getUserObject()));
            } else {
                component.setIcon(new ImageIcon(getClass().getResource("/artwork/16x16/filenew.png")));
                component.setText(null);
            }
            component.setFont(SYSConst.ARIAL14);
//            setBackground(selected ? SYSConst.lightblue : Color.WHITE);

            return component;
        }

    }


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
        labelPanel.setLayout(new VerticalLayout());

        CollapsiblePane panelFilter = new CollapsiblePane("Suchen");
        panelFilter.setStyle(CollapsiblePane.PLAIN_STYLE);
        panelFilter.setCollapsible(false);

        try {
            panelFilter.setCollapsed(false);
        } catch (PropertyVetoException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        txtSuche = new JXSearchField("Suchen");
        txtSuche.setFont(SYSConst.ARIAL14);
        txtSuche.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                txtSucheActionPerformed(actionEvent);
            }
        });
        txtSuche.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent focusEvent) {
                SYSTools.markAllTxt(txtSuche);
            }
        });
        labelPanel.add(txtSuche);

        lstPraep = new JList(new DefaultListModel());
        lstPraep.setCellRenderer(MedProdukteTools.getMedProdukteRenderer());
        lstPraep.setFont(SYSConst.ARIAL14);
        lstPraep.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent listSelectionEvent) {
                lstPraepValueChanged(listSelectionEvent);
            }
        });
        lstPraep.setFixedCellWidth(200);

        labelPanel.add(new JScrollPane(lstPraep));

        panelFilter.setContentPane(labelPanel);
        return panelFilter;
    }

    private CollapsiblePane addCommands() {
        JPanel mypanel = new JPanel();
        mypanel.setLayout(new VerticalLayout());
        mypanel.setBackground(Color.WHITE);

        CollapsiblePane searchPane = new CollapsiblePane("Medikamente");
        searchPane.setStyle(CollapsiblePane.PLAIN_STYLE);
        searchPane.setCollapsible(false);

        try {
            searchPane.setCollapsed(false);
        } catch (PropertyVetoException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }


//        if (OPDE.getAppInfo().userHasAccessLevelForThisClass(internalClassID, InternalClassACL.INSERT)) {
//            JideButton addButton = GUITools.createHyperlinkButton("Neues Medikament", new ImageIcon(getClass().getResource("/artwork/22x22/bw/add.png")), new ActionListener() {
//                @Override
//                public void actionPerformed(ActionEvent actionEvent) {
//                }
//            });
//            mypanel.add(addButton);
//        }

        if (OPDE.getAppInfo().userHasAccessLevelForThisClass(internalClassID, InternalClassACL.INSERT)) {
            final JideButton addButton = GUITools.createHyperlinkButton("Medikamenten-Assistent", new ImageIcon(getClass().getResource("/artwork/22x22/wizard.png")), null);

            addButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {


                    final JidePopup popup = new JidePopup();


                    WizardDialog wizard = new MedProductWizard(new Closure() {
                        @Override
                        public void execute(Object o) {
                            popup.hidePopup();
                            // keine Maßnahme nötig
                        }
                    }, null).getWizard();


                    popup.setMovable(false);
                    popup.setPreferredSize((new Dimension(800, 450)));
                    popup.setResizable(false);
                    popup.getContentPane().setLayout(new BoxLayout(popup.getContentPane(), BoxLayout.LINE_AXIS));
                    popup.getContentPane().add(wizard.getContentPane());
                    popup.setOwner(addButton);
                    popup.removeExcludedComponent(addButton);
                    popup.setTransient(true);
                    popup.setDefaultFocusComponent(wizard.getContentPane());
                    popup.addPropertyChangeListener("visible", new PropertyChangeListener() {
                        @Override
                        public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
                            OPDE.debug("popup property: " + propertyChangeEvent.getPropertyName() + " value: " + propertyChangeEvent.getNewValue() + " compCount: " + popup.getContentPane().getComponentCount());
                            popup.getContentPane().getComponentCount();
                        }
                    });

                    popup.showPopup(new Insets(-5, 0, -5, 0), addButton);
                }
            });

            mypanel.add(addButton);
        }

        if (OPDE.getAppInfo().userHasAccessLevelForThisClass(internalClassID, InternalClassACL.INSERT)) {
            JideButton buchenButton = GUITools.createHyperlinkButton("Medikamente einbuchen", new ImageIcon(getClass().getResource("/artwork/22x22/shetaddrow.png")), new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    new DlgBestand(null);
                }
            });
            mypanel.add(buchenButton);
        }

        searchPane.setContentPane(mypanel);
        return searchPane;
    }
}
