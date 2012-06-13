/*
* To change this template, choose Tools | Templates
* and open the template in the editor.
*/
package entity;

import entity.files.SYSFiles;
import entity.files.Syspb2file;
import entity.vorgang.SYSPB2VORGANG;
import entity.vorgang.Vorgaenge;
import op.OPDE;
import op.tools.SYSCalendar;
import op.tools.SYSConst;
import op.tools.SYSTools;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;

/**
 * @author tloehr
 */
public class PflegeberichteTools {

    /**
     * setzt einen Pflegebericht auf "gelöscht". Das heisst, er ist dann inaktiv. Es werden auch Datei und Vorgangs
     * Zuordnungen entfernt.
     *
     * @param bericht
     * @return
     */
    public static boolean deleteBericht(Pflegeberichte bericht) {
        boolean success = false;
        EntityManager em = OPDE.createEM();
        em.getTransaction().begin();
        try {
            bericht.setEditedBy(OPDE.getLogin().getUser());
            bericht.setEditpit(new Date());

            // Datei Zuordnungen entfernen
            Iterator<Syspb2file> files = bericht.getAttachedFiles().iterator();
            while (files.hasNext()) {
                Syspb2file oldAssignment = files.next();
                em.remove(oldAssignment);
            }
            bericht.getAttachedFiles().clear();

            // Vorgangszuordnungen entfernen
            Iterator<SYSPB2VORGANG> vorgaenge = bericht.getAttachedVorgaenge().iterator();
            while (vorgaenge.hasNext()) {
                // gleichfalls
                SYSPB2VORGANG oldAssignment = vorgaenge.next();
                em.remove(oldAssignment);
            }
            bericht.getAttachedVorgaenge().clear();

            em.merge(bericht);
            em.getTransaction().commit();
            success = true;
        } catch (Exception e) {
            OPDE.getLogger().error(e.getMessage(), e);
            em.getTransaction().rollback();
        } finally {
            em.close();
        }
        return success;
    }

    public static Pflegeberichte getFirstBericht(Bewohner bewohner) {
        EntityManager em = OPDE.createEM();
        Query query = em.createNamedQuery("Pflegeberichte.findAllByBewohner");
        query.setParameter("bewohner", bewohner);
        query.setFirstResult(0);
        query.setMaxResults(1);
        Pflegeberichte p = (Pflegeberichte) query.getSingleResult();
        em.close();
        return p;
    }


    /**
     * Führt die notwendigen Änderungen an den Entities durch, wenn ein Bericht geändert wurde. Dazu gehört auch die Dateien
     * und Vorgänge umzubiegen. Der alte Bericht verliert seine Dateien und Vorgänge. Es werden auch die
     * notwendigen Querverweise zwischen dem alten und dem neuen Bericht erstellt.
     *
     * @param oldBericht der Bericht, der durch den <code>newBericht</code> ersetzt werden soll.
     * @param newBericht siehe oben
     * @return Erfolg oder nicht
     */
    public static boolean changeBericht(Pflegeberichte oldBericht, Pflegeberichte newBericht) {
        boolean success = false;
        EntityManager em = OPDE.createEM();
        em.getTransaction().begin();
        try {
            newBericht.setReplacementFor(oldBericht);
            // Dateien umbiegen
            Iterator<Syspb2file> files = oldBericht.getAttachedFiles().iterator();
            while (files.hasNext()) {
                // Diesen Umweg muss ich wählen, dass Syspb2file eigentlich
                // die JOIN Relation einer M:N Rel ist.
                Syspb2file oldAssignment = files.next();
                SYSFiles file = oldAssignment.getSysfile();
                Syspb2file newAssignment = new Syspb2file(oldAssignment.getBemerkung(), file, newBericht, oldAssignment.getUser(), oldAssignment.getPit());
                newBericht.getAttachedFiles().add(newAssignment);
                em.remove(oldAssignment);
            }

            // Vorgänge umbiegen
            Iterator<SYSPB2VORGANG> vorgaenge = oldBericht.getAttachedVorgaenge().iterator();
            while (vorgaenge.hasNext()) {
                // gleichfalls
                SYSPB2VORGANG oldAssignment = vorgaenge.next();
                Vorgaenge vorgang = oldAssignment.getVorgang();
                SYSPB2VORGANG newAssignment = new SYSPB2VORGANG(vorgang, newBericht);
                newBericht.getAttachedVorgaenge().add(newAssignment);
                em.remove(oldAssignment);
            }

            em.persist(newBericht);

            oldBericht.getAttachedFiles().clear();
            oldBericht.getAttachedVorgaenge().clear();
            oldBericht.setEditedBy(OPDE.getLogin().getUser());
            oldBericht.setEditpit(new Date());
            oldBericht.setReplacedBy(newBericht);
            em.merge(oldBericht);

            em.getTransaction().commit();

            success = true;
        } catch (Exception e) {
            OPDE.error(e.getMessage());
            em.getTransaction().rollback();
        } finally {
            em.close();
        }
        return success;
    }

