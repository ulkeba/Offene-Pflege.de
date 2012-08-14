/*
 * Created by JFormDesigner on Fri Jun 01 11:55:36 CEST 2012
 */

package op.care.med.prodassistant;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import com.jidesoft.popup.JidePopup;
import entity.prescription.MedHersteller;
import entity.prescription.MedHerstellerTools;
import entity.prescription.MedProdukte;
import op.OPDE;
import org.apache.commons.collections.Closure;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author Torsten Löhr
 */
public class PnlHersteller extends JPanel {
    private MedProdukte produkt;
    private Closure validate;
    private Dialog parent;

    public PnlHersteller(Closure validate, MedProdukte produkt, Dialog parent) {
        this.validate = validate;
        this.produkt = produkt;
        this.parent = parent;
        initComponents();
        initPanel();
    }

    public void setProdukt(MedProdukte produkt) {
        this.produkt = produkt;
    }

    private void initPanel() {
        EntityManager em = OPDE.createEM();
        Query query2 = em.createNamedQuery("MedHersteller.findAll");
        lstHersteller.setModel(new DefaultComboBoxModel(query2.getResultList().toArray(new MedHersteller[]{})));
        lstHersteller.setCellRenderer(MedHerstellerTools.getHerstellerRenderer(0));
        em.close();
    }

    private void btnAddActionPerformed(ActionEvent e) {
        final JidePopup popup = new JidePopup();

        DlgHersteller dlg = new DlgHersteller(new Closure() {
            @Override
            public void execute(Object o) {
                if (o != null) {
                    lstHersteller.setModel(new DefaultComboBoxModel(new MedHersteller[]{(MedHersteller) o}));
                    lstHersteller.setSelectedIndex(0);
                    popup.hidePopup();
                }
            }
        });

        popup.setMovable(false);
        popup.setResizable(false);
        popup.getContentPane().setLayout(new BoxLayout(popup.getContentPane(), BoxLayout.LINE_AXIS));
        popup.getContentPane().add(dlg);
        popup.setOwner(btnAdd);
        popup.removeExcludedComponent(btnAdd);
        popup.setTransient(true);
        popup.setDefaultFocusComponent(dlg);

        popup.showPopup(new Insets(-5, 0, -5, 0), btnAdd);
    }

    private void lstHerstellerValueChanged(ListSelectionEvent e) {
        produkt.setHersteller((MedHersteller) lstHersteller.getSelectedValue());
        validate.execute(lstHersteller.getSelectedValue());
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        scrollPane1 = new JScrollPane();
        lstHersteller = new JList();
        btnAdd = new JButton();

        //======== this ========
        setLayout(new FormLayout(
            "default, $lcgap, default:grow, $lcgap, default",
            "default, $lgap, default:grow, 2*($lgap, default)"));

        //======== scrollPane1 ========
        {

            //---- lstHersteller ----
            lstHersteller.setFont(new Font("Arial", Font.PLAIN, 14));
            lstHersteller.addListSelectionListener(new ListSelectionListener() {
                @Override
                public void valueChanged(ListSelectionEvent e) {
                    lstHerstellerValueChanged(e);
                }
            });
            scrollPane1.setViewportView(lstHersteller);
        }
        add(scrollPane1, CC.xy(3, 3, CC.DEFAULT, CC.FILL));

        //---- btnAdd ----
        btnAdd.setText(null);
        btnAdd.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/bw/add.png")));
        btnAdd.setContentAreaFilled(false);
        btnAdd.setBorder(null);
        btnAdd.setSelectedIcon(new ImageIcon(getClass().getResource("/artwork/22x22/bw/add-pressed.png")));
        btnAdd.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnAdd.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                btnAddActionPerformed(e);
            }
        });
        add(btnAdd, CC.xy(3, 5, CC.LEFT, CC.DEFAULT));
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JScrollPane scrollPane1;
    private JList lstHersteller;
    private JButton btnAdd;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}