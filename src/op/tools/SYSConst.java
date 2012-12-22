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
package op.tools;

import op.OPDE;

import javax.swing.*;
import java.awt.*;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;

/**
 * @author tloehr
 */
public class SYSConst {

    public static final Font ARIAL10BOLD = new Font("Arial", Font.BOLD, 10);
    public static final Font ARIAL12BOLD = new Font("Arial", Font.BOLD, 12);
    public static final Font ARIAL14 = new Font("Arial", Font.PLAIN, 14);
    public static final Font ARIAL14BOLD = new Font("Arial", Font.BOLD, 14);
    public static final Font ARIAL20 = new Font("Arial", Font.PLAIN, 20);
    public static final Font ARIAL18BOLD = new Font("Arial", Font.BOLD, 18);
    public static final Font ARIAL20BOLD = new Font("Arial", Font.BOLD, 20);
    public static final Font ARIAL28 = new Font("Arial", Font.PLAIN, 28);

    public static Color darkgreen = new Color(0x00, 0x76, 0x00);
    public static Color darkred = new Color(0xbd, 0x00, 0x00);
    public static Color gold7 = new Color(0xff, 0xaa, 0x00);
    public static Color darkorange = new Color(0xff, 0x8c, 0x00);
    public static Color khaki1 = new Color(0xFF, 0xF3, 0x80);
    public static Color khaki2 = new Color(0xed, 0xe2, 0x75);
    public static Color khaki3 = new Color(0xc9, 0xbe, 0x62);
    public static Color khaki4 = new Color(0x82, 0x78, 0x39);
    public static Color deepskyblue = new Color(0, 191, 255);
    public static Color bluegrey = new Color(230, 230, 255);
    public static Color lightblue = new Color(192, 217, 217);
    public static Color bermuda_sand = new Color(246, 201, 204);
    public static Color melonrindgreen = new Color(223, 255, 165);
    public static Color orangered = new Color(255, 36, 0);
    public static Color sun3 = new Color(153, 153, 204);

    public static String html_22x22_StopSign = "<img src=\"" + OPDE.getOPWD() + "/artwork/22x22/stop.png\" border=\"0\">";
    public static String html_22x22_Eraser = "<img src=\"" + OPDE.getOPWD() + "/artwork/22x22/eraser.png\" border=\"0\">";
    public static String html_22x22_Edited = "<img src=\"" + OPDE.getOPWD() + "/artwork/22x22/edited.png\" border=\"0\">";
    public static String html_48x48_biohazard = "<img src=\"" + OPDE.getOPWD() + "/artwork/48x48/biohazard.png\" border=\"0\">";
    public static String html_48x48_warning = "<img src=\"" + OPDE.getOPWD() + "/artwork/48x48/warning.png\" border=\"0\">";
    public static String html_48x48_diabetes = "<img src=\"" + OPDE.getOPWD() + "/artwork/48x48/diabetes.png\" border=\"0\">";
    public static String html_48x48_allergy = "<img src=\"" + OPDE.getOPWD() + "/artwork/48x48/allergy.png\" border=\"0\">";

    public static String html_darkgreen = "color=\"#007600\"";
    public static String html_darkred = "color=\"#bd0000\"";
    public static String html_gold7 = "color=\"#ffaa00\"";
    public static String html_darkorange = "color=\"#ff8c00\"";
    public static String html_khaki1 = "color=\"#fffg8f\"";
    public static String html_silver = "color=\"#C0C0C0\"";
    public static String html_lightslategrey = "color=\"#778899\"";
    public static String html_grey80 = "color=\"#c7c7c5\"";
    public static String html_grey50 = "color=\"#747170\"";
    public static String html_cyan = "color=\"#00ffff\"";
    public static String html_mediumpurple3 = "color=\"#8968cd\"";
    public static String html_mediumorchid3 = "color=\"#b452cd\"";
    public static Color salmon1 = new Color(0xFF, 0x8C, 0x69);
    public static Color salmon2 = new Color(0xEE, 0x82, 0x62);
    public static Color salmon3 = new Color(0xCD, 0x70, 0x54);
    public static Color salmon4 = new Color(0x8B, 0x4C, 0x39);
    public static Color lightsteelblue1 = new Color(0xc6, 0xde, 0xff);
    public static Color lightsteelblue2 = new Color(188, 210, 238);
    public static Color lightsteelblue3 = new Color(0x9a, 0xad, 0xc7);
    public static Color lightsteelblue4 = new Color(0x6E, 0x7B, 0x8B);
    public static Color darkolivegreen1 = new Color(0xcc, 0xfb, 0x5d);
    public static Color darkolivegreen2 = new Color(0xBC, 0xEE, 0x68);
    public static Color darkolivegreen3 = new Color(0xa0, 0xc5, 0x44);
    public static Color darkolivegreen4 = new Color(0x6E, 0x8B, 0x3D);
    public static Color gold2 = new Color(0xEE, 0xC9, 0x00);
    public static Color gold4 = new Color(0x8B, 0x75, 0x00);
    public static Color mediumpurple1 = new Color(0xAB, 0x82, 0xFF);
    public static Color mediumpurple2 = new Color(0x9F, 0x79, 0xEE);
    public static Color mediumpurple3 = new Color(0x89, 0x68, 0xCD);
    public static Color mediumpurple4 = new Color(0x5D, 0x47, 0x8B);
    public static Color mediumorchid1 = new Color(0xE0, 0x66, 0xFF);
    public static Color mediumorchid3 = new Color(0xB4, 0x52, 0xCD);
    public static Color mediumorchid2 = new Color(0xC4, 0x5A, 0xEC);
    public static Color mediumorchid4 = new Color(0x6A, 0x28, 0x7E);
    public static Color thistle1 = new Color(0xfc, 0xdf, 0xFF);
    public static Color thistle2 = new Color(0xe9, 0xcf, 0xEC);
    public static Color thistle3 = new Color(0xc6, 0xae, 0xc7);
    public static Color thistle4 = new Color(0x80, 0x6d, 0x7E);
    //    public static Color yellow1 = new Color(0xFF, 0xFF, 0x00);
    public static Color yellow2 = new Color(0xEE, 0xEE, 0x00);
    public static Color yellow3 = new Color(0xCD, 0xCD, 0x00);
    public static Color yellow4 = new Color(0x8B, 0x8B, 0x00);
    public static Color grey80 = new Color(0xc7, 0xc7, 0xc5);
    public static Color grey50 = new Color(0x74, 0x71, 0x70);