    /**
     * liefert eine Kopie eines Berichtes, die noch nicht persistiert wurde. * Somit ist PBID = 0
     * Gilt nicht für die Mappings (Dateien oder Vorgänge). Die werden erst bei changeBericht() geändert.
     *
     * @param source
     * @return
     */
    public static Pflegeberichte copyBericht(Pflegeberichte source) {
        Pflegeberichte target = new Pflegeberichte(source.getBewohner());
        target.setDauer(source.getDauer());
        target.setEditedBy(source.getEditedBy());
        target.setEditpit(source.getEditpit());
        target.setPit(source.getPit());
        target.setReplacedBy(source.getReplacedBy());
        target.setReplacementFor(source.getReplacementFor());
        target.setText(source.getText());
        target.setUser(source.getUser());

        Iterator<PBerichtTAGS> tags = source.getTags().iterator();
        while (tags.hasNext()) {
            PBerichtTAGS tag = tags.next();
            target.getTags().add(tag);
        }

        return target;
    }

    public static boolean saveBericht(Pflegeberichte newBericht) {
        boolean success = false;
        EntityManager em = OPDE.createEM();
        em.getTransaction().begin();
        try {
            em.persist(newBericht);
            em.getTransaction().commit();
            success = true;
        } catch (Exception e) {
            OPDE.getLogger().error(e.getMessage(), e);
            em.getTransaction().rollback();
        } finally {
            em.close();
        }
        return success;
    }

    /**
     * Berichtdarstellung für die Vorgänge.
     *
     * @param bericht
     * @param mitBWKennung
     * @return
     */
    public static String getBerichtAsHTML(Pflegeberichte bericht, boolean mitBWKennung) {
        String html = "";
        String text = SYSTools.replace(bericht.getText(), "\n", "<br/>");

        if (mitBWKennung) {
            html += "<b>Pflegebericht für " + BewohnerTools.getBWLabelText(bericht.getBewohner()) + "</b>";
        } else {
            html += "<b>Pflegebericht</b>";
        }
        html += "<p>" + text + "</p>";
        return html;
    }

    public static String getPITAsHTML(Pflegeberichte bericht) {
        DateFormat df = new SimpleDateFormat("EEE, dd.MM.yyyy HH:mm");
        String html = "";
        html += df.format(bericht.getPit()) + "; " + bericht.getUser().getNameUndVorname();
        return html;
    }

