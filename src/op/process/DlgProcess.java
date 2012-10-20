/*
 * Created by JFormDesigner on Tue Aug 21 17:15:05 CEST 2012
 */

package op.process;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import com.toedter.calendar.JDateChooser;
import entity.info.Resident;
import entity.process.PCat;
import entity.process.PCatTools;
import entity.process.QProcess;
import op.OPDE;
import op.tools.MyJDialog;
import op.tools.SYSTools;
import org.apache.commons.collections.Closure;
import org.joda.time.DateTime;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Date;

/**
 * @author Torsten Löhr
 */
public class DlgProcess extends MyJDialog {
    private QProcess qProcess;
    private Closure actionBlock;

    public DlgProcess(QProcess qProcess, Closure actionBlock) {
        super();
        this.qProcess = qProcess;
        this.actionBlock = actionBlock;
        initComponents();
        initDialog();
        setVisible(true);
    }

    private void initDialog() {
        cmbPCat.setModel(SYSTools.list2cmb(PCatTools.getPCats()));
        cmbPCat.setSelectedIndex(0);

        EntityManager em = OPDE.createEM();
        Query query = em.createQuery("SELECT b FROM Resident b WHERE b.station IS NOT NULL ORDER BY b.name, b.firstname");
        cmbResident.setRenderer(new ListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList jList, Object o, int i, boolean isSelected, boolean cellHasFocus) {
                String text;
                if (o == null) {
                    text = SYSTools.toHTML("<i>" + OPDE.lang.getString(PnlProcess.internalClassID + ".commonprocess") + "</i>");
                } else if (o instanceof PCat) {
                    text = ((PCat) o).getText();
                } else {
                    text = o.toString();
                }
                return new DefaultListCellRenderer().getListCellRendererComponent(jList, text, i, isSelected, cellHasFocus);
            }
        });
        ArrayList<Resident> listResident = new ArrayList<Resident>(query.getResultList());
        listResident.add(0, null);
        cmbResident.setModel(SYSTools.list2cmb(listResident));
        em.close();

        jdcWV.setDate(qProcess.getRevision());
        jdcWV.setMinSelectableDate(new DateTime().plusDays(1).toDate());

        txtTitel.setText(qProcess.getTitle().trim());

    }

    private void btnCancelActionPerformed(ActionEvent e) {
        qProcess = null;
        dispose();
    }

    private void btnApplyActionPerformed(ActionEvent e) {
        save();
        dispose();
    }

    private void save() {
        qProcess.setResident((Resident) cmbResident.getSelectedItem());
        qProcess.setPcat((PCat) cmbPCat.getSelectedItem());
        qProcess.setRevision(jdcWV.getDate());
        qProcess.setTitle(txtTitel.getText().trim());
    }

    @Override
    public void dispose() {
        actionBlock.execute(qProcess);
        super.dispose();
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        pnlDetails = new JPanel();
        lblTitle = new JLabel();
        lblEvalDate = new JLabel();
        lblBW = new JLabel();
        txtTitel = new JTextField();
        jdcWV = new JDateChooser();
        cmbResident = new JComboBox();
        lblCat = new JLabel();
        cmbPCat = new JComboBox();
        panel2 = new JPanel();
        btnCancel = new JButton();
        btnApply = new JButton();

        //======== this ========
        Container contentPane = getContentPane();
        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.X_AXIS));

        //======== pnlDetails ========
        {
            pnlDetails.setLayout(new FormLayout(
                    "default, 0dlu, $lcgap, 70dlu, $lcgap, default:grow, $lcgap, 0dlu, default",
                    "default, 0dlu, 3*($lgap, fill:default), $lgap, pref, 2*($lgap, default)"));

            //---- lblTitle ----
            lblTitle.setText("Titel");
            lblTitle.setFont(new Font("Arial", Font.PLAIN, 14));
            pnlDetails.add(lblTitle, CC.xywh(4, 4, 2, 1));

            //---- lblEvalDate ----
            lblEvalDate.setText("Wiedervorlage");
            lblEvalDate.setFont(new Font("Arial", Font.PLAIN, 14));
            pnlDetails.add(lblEvalDate, CC.xywh(4, 6, 2, 1));

            //---- lblBW ----
            lblBW.setText("Zugeordnet zu");
            lblBW.setFont(new Font("Arial", Font.PLAIN, 14));
            pnlDetails.add(lblBW, CC.xywh(4, 8, 2, 1));

            //---- txtTitel ----
            txtTitel.setFont(new Font("Arial", Font.PLAIN, 18));
            pnlDetails.add(txtTitel, CC.xywh(6, 4, 2, 1));

            //---- jdcWV ----
            jdcWV.setFont(new Font("Arial", Font.PLAIN, 14));
            pnlDetails.add(jdcWV, CC.xywh(6, 6, 2, 1));

            //---- cmbResident ----
            cmbResident.setFont(new Font("Arial", Font.PLAIN, 14));
            pnlDetails.add(cmbResident, CC.xy(6, 8));

            //---- lblCat ----
            lblCat.setText("Kategorie");
            lblCat.setFont(new Font("Arial", Font.PLAIN, 14));
            pnlDetails.add(lblCat, CC.xy(4, 10));

            //---- cmbPCat ----
            cmbPCat.setFont(new Font("Arial", Font.PLAIN, 14));
            cmbPCat.setToolTipText("Kategorie des Vorgangs");
            pnlDetails.add(cmbPCat, CC.xy(6, 10));

            //======== panel2 ========
            {
                panel2.setLayout(new BoxLayout(panel2, BoxLayout.LINE_AXIS));

                //---- btnCancel ----
                btnCancel.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/cancel.png")));
                btnCancel.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        btnCancelActionPerformed(e);
                    }
                });
                panel2.add(btnCancel);

                //---- btnApply ----
                btnApply.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/apply.png")));
                btnApply.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        btnApplyActionPerformed(e);
                    }
                });
                panel2.add(btnApply);
            }
            pnlDetails.add(panel2, CC.xy(6, 14, CC.RIGHT, CC.FILL));
        }
        contentPane.add(pnlDetails);
        setSize(705, 305);
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JPanel pnlDetails;
    private JLabel lblTitle;
    private JLabel lblEvalDate;
    private JLabel lblBW;
    private JTextField txtTitel;
    private JDateChooser jdcWV;
    private JComboBox cmbResident;
    private JLabel lblCat;
    private JComboBox cmbPCat;
    private JPanel panel2;
    private JButton btnCancel;
    private JButton btnApply;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