    public static final int dark4 = 0;
    public static final int dark3 = 1;
    public static final int dark2 = 2;
    public static final int dark1 = 3;
    public static final int medium4 = 4;
    public static final int medium3 = 5;
    public static final int medium2 = 6;
    public static final int medium1 = 7;
    public static final int light4 = 8;
    public static final int light3 = 9;
    public static final int light2 = 10;
    public static final int light1 = 11;

    public static Color[] purple1 = new Color[]{
            SYSTools.getColor("800080"), SYSTools.getColor("BF00BF"),
            SYSTools.getColor("DB00DB"), SYSTools.getColor("F900F9"),
            SYSTools.getColor("FF4AFF"), SYSTools.getColor("FF86FF"),
            SYSTools.getColor("FFA4FF"), SYSTools.getColor("FFBBFF"),
            SYSTools.getColor("FFCEFF"), SYSTools.getColor("FFDFFF"),
            SYSTools.getColor("FFECFF"), SYSTools.getColor("FFF9FF")
    };

    public static Color[] greyscale = new Color[]{
            SYSTools.getColor("2E2E2E"), SYSTools.getColor("424242"),
            SYSTools.getColor("585858"), SYSTools.getColor("6E6E6E"),
            SYSTools.getColor("848484"), SYSTools.getColor("A4A4A4"),
            SYSTools.getColor("BDBDBD"), SYSTools.getColor("D8D8D8"),
            SYSTools.getColor("E6E6E6"), SYSTools.getColor("F2F2F2"),
            SYSTools.getColor("FAFAFA"), SYSTools.getColor("FFFFFF")
    };

    public static Color[] yellow1 = new Color[]{
            SYSTools.getColor("C8B400"), SYSTools.getColor("D9C400"),
            SYSTools.getColor("E6CE00"), SYSTools.getColor("F7DE00"),
            SYSTools.getColor("FFE920"), SYSTools.getColor("FFF06A"),
            SYSTools.getColor("FFF284"), SYSTools.getColor("FFF7B7"),
            SYSTools.getColor("FFF9CE"), SYSTools.getColor("FFFBDF"),
            SYSTools.getColor("FFFEF7"), SYSTools.getColor("FFFFFF")
    };

    public static Color[] blue1 = new Color[]{
            SYSTools.getColor("3923D6"), SYSTools.getColor("6755E3"),
            SYSTools.getColor("8678E9"), SYSTools.getColor("9588EC"),
            SYSTools.getColor("A095EE"), SYSTools.getColor("B0A7F1"),
            SYSTools.getColor("BCB4F3"), SYSTools.getColor("CBC5F5"),
            SYSTools.getColor("D7D1F8"), SYSTools.getColor("E3E0FA"),
            SYSTools.getColor("EFEDFC"), SYSTools.getColor("F7F5FE")
    };

    public static Color[] red1 = new Color[]{
            SYSTools.getColor("F70000"), SYSTools.getColor("FF2626"),
            SYSTools.getColor("FF5353"), SYSTools.getColor("FF7373"),
            SYSTools.getColor("FF8E8E"), SYSTools.getColor("FFA4A4"),
            SYSTools.getColor("FFB5B5"), SYSTools.getColor("FFC8C8"),
            SYSTools.getColor("FFEAEA"), SYSTools.getColor("FFEAEA"),
            SYSTools.getColor("FFFDFD"), SYSTools.getColor("FFFDFD")
    };

    public static Color[] red2 = new Color[]{
            SYSTools.getColor("B9264F"), SYSTools.getColor("D73E68"),
            SYSTools.getColor("DD597D"), SYSTools.getColor("E37795"),
            SYSTools.getColor("E994AB"), SYSTools.getColor("EDA9BC"),
            SYSTools.getColor("F0B9C8"), SYSTools.getColor("F4CAD6"),
            SYSTools.getColor("F8DAE2"), SYSTools.getColor("FAE7EC"),
            SYSTools.getColor("FEFAFB"), SYSTools.getColor("FEFAFB")
    };