    public static String getBerichteAsHTML(List<Pflegeberichte> berichte, boolean nurBesonderes) {
        String html = "";

        int num = berichte.size();
        if (num > 0) {
            html += "<h1>Pflegeberichte für " + BewohnerTools.getBWLabelText(berichte.get(0).getBewohner()) + "</h1>"; // Die Bewohner in dieser Liste sind alle dieselben.
            html += "<table border=\"1\" cellspacing=\"0\"><tr>"
                    + "<th>Info</th><th>Text</th></tr>";
            Iterator<Pflegeberichte> it = berichte.iterator();
            while (it.hasNext()) {
                Pflegeberichte bericht = it.next();

                if (!nurBesonderes || bericht.isBesonders()) {
                    html += "<tr>";
                    html += "<td>" + getDatumUndUser(bericht, false) + "</td>";
                    html += "<td>" + getAsHTML(bericht) + "</td>";
                    html += "</tr>";

                }

            }
            html += "</table>";
        } else {
            html += "<i>keine Berichte in der Auswahl vorhanden</i>";
        }

        html = "<html><head>"
                + "<title>" + SYSTools.getWindowTitle("") + "</title>"
                + "<script type=\"text/javascript\">"
                + "window.onload = function() {"
                + "window.print();"
                + "}</script></head><body>" + html + "</body></html>";
        return html;
    }

    private static String getHTMLColor(Pflegeberichte bericht) {
        String color = "";
        if (bericht.isReplaced() || bericht.isDeleted()) {
            color = SYSConst.html_lightslategrey;
        } else {
            color = SYSCalendar.getHTMLColor4Schicht(SYSCalendar.ermittleSchicht(bericht.getPit().getTime()));
        }
        return color;
    }

    public static String getTagsAsHTML(Pflegeberichte bericht) {
        String result = "<font " + getHTMLColor(bericht) + ">";
        Iterator<PBerichtTAGS> itTags = bericht.getTags().iterator();
        while (itTags.hasNext()) {
            PBerichtTAGS tag = itTags.next();
            result += (tag.isBesonders() ? "<b>" : "");
            result += tag.getKurzbezeichnung();
            result += (tag.isBesonders() ? "</b>" : "");
            result += (itTags.hasNext() ? " " : "");
        }
        result += "</font>";
        return SYSTools.toHTML(result);
    }

    public static String getDatumUndUser(Pflegeberichte bericht, boolean showIDs) {
        String result = "";
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd.MM.yyyy HH:mm");
        result = sdf.format(bericht.getPit()) + "; " + bericht.getUser().getNameUndVorname();
        if (showIDs) {
            result += "<br/><i>(" + bericht.getPbid() + ")</i>";
        }
        return "<font " + getHTMLColor(bericht) + ">" + result + "</font>";
    }

    /**
     * gibt eine HTML Darstellung des Berichtes zurück.
     *
     * @return
     */
    public static String getAsHTML(Pflegeberichte bericht) {
        String result = "";

        String fonthead = "<font " + getHTMLColor(bericht) + ">";

        DateFormat df = DateFormat.getDateTimeInstance();
        //result += (flags.equals("") ? "" : "<b>" + flags + "</b><br/>");
        if (bericht.isDeleted()) {
            result += "<i>Gelöschter Eintrag. Gelöscht am/um: " + df.format(bericht.getEditpit()) + " von " + bericht.getEditedBy().getNameUndVorname() + "</i><br/>";
        }
        if (bericht.isReplacement()) {
            result += "<i>Dies ist ein Eintrag, der nachbearbeitet wurde.<br/>Am/um: " + df.format(bericht.getReplacementFor().getEditpit()) + "<br/>Der Originaleintrag hatte die Bericht-Nummer: " + +bericht.getReplacementFor().getPbid() + "</i><br/>";
        }
        if (bericht.isReplaced()) {
            result += "<i>Dies ist ein Eintrag, der durch eine Nachbearbeitung ungültig wurde. Bitte ignorieren.<br/>Änderung wurde am/um: " + df.format(bericht.getEditpit()) + " von " + bericht.getEditedBy().getNameUndVorname() + " vorgenommen.";
            result += "<br/>Der Korrektureintrag hat die Bericht-Nummer: " + bericht.getReplacedBy().getPbid() + "</i><br/>";
        }
        if (!bericht.getAttachedFiles().isEmpty()) {
            result += "<font color=\"green\">&#9679;</font>";
        }
        if (!bericht.getAttachedVorgaenge().isEmpty()) {
            result += "<font color=\"red\">&#9679;</font>";
        }
        result += SYSTools.replace(bericht.getText(), "\n", "<br/>");
        result = fonthead + result + "</font>";
        return result;
    }

