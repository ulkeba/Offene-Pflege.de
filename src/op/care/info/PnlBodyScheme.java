package op.care.info;

import op.OPDE;
import op.tools.SYSConst;
import op.tools.SYSTools;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Properties;

/**
 * Created with IntelliJ IDEA.
 * User: tloehr
 * Date: 21.05.13
 * Time: 14:50
 * To change this template use File | Settings | File Templates.
 */
public class PnlBodyScheme extends JPanel {

    public static final String[] NAMES = new String[]{"head.left.side", "shoulder.left.side", "upper.back.left.side", "ellbow.left.side", "hand.left.side", "hip.left.side", "bottom.left.side", "upper.leg.left.side",
            "lower.leg.left.side", "calf.left.side", "heel.left.side", "face", "shoulder.front.right", "shoulder.front.left", "upper.belly", "crook.arm.right",
            "crook.arm.left", "lower.belly", "groin", "upper.leg.right.front", "upper.leg.left.front", "knee.right", "knee.left", "shin.right.front", "shin.left.front",
            "foot.right.front", "foot.left.front", "back.of.the.head", "shoulder.back.left", "shoulder.back.right", "back.mid", "ellbow.left",
            "ellbow.right", "back.low", "bottom", "upper.leg.left.back", "upper.leg.right.back", "knee.hollow.left", "knee.hollow.right", "calf.left",
            "calf.right", "foot.right.back", "foot.left.back", "head.right.side", "shoulder.right.side", "back.upper.left.side", "ellbow.right.side",
            "hand.right.side", "hip.right.side", "bottom.right.side", "upper.leg.right.side", "lower.leg.right.side", "calf.right.side", "heel.right.side"};

    BufferedImage bimg;
    Point[] cbPositions;
    Properties selection;


    public PnlBodyScheme(Properties selection) {
        this.selection = selection;
        try {
            bimg = ImageIO.read(new File(this.getClass().getResource("/artwork/body-scheme.png").getPath()));
        } catch (IOException e) {
            OPDE.fatal(e);
        }

        int width = bimg.getWidth();
        int height = bimg.getHeight();
        setSize(new Dimension(width, height));
        setPreferredSize(new Dimension(width, height));

        initPanel();

    }

    private void initPanel() {

//        cbTooltips = new String[]{"Kopf, linke Seite", "Schulter, links", "Rücken, linke Seite, oben", "Ellenbogen, links", "Hand, links", "Hüfte, links", "Gesäß, linke Seite", "Oberschenkel, links",
//                "Unterschenkel, links", "Wade, links", "Ferse, links", "Gesicht", "Schulter, rechts, vorne (aus Pat. Sicht)", "Schulter, links, vorne (aus Pat. Sicht)", "Bauch, oben", "Armbeuge, rechts",
//                "Armbeuge, links", "Bauch, unten", "Genitalbereich", "Oberschenkel, rechts, vorne", "Oberschenkel, links, vorne", "Knie, rechts", "Knie, links", "Schienbein, rechts, vorne", "Schienbein, links, vorne",
//                "Fuß, rechts, vorne", "Fuß, links, vorne", "Hinterkopf", "Schulter, links, hinten (aus Pat. Sicht)", "Schulter, rechts, hinten (aus Pat. Sicht)", "Rücken, mitte", "Ellenbogen, links",
//                "Ellenbogen, rechts", "Rücken, unten", "Gesäß", "Oberschenkel, links, hinten", "Oberschenkel, rechts, hinten", "Kniekehle, links", "Kniekehle, rechts", "Wade, links",
//                "Wade, rechts", "Fuß, rechts, hinten", "Fuß, links, hinten", "Kopf, rechte Seite", "Schulter, rechte Seite", "Rücken, oben, rechte Seite", "Ellenbogen, rechte Seite",
//                "Hand, rechte Seite", "Hüfte, rechte Seite", "Gesäß, rechte Seite", "Oberschenkel, rechte Seite", "Unterschenkel, rechte Seite", "Wade, rechte Seite", "Ferse, rechte Seite"};


        cbPositions = new Point[]{
        /* left side */
                new Point(130, 8), new Point(124, 69), new Point(146, 110), new Point(70, 108), new Point(22, 149), new Point(138, 161), new Point(141, 210), new Point(135, 258), new Point(146, 314),
                new Point(142, 365), new Point(140, 403),
                /* Front side */
                new Point(288, 12), new Point(253, 72), new Point(325, 72), new Point(288, 117), new Point(237, 144), new Point(344, 144), new Point(288, 164), new Point(288, 212), new Point(266, 258),
                new Point(317, 258), new Point(265, 312), new Point(314, 312), new Point(269, 363), new Point(314, 363), new Point(274, 404), new Point(310, 404),
                /* Back side */
                new Point(536, 9), new Point(497, 75), new Point(579, 75), new Point(536, 110), new Point(477, 159), new Point(596, 159), new Point(537, 161), new Point(536, 205), new Point(514, 253), new Point(560, 253),
                new Point(518, 305), new Point(562, 305), new Point(513, 351), new Point(562, 351), new Point(523, 402), new Point(557, 403),
                /* right side */
                new Point(705, 11), new Point(700, 69), new Point(696, 107), new Point(763, 107), new Point(806, 155), new Point(707, 157), new Point(691, 196), new Point(698, 262), new Point(696, 314), new Point(696, 358),
                new Point(698, 397)};

        setLayout(null);

        for (int i = 0; i < cbPositions.length; i++) {

            final JCheckBox jcb = new JCheckBox((String) null);
            jcb.setToolTipText(OPDE.lang.getString(NAMES[i]));
            jcb.setName(NAMES[i]);
            jcb.setBorder(null);
            jcb.setContentAreaFilled(false);
            jcb.setSelected(SYSTools.catchNull(selection.getProperty(jcb.getName())).equalsIgnoreCase("true"));
            jcb.addItemListener(new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent e) {
                    selection.put(jcb.getName(), Boolean.toString(jcb.isSelected()));
                }
            });
            add(jcb);
            jcb.setBounds(cbPositions[i].x, cbPositions[i].y, 32, 32);
            jcb.setSelectedIcon(SYSConst.icon32ledRedOn);
            jcb.setIcon(SYSConst.icon32ledGrey);
        }
        selection = new Properties();
    }

    public Properties getSelection() {
        return selection;
    }


    @Override
    public void paintComponent(Graphics g) {
        g.drawImage(bimg, 0, 0, getWidth(), getHeight(), this);
    }
}