    public static Color[] orange1 = new Color[]{
            SYSTools.getColor("FF800D"), SYSTools.getColor("FF9C42"),
            SYSTools.getColor("FFAC62"), SYSTools.getColor("FFBD82"),
            SYSTools.getColor("FFC895"), SYSTools.getColor("FFCEA2"),
            SYSTools.getColor("FFD7B3"), SYSTools.getColor("FFE2C8"),
            SYSTools.getColor("FFE6D0"), SYSTools.getColor("FFF1E6"),
            SYSTools.getColor("FFF9F4"), SYSTools.getColor("FFF9F4")
    };

    public static Color[] green1 = new Color[]{
            SYSTools.getColor("1FCB4A"), SYSTools.getColor("27DE55"),
            SYSTools.getColor("4AE371"), SYSTools.getColor("7CEB98"),
            SYSTools.getColor("93EEAA"), SYSTools.getColor("A4F0B7"),
            SYSTools.getColor("BDF4CB"), SYSTools.getColor("D6F8DE"),
            SYSTools.getColor("E3FBE9"), SYSTools.getColor("E3FBE9"),
            SYSTools.getColor("FAFEFB"), SYSTools.getColor("FFFFFF")
    };

    public static Color[] green2 = new Color[]{
            SYSTools.getColor("4A9586"), SYSTools.getColor("5EAE9E"),
            SYSTools.getColor("74BAAC"), SYSTools.getColor("8DC7BB"),
            SYSTools.getColor("A5D3CA"), SYSTools.getColor("C0E0DA"),
            SYSTools.getColor("CFE7E2"), SYSTools.getColor("DCEDEA"),
            SYSTools.getColor("E7F3F1"), SYSTools.getColor("F2F9F8"),
            SYSTools.getColor("F7FBFA"), SYSTools.getColor("FFFFFF")
    };

    public static Color colorWeekday = SYSTools.getColor("FFE6D0");
    public static Color colorWeekend = SYSTools.getColor("FFC895");
    public static Color colorHolliday = SYSTools.getColor("FF800D");

    //Gray50  	747170
    public static char eurosymbol = '\u20AC';
    public static final GregorianCalendar VERY_BEGINNING = new GregorianCalendar(1970, GregorianCalendar.JANUARY, 1, 0, 0, 0);
    public static final GregorianCalendar UNTIL_FURTHER_NOTICE = new GregorianCalendar(9999, GregorianCalendar.DECEMBER, 31, 23, 59, 59);
    //    public static final GregorianCalendar BIS_AUF_WEITERES_WO_TIME = new GregorianCalendar(9999, GregorianCalendar.DECEMBER, 31, 0, 0, 0);
    public static final Date DATE_THE_VERY_BEGINNING = new Date(VERY_BEGINNING.getTimeInMillis());
    public static final Date DATE_UNTIL_FURTHER_NOTICE = new Date(UNTIL_FURTHER_NOTICE.getTimeInMillis());
//    public static final Date DATE_BIS_AUF_WEITERES_WO_TIME = new Date(BIS_AUF_WEITERES_WO_TIME.getTimeInMillis());
    //    public static final Timestamp TS_VON_ANFANG_AN = new Timestamp(VERY_BEGINNING.getTimeInMillis());
//    public static final Timestamp TS_BIS_AUF_WEITERES = new Timestamp(UNTIL_FURTHER_NOTICE.getTimeInMillis());
//    public static final String MYSQL_DATETIME_VON_ANFANG_AN = "'1000-01-01 00:00:00'";
//    public static final String MYSQL_DATETIME_BIS_AUF_WEITERES = "'9999-12-31 23:59:59'";
//    public static final int MALE = 1;
//    public static final int FEMALE = 2;

    public static final String UNITS[] = {"", OPDE.lang.getString("misc.msg.piece"), "ml", "l", "mg", "g", "cm", "m"}; // Für AnwEinheit, PackEinheit, Dimension

    public static final byte UZ = 0; // Solluhrzeit
    public static final byte FM = 1; // Nacht Morgens
    public static final byte MO = 2; // Morgens
    public static final byte MI = 3; // Mittags
    public static final byte NM = 4; // Nachmittags
    public static final byte AB = 5; // Abends
    public static final byte NA = 6; // Nacht Abends

    public static final int ZEIT_ALLES = -1;
    public static final int ZEIT_NACHT_MO = 0;
    public static final int ZEIT_FRUEH = 1;
    public static final int ZEIT_SPAET = 2;
    public static final int ZEIT_NACHT_AB = 3;
//    public static final String ZEIT[] = {"Alles", "Nacht, morgens", "Früh", "Spät", "Nacht, abends"};


    public static final String html_arial14 = "face=\"" + ARIAL14.getFamily() + "\"";
    public static final String html_fontface = "<font " + html_arial14 + " >";
    public static final String html_h1_open = "<h1 id=\"fonth1\" >";
    public static final String html_h1_close = "</h1>";
    public static final String html_h2_open = "<h2 id=\"fonth2\" >";
    public static final String html_h2_close = "</h2>";
    public static final String html_h3_open = "<h3 id=\"fonth3\" >";
    public static final String html_h3_close = "</h3>";

    public static String html_ul(String content) {
        return "<ul id=\"fonttext\">\n" + SYSTools.xx(content) + "</ul>\n";
    }