    public static String getAsText(Pflegeberichte bericht) {
        String result = "";
        DateFormat df = DateFormat.getDateTimeInstance();
        if (bericht.isDeleted()) {
            result += "Gelöschter Eintrag. Gelöscht am/um: " + df.format(bericht.getEditpit()) + " von " + bericht.getEditedBy().getNameUndVorname();
            result += "\n=========================\n\n";
        }
        if (bericht.isReplacement()) {
            result += "Dies ist ein Eintrag, der nachbearbeitet wurde.\nAm/um: " + df.format(bericht.getReplacementFor().getEditpit()) + "\nDer Originaleintrag hatte die Bericht-Nummer: " + bericht.getReplacementFor().getPbid();
            result += "\n=========================\n\n";
        }
        if (bericht.isReplaced()) {
            result += "Dies ist ein Eintrag, der durch eine Nachbearbeitung ungültig wurde. Bitte ignorieren.\nÄnderung wurde am/um: " + df.format(bericht.getEditpit()) + " von " + bericht.getEditedBy().getNameUndVorname();
            result += "\nDer Korrektureintrag hat die Bericht-Nummer: " + bericht.getReplacedBy().getPbid();
            result += "\n=========================\n\n";
        }
        result += bericht.getText();
        return result;
    }

    public static String getBewohnerName(Pflegeberichte bericht) {
        String result = "";
        result = bericht.getBewohner().getNachname() + ", " + bericht.getBewohner().getVorname();
        return "<font " + getHTMLColor(bericht) + ">" + result + "</font>";
    }

    /**
     * @param em
     * @param headertiefe
     * @param bvwochen
     * @return
     */
    public static String getBVBerichte(EntityManager em, int headertiefe, int bvwochen) {
        StringBuilder html = new StringBuilder(1000);
//        String jpql = "" +
//                " SELECT  b, pb " +
//                " FROM Bewohner b " +
//                " LEFT JOIN b.pflegberichte pb " +
//                " LEFT JOIN pb.tags pbt " +
//                " WHERE pb.pit >= :datum AND pbt.kurzbezeichnung = 'BV' AND b.station IS NOT NULL AND b.adminonly <> 2 " +
//                " ORDER BY b.bWKennung, pb.pit ";


//        String sql = " SELECT b.*, a.PBID " +
//                " FROM Bewohner b " +
//                " LEFT OUTER JOIN ( " +
//                "    SELECT pb.* FROM Pflegeberichte pb " +
//                "    LEFT OUTER JOIN PB2TAGS pbt ON pbt.PBID = pb.PBID " +
//                "    LEFT OUTER JOIN PBericht_TAGS pbtags ON pbt.PBTAGID = pbtags.PBTAGID " +
//                "    WHERE pb.PIT > ? AND pbtags.Kurzbezeichnung = 'BV'" +
//                " ) a ON a.BWKennung = b.BWKennung " +
//                " WHERE b.StatID IS NOT NULL AND b.adminonly <> 2 " +
//                " ORDER BY b.BWKennung, a.pit ";
        Query query = em.createNamedQuery("Pflegeberichte.findBVAktivitaet");
        query.setParameter(1, SYSCalendar.addField(new Date(SYSCalendar.startOfDay()), bvwochen * -1, GregorianCalendar.WEEK_OF_MONTH));

//        Query query2 = em.createQuery("SELECT b FROM Bewohner b LEFT JOIN b.pflegberichte pb WHERE pb IS NULL ");
//        query.setParameter("datum", SYSCalendar.addField(new Date(SYSCalendar.startOfDay()), bvwochen * -1, GregorianCalendar.WEEK_OF_MONTH));


        List<Object[]> list = query.getResultList();
        DateFormat df = DateFormat.getDateInstance();
        html.append("<h" + headertiefe + ">");
        html.append("Berichte der BV-Tätigkeiten");
        html.append("</h" + headertiefe + ">");
        html.append("<table border=\"1\"><tr>" +
                "<th>BewohnerIn</th><th>Datum</th><th>Text</th><th>UKennung</th><th>BV</th></tr>");

        for (Object[] paar : list) {
            Bewohner bewohner = (Bewohner) paar[0];
            BigInteger pbid = (BigInteger) paar[1];

            // Bei Bedarf den Pflegebericht "einsammeln"
            Pflegeberichte bericht = pbid == null ? null : em.find(Pflegeberichte.class, pbid.longValue());

            html.append("<tr>");

            html.append("<td>" + BewohnerTools.getBWLabel1(bewohner) + "</td>");
            if (bericht == null) {
                html.append("<td align=\"center\">--</td>");
                html.append("<td><b>Keine BV Aktivitäten gefunden.</b></td>");
                html.append("<td align=\"center\">--</td>");
            } else {
                html.append("<td>" + df.format(bericht.getPit()) + "</td>");
                html.append("<td>" + bericht.getText() + "</td>");
                html.append("<td>" + bericht.getUser().getUKennung() + "</td>");
            }
            if (bewohner.getBv1() == null) {
                html.append("<td><b>kein BV zugeordnet</b></td>");
            } else {
                html.append("<td>" + bewohner.getBv1().getUKennung() + "</td>");
            }
            html.append("</tr>");
        }
        html.append("</table>");

//        if (rs.first()) {

//        }


        return html.toString();
    }