    public static String html_ol(String content) {
        return "<ol id=\"fonttext\">\n" + SYSTools.xx(content) + "</ol>\n";
    }

    public static String html_li(String content) {
        return "<li>" + SYSTools.xx(content) + "</li>\n";
    }

    public static String html_table_th(String content, String align) {
        return "<th " + SYSTools.catchNull(align, "align=\"", "\"") + ">" + SYSTools.xx(content) + "</th>\n";
    }

    public static String html_table_th(String content) {
        return "<th>" + SYSTools.xx(content) + "</th>\n";
    }

    public static String html_table_td(String content, String align) {
        return "<td " + SYSTools.catchNull(align, "align=\"", "\"") + ">" + SYSTools.xx(content) + "</td>\n";
    }

    public static String html_table_td(String content, String align, String valign) {
        return "<td " + SYSTools.catchNull(align, "align=\"", "\"") + " " + SYSTools.catchNull(valign, "valign=\"", "\"") + ">" + SYSTools.xx(content) + "</td>\n";
    }

    public static String html_table_td(String content) {
        return html_table_td(content, null);
    }

    public static String html_table_td(String content, boolean bold) {
        return html_table_td((bold ? "<b>" : "") + content + (bold ? "</b>" : ""), null);
    }

    public static String html_table_tr(String content) {
        return "<tr>" + SYSTools.xx(content) + "</tr>\n";
    }

    public static String html_table_tr(String content, boolean highlight) {
        return "<tr " + (highlight ? "id=\"fonttextgray\"" : "") + ">" + SYSTools.xx(content) + "</tr>\n";
    }

    public static String html_bold(String content) {
        return "<b>" + SYSTools.xx(content) + "</b>";
    }

    public static String html_italic(String content) {
            return "<i>" + SYSTools.xx(content) + "</i>";
        }

    public static String html_paragraph(String content) {
        return "<p id=\"fonttext\">\n" + SYSTools.xx(content) + "</p>\n";
    }

    public static String html_div(String content) {
        return "<div id=\"fonttext\">\n" + SYSTools.xx(content) + "</div>\n";
    }

    public static String html_h1(String content) {
        return "<h1 id=\"fonth1\" >" + SYSTools.xx(content) + "</h1>\n";
    }

    public static String html_h2(String content) {
        return "<h2 id=\"fonth2\" >" + SYSTools.xx(content) + "</h2>\n";
    }

    public static String html_h3(String content) {
        return "<h3 id=\"fonth3\" >" + SYSTools.xx(content) + "</h3>\n";
    }

    public static String html_table(String content, String border) {
        return "<table id=\"fonttext\" border=\"" + border + "\">" + SYSTools.xx(content) + "</table>\n";
    }

    public static final String html_div_open = "<div id=\"fonttext\">";
    public static final String html_div_close = "</div>";

//    public static final String html_report_footer = "<hr/>" +
//            html_fontface +
//            "<b>" + OPDE.lang.getString("misc.msg.endofreport") + "</b><br/>" + (OPDE.getLogin() != null ? SYSTools.htmlUmlautConversion(OPDE.getLogin().getUser().getFullname()) : "")
//            + "<br/>" + DateFormat.getDateTimeInstance().format(new Date())
//            + "<br/>http://www.offene-pflege.de</font>\n";

    public static final int SCROLL_TIME_FAST = 500; // for the sliding splitpanes

    public static HashMap getNachnamenAnonym() {
        HashMap hm = new HashMap();
        hm.put("a", new String[]{"Anders", "Ackerman", "Acord", "Adams", "Addison"});
        hm.put("b", new String[]{"Baesman", "Bahden", "Bailie", "Bäke", "Baker"});
        hm.put("c", new String[]{"Clefisch", "Cleimann", "Clemann", "Clever", "Cleverdon"});
        hm.put("d", new String[]{"Dammann", "Dammer", "Dammermann", "Damschröder", "Dankel"});
        hm.put("e", new String[]{"Ellebracht", "Ellerbrock", "Ellerkamp", "Ellermann", "Ellinghaus"});
        hm.put("f", new String[]{"Fehring", "Feickert", "Feistkorn", "Feldhus", "Feldmann"});
        hm.put("g", new String[]{"Gaunert", "Gausebrink", "Gausmann", "Geck", "Gehl"});
        hm.put("h", new String[]{"Habighorst", "Hackemüller", "Hackemöller", "Hackmann", "Hackstedt"});
        hm.put("i", new String[]{"Imbusch", "Imeyer", "Imholz", "Irmer", "Irmscher"});
        hm.put("j", new String[]{"Jensen", "Jobstvogt", "Jobusch", "Joeckle", "Joesting"});
        hm.put("k", new String[]{"Kalmey", "Kalthof", "Kamlage", "Kammerer", "Kamp"});
        hm.put("l", new String[]{"Lohfener", "Löhr", "Lohrbach", "Lohse", "Long"});
        hm.put("m", new String[]{"Magna", "Mailänder", "Malasse", "Mandrella", "Mann"});
        hm.put("n", new String[]{"Nehring", "Nelson", "Nelz", "Nendel", "Nentrup"});
        hm.put("o", new String[]{"Obermeyer", "Obermüller", "Oberniehaus", "Ostmann", "Oberwahrenbrock"});
        hm.put("p", new String[]{"Papenburg", "Pardieck", "Parr", "Pörsch", "Partzsch"});
        hm.put("q", new String[]{"Quam", "Quark", "Quast", "Quest", "Quench"});
        hm.put("r", new String[]{"Ramms", "Randall", "Rappold", "Raschack", "Rathert"});
        hm.put("s", new String[]{"Sandkühler", "Sandner", "Sandy", "Sarner", "Sarvela"});
        hm.put("t", new String[]{"Tegeder", "Teigeler", "Tellmann", "Temme", "Tessmann"});
        hm.put("u", new String[]{"Ulbricht", "Ullmann", "Ullrich", "Unland", "Unnerstall"});
        hm.put("v", new String[]{"Vegesack", "Vehling", "Vehring", "Vemmer", "Venckhaus"});
        hm.put("w", new String[]{"Wanscheer", "Warber", "Ward", "Warnecke", "Warner"});
        hm.put("x", new String[]{"Xaver", "Xanderin", "Xanders", "Xandri", "Xanking"});
        hm.put("y", new String[]{"Yanker", "Yareck", "Yaritz", "Yark", "Yarletts"});
        hm.put("z", new String[]{"Zeiser", "Zeretzki", "Ziebart", "Ziegemeier", "Zieger"});
        hm.put("ä", new String[]{"Anders", "Ackerman", "Acord", "Adams", "Addison"});
        hm.put("ö", new String[]{"Obermeyer", "Obermüller", "Oberniehaus", "Ostmann", "Oberwahrenbrock"});
        hm.put("ü", new String[]{"Ulbricht", "Ullmann", "Ullrich", "Unland", "Unnerstall"});
        return hm;
    }