    /**
     * Durchsucht die Pflegeberichte nach einem oder mehreren Suchbegriffen
     */

    public static String getBerichteASHTML(EntityManager em, String suche, PBerichtTAGS tag, int headertiefe, int monate) {
        StringBuilder html = new StringBuilder(1000);
        String where = "";
        String htmlbeschreibung = "";
        String jpql = "";
        String order = " ORDER BY p.bewohner.bWKennung, p.pit ";

        if (suche.trim().isEmpty() && tag == null) {
            html.append("<h" + headertiefe + ">");
            html.append("Berichtsuche nicht möglich.");
            html.append("</h" + headertiefe + ">");
        } else {
            jpql = "SELECT p FROM Pflegeberichte p JOIN p.tags t WHERE p.pit >= :date AND t = :tag";

            if (!suche.trim().isEmpty()) {
                where = " AND p.text like :search ";
                htmlbeschreibung += "Suchbegriff: '" + suche + "'<br/>";
            }

            if (tag != null) {
                // Suchausdruck vorbereiten.
                where += " AND t = :tag ";
                htmlbeschreibung += "Markierung: '" + tag.getBezeichnung() + "'<br/>";
            }

//            String sql = "" +
//                    " SELECT b.nachname, b.vorname, b.geschlecht, b.bwkennung, tb.Text, tb.PIT, tb.UKennung " +
//                    " FROM Tagesberichte tb " +
//                    " INNER JOIN Bewohner b ON tb.BWKennung = b.BWKennung " +
//                    " WHERE Date(tb.PIT) >= DATE_ADD(now(), INTERVAL ? MONTH) " +
//                    " AND " + where +
//                    " b.AdminOnly <> 2 " +
//                    " ORDER BY b.BWKennung, tb.PIT ";

            try {
                Query query = em.createQuery(jpql + where + order);
                if (!suche.trim().isEmpty()) {
                    query.setParameter("search", "%" + suche + "%");
                }
                if (tag != null) {
                    query.setParameter("tag", tag);
                }

                query.setParameter("date", SYSCalendar.addField(new Date(), monate * -1, GregorianCalendar.MONTH));
                List<Pflegeberichte> list = query.getResultList();
                html.append("<h" + headertiefe + ">");
                html.append("Suchergebnisse in den Berichten der letzten " + monate + " Monate");
                html.append("</h" + headertiefe + ">");
                html.append(htmlbeschreibung);

                if (!list.isEmpty()) {
                    DateFormat df = DateFormat.getDateTimeInstance();
                    html.append("<table border=\"1\"><tr>" +
                            "<th>BewohnerIn</th><th>Datum/Uhrzeit</th><th>Text</th><th>UKennung</th></tr>");
                    for (Pflegeberichte bericht : list) {
                        html.append("<tr>");

                        html.append("<td>" + BewohnerTools.getBWLabel1(bericht.getBewohner()) + "</td>");
                        html.append("<td>" + df.format(bericht.getPit()) + "</td>");
                        html.append("<td>" + bericht.getText() + "</td>");
                        html.append("<td>" + bericht.getUser().getUKennung() + "</td>");
                        html.append("</tr>");
                    }
                    html.append("</table>");
                } else {
                    html.append("<br/>keine Treffer gefunden...");
                }
            } catch (Exception e) {
                OPDE.fatal(e);
            }
        }

        return html.toString();
    }