    public static HashMap getVornamenFrauAnonym() {
        HashMap hm = new HashMap();
        hm.put("a", new String[]{"Adrina", "Agnes", "Alexandra", "Alina", "Amelie"});
        hm.put("b", new String[]{"Barbara", "Beate", "Berit", "Berta", "Bettina"});
        hm.put("c", new String[]{"Celina", "Celine", "Charlotte", "Corinna", "Cornelia"});
        hm.put("d", new String[]{"Dagmar", "Daniela", "Daria", "Doreen", "Denise"});
        hm.put("e", new String[]{"Edith", "Eleonora", "Elfriede", "Erika", "Elisa"});
        hm.put("f", new String[]{"Fabienne", "Finja", "Fiona", "Franziska", "Frieda"});
        hm.put("g", new String[]{"Gabriele", "Galadriel", "Gerda", "Gertrud", "Gisela"});
        hm.put("h", new String[]{"Heike", "Helen", "Helena", "Helene", "Helga"});
        hm.put("i", new String[]{"Ilse", "Imke", "Inge", "Ingeborg", "Ingrid"});
        hm.put("j", new String[]{"Jacqueline", "Jana", "Janin", "Janina", "Jasmin"});
        hm.put("k", new String[]{"Krista", "Kristiane", "Kristin", "Kristina", "Klaudia"});
        hm.put("l", new String[]{"Lara", "Laura", "Lea", "Lena", "Leni"});
        hm.put("m", new String[]{"Mandy", "Manou", "Manuela", "Mareike", "Margarethe"});
        hm.put("n", new String[]{"Nadine", "Natalie", "Nele", "Natascha", "Nicole"});
        hm.put("o", new String[]{"Olga", "Oliana", "Olisa", "Olivia", "Ottilie"});
        hm.put("p", new String[]{"Paula", "Pauline", "Petra", "Pia", "Patrizia"});
        hm.put("q", new String[]{"Quella", "Quenby", "Querida", "Quilla", "Quintia"});
        hm.put("r", new String[]{"Ranya", "Rebekka", "Regina", "Renate", "Ronja"});
        hm.put("s", new String[]{"Sabine", "Sabrina", "Sandra", "Sara", "Sascha"});
        hm.put("t", new String[]{"Tania", "Tara", "Thea", "Tiana", "Tiffany"});
        hm.put("u", new String[]{"Ute", "Ursula", "Uschi", "Ulrike", "Ursa"});
        hm.put("v", new String[]{"Vanessa", "Vera", "Viktoria", "Veronika", "Verena"});
        hm.put("w", new String[]{"Wally", "Walli", "Waltheide", "Waltraud", "Wanda"});
        hm.put("x", new String[]{"Xandra", "Xaveria", "Xynthia", "Xaviera", "Xenia"});
        hm.put("y", new String[]{"Yvonne", "Yasmin", "Yana", "Yola", "Yuki"});
        hm.put("z", new String[]{"Zatiye", "Zäzilie", "Zdenka", "Zelda", "Zia"});
        hm.put("ä", new String[]{"Adrina", "Agnes", "Alexandra", "Alina", "Amelie"});
        hm.put("ö", new String[]{"Olga", "Oliana", "Olisa", "Olivia", "Ottilie"});
        hm.put("ü", new String[]{"Ute", "Ursula", "Uschi", "Ulrike", "Ursa"});
        return hm;
    }

    public static HashMap getVornamenMannAnonym() {
        HashMap hm = new HashMap();
        hm.put("a", new String[]{"Aaron", "Adrian", "Albert", "Alexander", "Alfred"});
        hm.put("b", new String[]{"Bastian", "Bela", "Ben", "Benjamin", "Bernd"});
        hm.put("c", new String[]{"Christian", "Christoph", "Christopher", "Claus", "Carl"});
        hm.put("d", new String[]{"Daniel", "David", "Dennis", "Detlef", "Dieter"});
        hm.put("e", new String[]{"Elias", "Emil", "Erik", "Ernst", "Erich"});
        hm.put("f", new String[]{"Fabian", "Felix", "Ferdinand", "Florian", "Frank"});
        hm.put("g", new String[]{"Georg", "Gerd", "Gerhard", "Gustav", "Günther"});
        hm.put("h", new String[]{"Hagen", "Hannes", "Hartmut", "Hans", "Harald"});
        hm.put("i", new String[]{"Ingo", "Ingobald", "Ingolf", "Ingram", "Ingwar"});
        hm.put("j", new String[]{"Jakob", "Jan", "Jens", "Joachim", "Josef"});
        hm.put("k", new String[]{"Kristian", "Kristoph", "Kristopher", "Klaus", "Karl"});
        hm.put("l", new String[]{"Lars", "Lasse", "Leo", "Leon", "Ludwig"});
        hm.put("m", new String[]{"Manfred", "Manuel", "Marcel", "Markus", "Mario"});
        hm.put("n", new String[]{"Nevio", "Nick", "Nico", "Niklas", "Norbert"});
        hm.put("o", new String[]{"Olaf", "Ole", "Oliver", "Oskar", "Otto"});
        hm.put("p", new String[]{"Pascal", "Patrick", "Paul", "Peter", "Philipp"});
        hm.put("q", new String[]{"Quentin", "Quico", "Quillan", "Quinlan", "Quinn"});
        hm.put("r", new String[]{"Ralf", "René", "Richard", "Robert", "Rudolf"});
        hm.put("s", new String[]{"Sebastian", "Simon", "Stefan", "Steffen", "Sven"});
        hm.put("t", new String[]{"Thomas", "Timotheus", "Tobias", "Torsten", "Tarek"});
        hm.put("u", new String[]{"Uberto", "Ulrich", "Uwe", "Udo", "Ulf"});
        hm.put("v", new String[]{"Vaclav", "Vincent", "Volker", "Vico", "Valentin"});
        hm.put("w", new String[]{"Walter", "Werner", "Wilhelm", "Willi", "Wolfgang"});
        hm.put("x", new String[]{"Xaver", "Xander", "Xaverius", "Xavier", "Xerxes"});
        hm.put("y", new String[]{"Yanick", "Yann", "Yannis", "Yash", "Yashodhan"});
        hm.put("z", new String[]{"Zacharias", "Zaki", "Zafer", "Zadok", "Zenobius"});
        hm.put("ä", new String[]{"Aaron", "Adrian", "Albert", "Alexander", "Alfred"});
        hm.put("ö", new String[]{"Olaf", "Ole", "Oliver", "Oskar", "Otto"});
        hm.put("ü", new String[]{"Uberto", "Ulrich", "Uwe", "Udo", "Ulf"});
        return hm;
    }