    public static String getSozialZeiten(EntityManager em, int headertiefe, Date monat) {
        StringBuilder html = new StringBuilder(1000);
        SimpleDateFormat df = new SimpleDateFormat("MMMM yyyy");

        try {

            Date von = new Date(SYSCalendar.bom(monat).getTime());
            Date bis = new Date(SYSCalendar.eom(monat).getTime());
//            int daysinmonth = SYSCalendar.eom(SYSCalendar.toGC(monat));

            Query query = em.createNamedQuery("Pflegeberichte.findSozialZeiten");
            query.setParameter(1, von);
            query.setParameter(2, bis);
            query.setParameter(3, von);
            query.setParameter(4, bis);

            List list = query.getResultList();
            BigDecimal daysinmonth = new BigDecimal(SYSCalendar.eom(SYSCalendar.toGC(monat)));

            html.append("<h" + headertiefe + ">");
            html.append("Zeiten des Sozialen Dienstes je BewohnerIn");
            html.append("</h" + headertiefe + ">");

            headertiefe++;

            html.append("<h" + headertiefe + ">");
            html.append("Zeitraum: " + df.format(monat));
            html.append("</h" + headertiefe + ">");

            html.append("<table border=\"1\"><tr>" +
                    "<th>BewohnerIn</th><th>Dauer (Minuten)</th><th>Dauer (Stunden)</th><th>Stundenschnitt pro Tag</th><th>PEA (Minuten)</th><th>PEA (Stunden)</th><th>Stundenschnitt pro Tag</th></tr>");
            for (Object object : list) {
                Bewohner bewohner = (Bewohner) ((Object[]) object)[0];
                BigDecimal sdauer = (BigDecimal) ((Object[]) object)[1];
                BigDecimal peadauer = (BigDecimal) ((Object[]) object)[2];


                html.append("<tr>");

                html.append("<td>" + BewohnerTools.getBWLabel1(bewohner) + "</td>");
                html.append("<td>" + sdauer + "</td>");
                html.append("<td>" + sdauer.divide(new BigDecimal(60), 2, BigDecimal.ROUND_HALF_UP) + "</td>");
                html.append("<td>" + sdauer.divide(new BigDecimal(60), 2, BigDecimal.ROUND_HALF_UP).divide(daysinmonth, 2, BigDecimal.ROUND_HALF_UP) + "</td>");
                html.append("<td>" + peadauer + "</td>");
                html.append("<td>" + peadauer.divide(new BigDecimal(60), 2, BigDecimal.ROUND_HALF_UP) + "</td>");
                html.append("<td>" + peadauer.divide(new BigDecimal(60), 2, BigDecimal.ROUND_HALF_UP).divide(daysinmonth, 2, BigDecimal.ROUND_HALF_UP) + "</td>");
                html.append("</tr>");
            }

            html.append("</table>");

            html.append("<p><b>PEA:</b> Personen mit erheblich eingeschränkter Alltagskompetenz (gemäß §87b SGB XI)." +
                    " Der hier errechnete Wert ist der <b>Anteil</b> für die PEA Leistungen, die in den allgemeinen Sozialzeiten" +
                    " mit enthalten sind.</p>");

        } catch (Exception e) {
            OPDE.fatal(e);
        }

        return html.toString();
    }

}