    //    public static final Icon icon16bysecond = new ImageIcon(SYSConst.class.getResource("/artwork/16x16/bw/bysecond.png"));
    public static final Icon icon16exec = new ImageIcon(SYSConst.class.getResource("/artwork/16x16/exec.png"));
    //    public static final Icon icon16pit = new ImageIcon(SYSConst.class.getResource("/artwork/16x16/bw/pointintime.png"));
    public static final Icon icon16redStar = new ImageIcon(SYSConst.class.getResource("/artwork/16x16/redstar.png"));
    public static final Icon icon16greenStar = new ImageIcon(SYSConst.class.getResource("/artwork/16x16/greenstar.png"));
    public static final Icon icon22redStar = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/redstar.png"));
    public static final Icon icon22greenStar = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/greenstar.png"));
    public static final Icon icon16unlink = new ImageIcon(SYSConst.class.getResource("/artwork/16x16/unlink.png"));
    public static final Icon icon16unlinkPressed = new ImageIcon(SYSConst.class.getResource("/artwork/16x16/unlink_pressed.png"));
    public static final Icon icon22add = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/bw/add.png"));
    public static final Icon icon22addGroup = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/add_group.png"));
    public static final Icon icon22addPressed = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/bw/add-pressed.png"));
    public static final Icon icon22addUser = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/add_user.png"));
    public static final Icon icon22addbw = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/add_bw.png"));
    public static final Icon icon22ambulance2 = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/ambulance2.png"));
    public static final Icon icon22ambulance2Pressed = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/ambulance2_pressed.png"));
    public static final Icon icon22apply = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/apply.png"));
    public static final Icon icon16apply = new ImageIcon(SYSConst.class.getResource("/artwork/16x16/apply.png"));
    public static final Icon icon22applyPressed = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/apply_pressed.png"));
    public static final Icon icon22attach = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/bw/attach.png"));
    public static final Icon icon22attachPressed = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/bw/attach_pressed.png"));
    public static final Icon icon22calendar = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/calenders.png"));
    public static final Icon icon22calendarPressed = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/calenders_pressed.png"));
    public static final Icon icon22cancel = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/cancel.png"));
    public static final Icon icon22cancelPressed = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/cancel_pressed.png"));
    public static final Icon icon22changePeriod = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/bw/reload_page.png"));
    public static final Icon icon22changePeriodPressed = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/bw/reload_page_pressed.png"));
    public static final Icon icon22clock = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/bw/clock.png"));
    public static final Icon icon22clock1 = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/clock.png"));
    public static final Icon icon22clockPressed = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/bw/edit3_pressed.png"));
    public static final Icon icon22delete = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/bw/editdelete.png"));
    public static final Icon icon22deletePressed = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/bw/editdelete_pressed.png"));
    public static final Icon icon22deleteall = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/deleteall.png"));
    public static final Icon icon22deleteallPressed = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/deleteall_pressed.png"));
    public static final Icon icon22down = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/bw/1downarrow.png"));
    public static final Icon icon22edit = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/bw/kspread.png"));
    public static final Icon icon22edit3 = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/bw/edit3.png"));
    public static final Icon icon22menu = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/bw/menu.png"));
    public static final Icon icon32menu = new ImageIcon(SYSConst.class.getResource("/artwork/32x32/bw/menu.png"));
    public static final Icon icon32Pressed = new ImageIcon(SYSConst.class.getResource("/artwork/32x32/bw/pressed.png"));
    public static final Icon icon22Pressed = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/bw/pressed.png"));
    public static final Icon icon22edit3Pressed = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/bw/edit3_pressed.png"));
    //    public static final Icon icon22edit1 = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/edit1.png"));
//    public static final Icon icon22edit1Pressed = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/edit1_pressed.png"));
    public static final Icon icon22editPressed = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/bw/kspread_pressed.png"));
    public static final Icon icon22empty = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/empty.png"));
    public static final Icon icon22eraser = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/eraser.png"));
    public static final Icon icon22emptyPressed = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/empty_pressed.png"));
    public static final Icon icon22give = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/hand-over.png"));
    public static final Icon icon22givePressed = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/hand-over_pressed.png"));
    public static final Icon icon22gotoEnd = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/bw/player_end.png"));
    public static final Icon icon22gotoEndPressed = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/bw/player_end_pressed.png"));
    public static final Icon icon22info = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/info.png"));
    public static final Icon icon22infogreen2 = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/infogreen2.png"));
    public static final Icon icon22infoPressed = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/info_pressed.png"));
    public static final Icon icon22infoblue = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/infoblue.png"));
    public static final Icon icon22infogreen = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/infogreen.png"));
    public static final Icon icon22infored = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/infored.png"));
    public static final Icon icon22infoyellow = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/infoyellow.png"));
    public static final Icon icon22link = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/bw/link.png"));
    public static final Icon icon22linkPressed = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/bw/link_pressed.png"));
    public static final Icon icon22myself = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/me.png"));
    public static final Icon icon22password = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/password.png"));
    public static final Icon icon22passwordPressed = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/password_pressed.png"));
    public static final Icon icon22playerPlay = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/bw/player_play.png"));
    public static final Icon icon22playerPlayPressed = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/bw/player_play_pressed.png"));
    public static final Icon icon22playerStart = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/bw/player_start.png"));
    public static final Icon icon22playerStartPressed = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/bw/player_start_pressed.png"));
    public static final Icon icon22playerStop = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/bw/player_stop.png"));
    public static final Icon icon22playerStopPressed = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/bw/player_stop_pressed.png"));
    //    public static final Icon icon22print = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/printer1.png"));
//    public static final Icon icon22printPressed = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/printer1_pressed.png"));
    public static final Icon icon22print2 = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/bw/printer2.png"));
    public static final Icon icon22print2Pressed = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/bw/printer2_pressed.png"));
    public static final Icon icon22redo = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/bw/redo.png"));
    public static final Icon icon22calc = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/calc.png"));
    public static final Icon icon22redoPressed = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/bw/redo_pressed.png"));
    public static final Icon icon22residentAbsent = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/resident-absent.png"));
    public static final Icon icon22residentActive = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/resident-active.png"));
    public static final Icon icon22residentBack = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/resident-back.png"));
    public static final Icon icon22residentBoth = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/resident-both.png"));
    public static final Icon icon22residentDied = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/resident-died.png"));
    public static final Icon icon22residentGone = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/resident-gone.png"));
    public static final Icon icon22residentInactive = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/resident-inactive.png"));
    public static final Icon icon22stop = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/bw/player_stop.png"));
    public static final Icon icon22stopPressed = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/bw/player_stop_pressed.png"));
    public static final Icon icon22take = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/take-over.png"));
    public static final Icon icon22takePressed = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/take-over_pressed.png"));
    public static final Icon icon22todo = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/korganizer_todo.png"));
    public static final Icon icon22todoPressed = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/korganizer_todo_pressed.png"));
    public static final Icon icon22checkbox = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/bw/checkbox.png"));
    public static final Icon icon22checkboxPressed = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/bw/checkbox_pressed.png"));
    public static final Icon icon22undo = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/bw/undo.png"));
    public static final Icon icon22unlink = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/unlink.png"));
    public static final Icon icon22edited = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/edited.png"));
    public static final Icon icon22unlinkPressed = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/unlink_pressed.png"));
    public static final Icon icon22up = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/bw/1uparrow.png"));
    //    public static final Icon icon22view = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/bw/viewmag.png"));
//    public static final Icon icon22viewPressed = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/bw/viewmag-selected.png"));
    public static final Icon icon32ambulance2 = new ImageIcon(SYSConst.class.getResource("/artwork/32x32/ambulance2.png"));
    public static final Icon icon32ambulance2Pressed = new ImageIcon(SYSConst.class.getResource("/artwork/32x32/ambulance2_pressed.png"));
    public static final Icon icon48ambulance2 = new ImageIcon(SYSConst.class.getResource("/artwork/48x48/ambulance2.png"));
    public static final Icon icon48ambulance2Pressed = new ImageIcon(SYSConst.class.getResource("/artwork/32x32/ambulance2_pressed.png"));
    public static final Icon icon48delete = new ImageIcon(SYSConst.class.getResource("/artwork/48x48/bw/editdelete.png"));
    public static final Icon icon48give = new ImageIcon(SYSConst.class.getResource("/artwork/48x48/hand-over.png"));
    public static final Icon icon48kgetdock = new ImageIcon(SYSConst.class.getResource("/artwork/48x48/kget_dock.png"));
    public static final Icon icon48play = new ImageIcon(SYSConst.class.getResource("/artwork/48x48/bw/player_play.png"));
    public static final Icon icon48undo = new ImageIcon(SYSConst.class.getResource("/artwork/48x48/bw/undo.png"));
    public static final Icon icon48stop = new ImageIcon(SYSConst.class.getResource("/artwork/48x48/bw/player_stop.png"));
    public static final Icon icon48take = new ImageIcon(SYSConst.class.getResource("/artwork/48x48/take-over.png"));
    //    public static final Icon icon16byday = new ImageIcon(SYSConst.class.getResource("/artwork/16x16/bw/byday.png"));
    public static final Icon icon22collapse = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/bw/collapse.png"));
    public static final Icon icon22expand = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/bw/expand.png"));
    public static final Icon icon22ledRedOn = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/ledred.png"));
    public static final Icon icon22ledRedOff = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/leddarkred.png"));
    public static final Icon icon22ledYellowOn = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/ledyellow.png"));
    public static final Icon icon22ledYellowOff = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/leddarkyellow.png"));
    public static final Icon icon22ledGreenOn = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/ledgreen.png"));
    public static final Icon icon22ledGreenOff = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/leddarkgreen.png"));
    public static final Icon icon16ledGreenOn = new ImageIcon(SYSConst.class.getResource("/artwork/16x16/ledgreen.png"));
    public static final Icon icon163ledGreenOn = new ImageIcon(SYSConst.class.getResource("/artwork/16x16/3ledlightgreen.png"));
    public static final Icon icon16ledGreenOff = new ImageIcon(SYSConst.class.getResource("/artwork/16x16/leddarkgreen.png"));
    public static final Icon icon22ledOrangeOn = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/ledorange.png"));
    public static final Icon icon22ledOrangeOff = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/leddarkorange.png"));
    public static final Icon icon22ledPurpleOn = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/ledpurple.png"));
    public static final Icon icon22ledPurpleOff = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/leddarkpurple.png"));
    public static final Icon icon22ledBlueOn = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/ledlightblue.png"));
    public static final Icon icon22ledBlueOff = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/leddarkblue.png"));
    public static final Icon icon22singleIncident = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/single-incident.png"));
    public static final Icon icon22intervalByDay = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/by-day.png"));
    public static final Icon icon22intervalBySecond = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/by-second.png"));
    public static final Icon icon22intervalNoConstraints = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/no-constraints.png"));
    public static final Icon icon22nothing = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/nothing.png"));
    public static final Icon icon22stopSign = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/stop.png"));
    public static final Icon icon22magnify1 = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/magnify1.png"));
    public static final Icon icon22home = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/home.png"));
    public static final Icon icon22wizard = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/wizard.png"));
    public static final Icon icon22addrow = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/shetaddrow.png"));
    public static final Icon icon22biohazard = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/biohazard.png"));
    public static final Icon icon48biohazard = new ImageIcon(SYSConst.class.getResource("/artwork/48x48/biohazard.png"));
    public static final Icon icon22diabetes = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/diabetes.png"));
    public static final Icon icon22warning = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/warning.png"));
    public static final Icon icon22allergy = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/allergy.png"));
    public static final Icon gfx259x203medic0 = new ImageIcon(SYSConst.class.getResource("/artwork/other/medicine0.png"));
    public static final Icon gfx259x203medic1 = new ImageIcon(SYSConst.class.getResource("/artwork/other/medicine1.png"));
    public static final Icon gfx259x203medic2 = new ImageIcon(SYSConst.class.getResource("/artwork/other/medicine2.png"));
    public static final Icon gfx259x203medic3 = new ImageIcon(SYSConst.class.getResource("/artwork/other/medicine3.png"));
    public static final Icon gfx259x203medic4 = new ImageIcon(SYSConst.class.getResource("/artwork/other/medicine4.png"));

    //
//    public static final Icon icon22collapsePressed = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/collapse_pressed.png"));
//    public static final Icon icon22expandPressed = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/collapse_arrow_pressed.png"));

}
