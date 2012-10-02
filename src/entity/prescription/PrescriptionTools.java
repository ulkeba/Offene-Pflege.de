/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package entity.prescription;

import entity.Homes;
import entity.Station;
import entity.info.Resident;
import entity.info.ResidentTools;
import op.OPDE;
import op.care.prescription.PnlPrescription;
import op.tools.HTMLTools;
import op.tools.SYSConst;
import op.tools.SYSTools;
import org.joda.time.DateTime;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DateFormat;
import java.util.*;

/**
 * @author tloehr
 */
public class PrescriptionTools {

    /**
     * Diese Methode erzeugt einen Stellplan für den aktuellen Tag im HTML Format.
     * Eine Besonderheit bei der Implementierung muss ich hier erläutern.
     * Aufgrund der ungleichen HTML Standards (insbesonders der Druckdarstellung im CSS2.0 und später auch CSS2.1)
     * muss ich hier einen Trick anwenden, damit das auf verschiedenen Browsern halbwegs gleich aussieht.
     * <p/>
     * Daher addiere ich jedes größere Element auf einer Seite (also Header, Tabellen Zeilen) mit dem Wert 1.
     * Nach einer bestimmten Anzahl von Elementen erzwinge ich einen Pagebreak.
     * <p/>
     * Nach einem Pagebreak wird der Name des aktuellen Bewohner nocheinmal wiederholt.
     * <p/>
     * Ein Mac OS Safari druckt mit diesen Werten sehr gut.
     * Beim Firefox sollten die Ränder wie folgt eingestellt werden:
     * <ul>
     * <li>print.print_margin_bottom = 0.3</li>
     * <li>print.print_margin_left = 0.1</li>
     * <li>print.print_margin_right = 0.1</li>
     * <li>print.print_margin_top = 0.3</li>
     * <li>print.print_unwriteable_margin_bottom = 57</li>
     * <li>print.print_unwriteable_margin_left = 25</li>
     * <li>print.print_unwriteable_margin_right = 25</li>
     * <li>print.print_unwriteable_margin_top = 25</li>
     * <li>Drucken des Hintergrundes einschalten</li>
     * <ul>
     *
     * @param homes Die Einrichtung, für die der Stellplan erstellt werden soll. Sortiert nach den Station.
     */
    public static String getDailyPlanAsHTML(Homes homes) {
        EntityManager em = OPDE.createEM();
        String html = "";

        try {
            Query query = em.createNativeQuery("" +
                    " SELECT v.VerID, st.StatID, bhp.BHPPID, best.BestID, vor.VorID, F.FormID, M.MedPID, M.Bezeichnung, Ms.Bezeichnung " +
                    " FROM BHPVerordnung v " +
                    " INNER JOIN Bewohner bw ON v.BWKennung = bw.BWKennung  " +
                    " INNER JOIN Massnahmen Ms ON Ms.MassID = v.MassID " +
                    " INNER JOIN Stationen st ON bw.StatID = st.StatID  " +
                    " LEFT OUTER JOIN MPDarreichung D ON v.DafID = D.DafID " +
                    " LEFT OUTER JOIN BHPPlanung bhp ON bhp.VerID = v.VerID " +
                    " LEFT OUTER JOIN MProdukte M ON M.MedPID = D.MedPID " +
                    " LEFT OUTER JOIN MPFormen F ON D.FormID = F.FormID " +
                    " LEFT OUTER JOIN ( " +
                    "      SELECT DISTINCT M.VorID, M.BWKennung, B.DafID FROM MPVorrat M  " +
                    "      INNER JOIN MPBestand B ON M.VorID = B.VorID " +
                    "      WHERE M.Bis = '9999-12-31 23:59:59' " +
                    " ) vorr ON vorr.DafID = v.DafID AND vorr.BWKennung = v.BWKennung" +
                    " LEFT OUTER JOIN MPVorrat vor ON vor.VorID = vorr.VorID" +
                    " LEFT OUTER JOIN MPBestand best ON best.VorID = vor.VorID" +
                    " WHERE v.AnDatum < now() AND v.AbDatum > now() AND v.SitID IS NULL AND (v.DafID IS NOT NULL OR v.Stellplan IS TRUE) " +
                    " AND st.EKennung = ? AND ((best.Aus = '9999-12-31 23:59:59' AND best.Anbruch < '9999-12-31 23:59:59') OR (v.DafID IS NULL)) " +
                    " ORDER BY st.statid, CONCAT(bw.nachname,bw.vorname), bw.BWKennung, v.DafID IS NOT NULL, F.Stellplan, CONCAT( M.Bezeichnung, Ms.Bezeichnung)");
            query.setParameter(1, homes.getEKennung());
            html = getDailyPlan(query.getResultList());

        } catch (Exception e) {
            OPDE.fatal(e);
        }
        return html;
    }

//    /**
//     * Erzeugt eine Liste mit EntityBeans und Salden. Diese Liste enthält die zur Zeit verordnete Bedarfsverordnungen.
//     * Die Liste enthält ein Objekt Array mit dem folgenden Aufbau:
//     * <ol>
//     * <li></li>
//     * </ol>
//     *
//     * @param bewohner
//     * @return Liste mit allen Bedarfsverordnungen. <code>null</code>, wenn nichts da war oder bei Fehler.
//     */
//    public static List getBedarfsliste(Resident bewohner) {
//        String sql = " SELECT v.VerID, s.SitID, p.BHPPID, vor.Saldo, bisher.tagesdosis, d.DafID, bestand.APV, bestand.Summe, bestand.BestID " +
//                " FROM BHPVerordnung v " +
//                " INNER JOIN Situationen s ON v.SitID = s.SitID " +
//                " INNER JOIN BHPPlanung p ON v.VerID = p.VerID" +
//                " LEFT OUTER JOIN MPDarreichung d ON v.DafID = d.DafID " +
//                // Dieser Konstrukt bestimmt die Vorräte für einen Bewohner
//                // Dabei wird berücksichtigt, dass ein Vorrat unterschiedliche Hersteller umfassen
//                // kann. Dies wird durch den mehrfach join erreicht. Dadurch stehen die verschiedenen
//                // DafIDs der unterschiedlichen Produkte im selben Vorrat jeweils in verschiedenen Zeilen.
//                // Durch den LEFT OUTER JOIN pickt sich die Datenbank die richtigen Paare heraus.
//                " LEFT OUTER JOIN " +
//                "      ( " +
//                "        SELECT DISTINCT a.VorID, b.DafID, a.saldo FROM ( " +
//                "           SELECT best.VorID, best.DafID, sum(buch.Menge) saldo FROM MPBestand best " +
//                "           INNER JOIN MPBuchung buch ON buch.BestID = best.BestID " +
//                "           INNER JOIN MPVorrat vor1 ON best.VorID = vor1.VorID" +
//                "           WHERE vor1.BWKennung=? AND vor1.Bis = '9999-12-31 23:59:59'" +
//                "           GROUP BY VorID" +
//                "           ) a  " +
//                "        INNER JOIN (" +
//                "           SELECT best.VorID, best.DafID FROM MPBestand best " +
//                "           ) b ON a.VorID = b.VorID " +
//                "      ) vor ON vor.DafID = v.DafID " +
//                // Hier wird berechnet, wieviel von der Tagesdosis der Bewohner heute schon bekommen hat.
//                " LEFT OUTER JOIN" +
//                "      (" +
//                "        SELECT b3.VerID, sum(b1.dosis) tagesdosis " +
//                "        FROM BHP b1" +
//                "        INNER JOIN BHPPlanung b2 ON b1.BHPPID = b2.BHPPID" +
//                "        INNER JOIN BHPVerordnung b3 ON b3.VerID = b2.VerID" +
//                "        WHERE b3.BWKennung=? AND b3.AbDatum = '9999-12-31 23:59:59'" +
//                "        AND DATE(b1.Ist) = Date(now()) AND b1.Status = " + BHPTools.STATE_DONE +
//                "        GROUP BY b3.VerID" +
//                "      ) bisher ON bisher.VerID = v.VerID" +
//                // Hier kommen jetzt die Bestände im Anbruch dabei. Die Namen der Medikamente könnten ja vom
//                // ursprünglich verordneten abweichen.
//                " LEFT OUTER JOIN( " +
//                "        SELECT best1.NextBest, best1.VorID, best1.BestID, best1.DafID, best1.APV, SUM(buch1.Menge) summe " +
//                "        FROM MPBestand best1 " +
//                "        INNER JOIN MPBuchung buch1 ON buch1.BestID = best1.BestID " +
//                "        WHERE best1.Aus = '9999-12-31 23:59:59' AND best1.Anbruch < now() " +
//                "        GROUP BY best1.BestID" +
//                "      ) bestand ON bestand.VorID = vor.VorID " +
//                " LEFT OUTER JOIN MPDarreichung D1 ON bestand.DafID = D1.DafID " +
//                " LEFT OUTER JOIN MProdukte M1 ON M1.MedPID = D1.MedPID " +
//                " WHERE v.BWKennung = ? AND v.AbDatum = '9999-12-31 23:59:59' " +
//                " ORDER BY s.Text";
//        EntityManager em = OPDE.createEM();
//
//        Query query = em.createNativeQuery(sql);
//        query.setParameter(1, bewohner.getRID());
//        query.setParameter(2, bewohner.getRID());
//        query.setParameter(3, bewohner.getRID());
//        List<Object[]> listeRohfassung = query.getResultList();
//        ArrayList<Object[]> listeBedarf = null;
//
//        if (!listeRohfassung.isEmpty()) {
//            listeBedarf = new ArrayList<Object[]>(listeRohfassung.size());
//
//            for (Object[] rohdaten : listeRohfassung) {
//                Prescription verordnung = em.find(Prescription.class, ((BigInteger) rohdaten[0]).longValue());
//                Situations situation = em.find(Situations.class, ((BigInteger) rohdaten[1]).longValue());
//                PrescriptionSchedule planung = em.find(PrescriptionSchedule.class, ((BigInteger) rohdaten[2]).longValue());
//                BigDecimal vorratSaldo = (BigDecimal) rohdaten[3];
//                BigDecimal tagesdosisBisher = (BigDecimal) rohdaten[4];
//                TradeForm darreichung = rohdaten[5] == null ? null : em.find(TradeForm.class, ((BigInteger) rohdaten[5]).longValue());
//                BigDecimal apv = (BigDecimal) rohdaten[6];
//                BigDecimal bestandSumme = (BigDecimal) rohdaten[7];
//                MedStock bestand = rohdaten[8] == null ? null : em.find(MedStock.class, ((BigInteger) rohdaten[8]).longValue());
//
//                listeBedarf.add(new Object[]{verordnung, situation, planung, vorratSaldo, tagesdosisBisher, darreichung, apv, bestandSumme, bestand});
//            }
//        }
//
//        em.close();
//        return listeBedarf;
//    }
//
//
//    public static boolean hasBedarf(Resident resident) {
//        EntityManager em = OPDE.createEM();
//        Query query = em.createQuery("SELECT COUNT(v) FROM Prescription v WHERE v.resident = :resident AND v.to >= :now AND v.situation IS NOT NULL ");
//        query.setParameter("resident", resident);
//        query.setParameter("now", new Date());
//        return ((Long) query.getSingleResult()).longValue() > 0;
//    }

    private static String getDailyPlan(List data) {

        int STELLPLAN_PAGEBREAK_AFTER_ELEMENT_NO = Integer.parseInt(OPDE.getProps().getProperty("stellplan_pagebreak_after_element_no"));

        int elementNumber = 1;
        boolean pagebreak = false;

        String header = "Stellplan für den " + DateFormat.getDateInstance().format(new Date());

        String html = "<html>"
                + "<head>"
                + "<title>" + header + "</title>"
                + OPDE.getCSS()
                + HTMLTools.JSCRIPT_PRINT
                + "</head>"
                + "<body>";

        String bwkennung = "";
        long statid = 0;

        Iterator it = data.iterator();

        EntityManager em = OPDE.createEM();

        while (it.hasNext()) {

            Object[] objects = (Object[]) it.next();

            Prescription verordnung = em.find(Prescription.class, ((BigInteger) objects[0]).longValue());
            Station station = em.find(Station.class, ((BigInteger) objects[1]).longValue());
            PrescriptionSchedule planung = em.find(PrescriptionSchedule.class, ((BigInteger) objects[2]).longValue());

            BigInteger bestid = (BigInteger) objects[3];
            //Vorrat wäre objects[4]
            BigInteger formid = (BigInteger) objects[5];

            OPDE.debug(verordnung);


            boolean stationsWechsel = statid != station.getStatID();

            // Wenn der Plan für eine ganze Einrichtung gedruckt wird, dann beginnt eine
            // neue Station immer auf einer neuen Seite.
            if (stationsWechsel) {
                elementNumber = 1;
                // Beim ersten Mal nur ein H1 Header. Sonst mit Seitenwechsel.
                if (statid == 0) {
                    html += "<h1 align=\"center\" id=\"fonth1\">";
                } else {
                    html += "</table>";
                    html += "<h1 align=\"center\" id=\"fonth1\" style=\"page-break-before:always\">";
                }
                html += header + " (" + station.getBezeichnung() + ")" + "</h1>";
                html += "<div align=\"center\" id=\"fontsmall\">Stellpläne <u>nur einen Tag</u> lang benutzen! Danach <u>müssen sie vernichtet</u> werden.</div>";
                statid = station.getStatID();
            }


            // Alle Formen, die nicht abzählbar sind, werden grau hinterlegt. Also Tropfen, Spritzen etc.
            boolean grau = false;
            if (formid != null) {
                DosageForm form = em.find(DosageForm.class, formid.longValue());
                grau = form.getDailyPlan() > 0;
            }

            // Wenn der Bewohnername sich in der Liste ändert, muss
            // einmal die Überschrift drüber gesetzt werden.
            boolean bewohnerWechsel = !bwkennung.equalsIgnoreCase(verordnung.getResident().getRID());

            if (pagebreak || stationsWechsel || bewohnerWechsel) {
                // Falls zufällig ein weiterer Header (der 2 Elemente hoch ist) einen Pagebreak auslösen WÜRDE
                // müssen wir hier schonmal vorsorglich den Seitenumbruch machen.
                // 2 Zeilen rechne ich nochdrauf, damit die Tabelle mindestens 2 Zeilen hat, bevor der Seitenumbruch kommt.
                // Das kann dann passieren, wenn dieser if Konstrukt aufgrund eines BW Wechsels durchlaufen wird.
                pagebreak = (elementNumber + 2 + 2) > STELLPLAN_PAGEBREAK_AFTER_ELEMENT_NO;

                // Außer beim ersten mal und beim Pagebreak, muss dabei die vorherige Tabelle abgeschlossen werden.
                if (pagebreak || !bwkennung.equals("")) {
                    html += "</table>";
                }

                bwkennung = verordnung.getResident().getRID();

                html += "<h2 id=\"fonth2\" " +
                        (pagebreak ? "style=\"page-break-before:always\">" : ">") +
                        ((pagebreak && !bewohnerWechsel) ? "<i>(fortgesetzt)</i> " : "")
                        + ResidentTools.getLabelText(verordnung.getResident())
                        + "</h2>";
                html += "<table id=\"fonttext\" border=\"1\" cellspacing=\"0\"><tr>"
                        + "<th>Präparat / Massnahme</th><th>FM</th><th>MO</th><th>MI</th><th>NM</th><th>AB</th><th>NA</th><th>Bemerkungen</th></tr>";
                elementNumber += 2;

                if (pagebreak) {
                    elementNumber = 1;
                    pagebreak = false;
                }
            }


            html += "<tr " + (grau ? "id=\"fonttextgrau\">" : ">");
            html += "<td width=\"300\" >" + (verordnung.hasMed() ? "<b>" + TradeFormTools.toPrettyString(verordnung.getTradeForm()) + "</b>" : verordnung.getIntervention().getBezeichnung());
            html += (bestid != null ? "<br/><i>Bestand im Anbruch Nr.: " + bestid + "</i>" : "") + "</td>";
            html += "<td width=\"25\" align=\"center\">" + HTMLTools.printDouble(planung.getNachtMo()) + "</td>";
            html += "<td width=\"25\" align=\"center\">" + HTMLTools.printDouble(planung.getMorgens()) + "</td>";
            html += "<td width=\"25\" align=\"center\">" + HTMLTools.printDouble(planung.getMittags()) + "</td>";
            html += "<td width=\"25\" align=\"center\">" + HTMLTools.printDouble(planung.getNachmittags()) + "</td>";
            html += "<td width=\"25\" align=\"center\">" + HTMLTools.printDouble(planung.getAbends()) + "</td>";
            html += "<td width=\"25\" align=\"center\">" + HTMLTools.printDouble(planung.getNachtAb()) + "</td>";
            html += "<td width=\"300\" >" + PrescriptionScheduleTools.getHinweis(planung, false) + "</td>";
            html += "</tr>";
            elementNumber += 1;

            pagebreak = elementNumber > STELLPLAN_PAGEBREAK_AFTER_ELEMENT_NO;
        }

        em.close();

        html += "</table>"
                + "</body>";


        return html;
    }

    public static String getShortDescription(Prescription prescription) {
        String result = "<font size=+1>";// = SYSConst.html_fontface;

        result += prescription.isDiscontinued() ? "<s>" : "";

        if (!prescription.hasMed()) {
            result += prescription.getIntervention().getBezeichnung();
        } else {

            MedInventory inventory = TradeFormTools.getInventory4TradeForm(prescription.getResident(), prescription.getTradeForm());
            MedStock stockInUse = MedStockTools.getStockInUse(inventory);

            if (stockInUse != null) {
                result += "<b>" + prescription.getTradeForm().getMedProduct().getBezeichnung()
                        + (stockInUse.getTradeForm().getSubtext().isEmpty() ? "" : " " + stockInUse.getTradeForm().getSubtext()) + "</b>" +
                        (stockInUse.getTradeForm().getDosageForm().getPreparation().isEmpty() ? "" : " " + stockInUse.getTradeForm().getDosageForm().getPreparation()) + " " +
                        (prescription.getTradeForm().getDosageForm().getUsageTex().isEmpty() ? SYSConst.UNITS[prescription.getTradeForm().getDosageForm().getUsageUnit()] : prescription.getTradeForm().getDosageForm().getUsageTex());

            } else {
                result += "<b>" + prescription.getTradeForm().getMedProduct().getBezeichnung()
                        + (prescription.getTradeForm().getSubtext().isEmpty() ? "" : " " + prescription.getTradeForm().getSubtext()) + "</b>" +
                        (prescription.getTradeForm().getDosageForm().getPreparation().isEmpty() ? "" : " " + prescription.getTradeForm().getDosageForm().getPreparation()) + " " +
                        (prescription.getTradeForm().getDosageForm().getUsageTex().isEmpty() ? SYSConst.UNITS[prescription.getTradeForm().getDosageForm().getUsageUnit()] : prescription.getTradeForm().getDosageForm().getUsageTex());
            }
        }

        result += "</font>";

        if (prescription.isOnDemand()) {
            result += "<br/><b><u>" + OPDE.lang.getString("misc.msg.ondemand") + ":</u> " + prescription.getSituation().getText() + "</b>";
        }

        result += prescription.isDiscontinued() ? "</s>" : "";

        return result;
    }

    public static String getPrescriptionAsText2(Prescription verordnung) {
        String result = "<div id=\"fonttext\">";// = SYSConst.html_fontface;

        if (verordnung.isDiscontinued()) {
            result += "<s>"; // Abgesetzte
        }
        if (!verordnung.hasMed()) {
            result += verordnung.getIntervention().getBezeichnung();
        } else {

            MedInventory inventory = TradeFormTools.getInventory4TradeForm(verordnung.getResident(), verordnung.getTradeForm());
            MedStock stockInUse = MedStockTools.getStockInUse(inventory);

            if (stockInUse != null) {
                // If the current prescription defers from the original one (different provider of the medication as in the beginning)
                if (!stockInUse.getTradeForm().equals(verordnung.getTradeForm())) {
                    result += "<b>" + stockInUse.getTradeForm().getMedProduct().getBezeichnung() +
                            (stockInUse.getTradeForm().getSubtext().isEmpty() ? "" : " " + stockInUse.getTradeForm().getSubtext()) + "</b>" +
                            (stockInUse.getTradeForm().getDosageForm().getPreparation().isEmpty() ? "" : " " + stockInUse.getTradeForm().getDosageForm().getPreparation()) + " " +
                            (stockInUse.getTradeForm().getDosageForm().getUsageTex().isEmpty() ? SYSConst.UNITS[stockInUse.getTradeForm().getDosageForm().getUsageUnit()] : stockInUse.getTradeForm().getDosageForm().getUsageTex());
                    result += " <i>(" + OPDE.lang.getString(PnlPrescription.internalClassID + ".originalprescription") + ": " + verordnung.getTradeForm().getMedProduct().getBezeichnung();
                    result += (stockInUse.getTradeForm().getSubtext().isEmpty() ? "" : " " + stockInUse.getTradeForm().getSubtext()) + ")</i>";
                } else {
                    // No, the resident still gets the orginal stuff
                    result += "<b>" + verordnung.getTradeForm().getMedProduct().getBezeichnung()
                            + (stockInUse.getTradeForm().getSubtext().isEmpty() ? "" : " " + stockInUse.getTradeForm().getSubtext()) + "</b>" +
                            (stockInUse.getTradeForm().getDosageForm().getPreparation().isEmpty() ? "" : " " + stockInUse.getTradeForm().getDosageForm().getPreparation()) + " " +
                            (verordnung.getTradeForm().getDosageForm().getUsageTex().isEmpty() ? SYSConst.UNITS[verordnung.getTradeForm().getDosageForm().getUsageUnit()] : verordnung.getTradeForm().getDosageForm().getUsageTex());
                }
            } else {
                result += "<b>" + verordnung.getTradeForm().getMedProduct().getBezeichnung()
                        + (verordnung.getTradeForm().getSubtext().isEmpty() ? "" : " " + verordnung.getTradeForm().getSubtext()) + "</b>" +
                        (verordnung.getTradeForm().getDosageForm().getPreparation().isEmpty() ? "" : " " + verordnung.getTradeForm().getDosageForm().getPreparation()) + " " +
                        (verordnung.getTradeForm().getDosageForm().getUsageTex().isEmpty() ? SYSConst.UNITS[verordnung.getTradeForm().getDosageForm().getUsageUnit()] : verordnung.getTradeForm().getDosageForm().getUsageTex());
            }


        }
        if (verordnung.isDiscontinued()) {
            result += "</s>"; // Abgesetzte
        }

        return result + "</div>";
    }

    public static String getPrescriptionAsShortText(Prescription verordnung) {
        String result = "";

        if (verordnung.isDiscontinued()) {
            result += "<s>"; // Abgesetzte
        }
        if (!verordnung.hasMed()) {
            result += verordnung.getIntervention().getBezeichnung();
        } else {


            result += verordnung.getTradeForm().getMedProduct().getBezeichnung()
                    + (verordnung.getTradeForm().getSubtext().isEmpty() ? "" : " " + verordnung.getTradeForm().getSubtext());


        }
        if (verordnung.isDiscontinued()) {
            result += "</s>"; // Abgesetzte
        }

        return result;
    }

    public static String getRemark(Prescription verordnung) {
        String result = "<div id=\"fonttext\">";

        if (verordnung.isOnDemand()) {
            result += "<b><u>" + OPDE.lang.getString("misc.msg.ondemand") + ":</u> <font color=\"blue\">" + verordnung.getSituation().getText() + "</font></b>";
        }
        if (!verordnung.getText().isEmpty()) {
            result += result.isEmpty() ? "" : "<br/>";
            result += "<b><u>" + OPDE.lang.getString("misc.msg.comment") + ":</u> </b>" + verordnung.getText();
        }
        return result + "</div>";
    }

    public static String getON(Prescription verordnung) {
        String result = "<div id=\"fonttext\">";
        String datum = DateFormat.getDateInstance().format(verordnung.getFrom());

        result += "<font color=\"green\">" + datum + "; ";
        if (verordnung.getHospitalON() != null) {
            result += verordnung.getHospitalON().getName();
        }
        if (verordnung.getDocON() != null) {
            if (verordnung.getHospitalON() != null) {
                result += " <i>" + OPDE.lang.getString("misc.msg.confirmedby") + ":</i> ";
            }
            result += verordnung.getDocON().getAnrede() + " " + SYSTools.anonymizeName(verordnung.getDocON().getName(), SYSTools.INDEX_NACHNAME);
        }
        result += "; " + verordnung.getUserON().getFullname() + "</font>";

        return result + "</div>";
    }

    public static String getOFF(Prescription verordnung) {
        String result = "<div id=\"fonttext\">";

        if (verordnung.isLimited()) {
            String datum = DateFormat.getDateInstance().format(verordnung.getTo());

            result += "<font color=\"" + (verordnung.isDiscontinued() ? "red" : "lime") + "\">" + datum + "; ";

            result += verordnung.getHospitalOFF() != null ? verordnung.getHospitalOFF().getName() : "";

            if (verordnung.getDocOFF() != null) {
                if (verordnung.getHospitalOFF() != null) {
                    result += " <i>" + OPDE.lang.getString("misc.msg.confirmedby") + ":</i> ";
                }
                result += verordnung.getDocOFF().getAnrede() + " " + SYSTools.anonymizeName(verordnung.getDocOFF().getName(), SYSTools.INDEX_NACHNAME);
            }
            result += "; " + verordnung.getUserOFF().getFullname() + "</font>";

        }
        return result + "</div>";
    }

    public static String getDose(Prescription prescription) {
        return getDose(prescription, false);
    }

    public static String getDose(Prescription prescription, boolean showInventory) {
//        long timestart = System.currentTimeMillis();
        String result = "";
        if (prescription.getPrescriptionSchedule().size() > 1) {
            Collections.sort(prescription.getPrescriptionSchedule());
        }
        Iterator<PrescriptionSchedule> planungen = prescription.getPrescriptionSchedule().iterator();


        if (planungen.hasNext()) {
            PrescriptionSchedule vorherigePlanung = null;
            PrescriptionSchedule planung = null;
            while (planungen.hasNext()) {
                planung = planungen.next();
                result += PrescriptionScheduleTools.getDoseAsHTML(planung, vorherigePlanung, false);
                vorherigePlanung = planung;
            }
            if (PrescriptionScheduleTools.getTerminStatus(planung) != PrescriptionScheduleTools.MAXDOSIS) {
                // Wenn die letzte Planung eine Tabelle benötigte (das tut sie dann, wenn
                // es keine Bedarfsverordnung war), dann müssen wir die Tabelle hier noch
                // schließen.
                result += "</table>";
            }
        } else {
            result += "<i>Noch keine Dosierung / Anwendungsinformationen verfügbar</i><br/>";
        }

        if (showInventory && prescription.hasMed()) {
            MedInventory inventory = TradeFormTools.getInventory4TradeForm(prescription.getResident(), prescription.getTradeForm());
            MedStock stockInUse = MedStockTools.getStockInUse(inventory);

            if (prescription.isTillEndOfPackage()) {
                result += "nur bis Packungs Ende<br/>";
            }
            if (!prescription.isDiscontinued()) {
                if (stockInUse != null) {
                    EntityManager em = OPDE.createEM();

                    BigDecimal invSum = null;
                    BigDecimal stockSum = null;
                    try {
                        invSum = MedInventoryTools.getSum(em, inventory);
                        stockSum = MedStockTools.getSum(em, stockInUse);
                    } catch (Exception e) {
                        OPDE.fatal(e);
                    } finally {
                        em.close();
                    }


                    if (invSum != null && invSum.compareTo(BigDecimal.ZERO) > 0) {
                        result += "<b><u>Vorrat:</u> <font color=\"green\">" + invSum.setScale(2, BigDecimal.ROUND_UP) + " " +
                                SYSConst.UNITS[stockInUse.getTradeForm().getDosageForm().getPackUnit()] +
                                "</font></b>";
                        if (!stockInUse.getTradeForm().getDosageForm().isAPV1()) {

                            BigDecimal anwmenge = invSum.multiply(stockInUse.getAPV());

                            result += " <i>entspricht " + anwmenge.setScale(2, BigDecimal.ROUND_UP) + " " +
                                    DosageFormTools.getUsageText(stockInUse.getTradeForm().getDosageForm());
                            result += " (bei einem APV von " + stockInUse.getAPV().setScale(2, BigDecimal.ROUND_UP) + " zu 1)";
                            result += "</i>";
                        }

                        result += "<br/>Bestand im Anbruch Nr.: <b><font color=\"green\">" + stockInUse.getID() + "</font></b>";

                        if (invSum.compareTo(stockSum) != 0) {
                            result += "<br/>Restmenge im Anbruch: <b><font color=\"green\">" + stockSum.setScale(2, BigDecimal.ROUND_UP) + " " +
                                    SYSConst.UNITS[stockInUse.getTradeForm().getDosageForm().getPackUnit()] + "</font></b>";
                            if (!stockInUse.getTradeForm().getDosageForm().isAPV1()) {
                                BigDecimal usage = stockSum.multiply(stockInUse.getAPV());

                                result += " <i>entspricht " + usage.setScale(2, BigDecimal.ROUND_UP) + " " +
                                        DosageFormTools.getUsageText(stockInUse.getTradeForm().getDosageForm()) + "</i>";
                            }
                        }

                    } else {
                        result += "<b><font color=\"red\">Der Vorrat an diesem Medikament ist <u>leer</u>.</font></b>";
                    }
                } else {
                    if (inventory == null) {
                        result += "<b><font color=\"red\">Es gibt bisher keinen Vorrat für dieses Medikament.</font></b>";
                    } else {
                        if (MedInventoryTools.getNextToOpen(inventory) != null) {
                            result += "<br/><b><font color=\"red\">Kein Bestand im Anbruch. Vergabe nicht möglich.</font></b>";
                        } else {
                            result += "<br/><b><font color=\"red\">Keine Bestände mehr im Vorrat vorhanden. Vergabe nicht möglich.</font></b>";
                        }

                    }
                }
            }

        }


//        long timeend = System.currentTimeMillis();

//        OPDE.debug("time end: " + (timeend - timestart) + " millis");

        return result;
    }

    /**
     * Dieser Query ordnet Verordnungen den Vorräten zu. Dazu ist ein kleiner Trick nötig. Denn über die Zeit können verschiedene Vorräte mit verschiedenen
     * Darreichungen für dieselbe Verordnung verwendet werden. Der Trick ist der Join über zwei Spalten in der Zeile mit "MPBestand"
     */
    public static List<Prescription> getPrescriptionsByInventory(MedInventory inventory) {
        EntityManager em = OPDE.createEM();

        List<BigInteger> list = null;
        List<Prescription> result = null;
        Query query = em.createNativeQuery(" SELECT DISTINCT ver.VerID FROM BHPVerordnung ver " +
                " INNER JOIN MPVorrat v ON v.BWKennung = ver.BWKennung " + // Verbindung über Bewohner
                " INNER JOIN MPBestand b ON ver.DafID = b.DafID AND v.VorID = b.VorID " + // Verbindung über Bestand zur Darreichung UND dem Vorrat
                " WHERE b.VorID=? AND ver.AbDatum > now() ");
        query.setParameter(1, inventory.getID());
        list = query.getResultList();

        if (!list.isEmpty()) {
            result = new ArrayList<Prescription>(list.size());
            for (BigInteger verid : list) {
                result.add(em.find(Prescription.class, verid.longValue()));
            }
        }
        em.close();

        return result;
    }

    public static ArrayList<Prescription> getAll(Resident resident) {
        EntityManager em = OPDE.createEM();

        ArrayList<Prescription> result = null;
        Query query = em.createQuery(" SELECT p FROM Prescription p WHERE p.resident = :resident ");
        query.setParameter("resident", resident);
        result = new ArrayList<Prescription>(query.getResultList());

        em.close();

        return result;
    }


    /**
     * Dieser Query ordnet Verordnungen den Vorräten zu. Dazu ist ein kleiner Trick nötig. Denn über die Zeit können verschiedene Vorräte mit verschiedenen
     * Darreichungen für dieselbe Verordnung verwendet werden. Der Trick ist der Join über zwei Spalten in der Zeile mit "MPBestand"
     */
    public static List<Prescription> getOnDemandPrescriptions(Resident resident, Date date) {
        EntityManager em = OPDE.createEM();

//        List<Prescription> list = null;
        Query query = em.createQuery("SELECT p FROM Prescription p WHERE p.resident = :resident AND p.situation IS NOT NULL AND p.from <= :from AND p.to >= :to ORDER BY p.situation.text, p.id");
        query.setParameter("resident", resident);
        query.setParameter("from", new DateTime(date).toDateMidnight().toDate());
        query.setParameter("to", new DateTime(date).toDateMidnight().plusDays(1).toDateTime().minusSeconds(1).toDate());

        List<Prescription> list = query.getResultList();
//        Collections.sort(list);

        em.close();

        return list;
    }

    public static String getPrescriptionAsHTML(Prescription prescription, boolean withheader, boolean withlongheader, boolean withmed) {
        ArrayList<Prescription> single = new ArrayList<Prescription>();
        single.add(prescription);
        return getPrescriptionsAsHTML(single, withheader, withlongheader, withmed, true);
    }

    /**
     * Gibt eine HTML Darstellung der Verordungen zurück, die in dem übergebenen TableModel enthalten sind.
     */
    public static String getPrescriptionsAsHTML(List<Prescription> list, boolean withheader, boolean withlongheader, boolean withmed, boolean withDiscontinued) {
        String result = "";

        if (!list.isEmpty()) {
            Prescription prescription = list.get(0);
            result += withheader ? "<h2 id=\"fonth2\" >" + OPDE.lang.getString("nursingrecords.prescription") + (withlongheader ? " für " + ResidentTools.getLabelText(prescription.getResident()) : "") + "</h2>" : "";

//            if (prescription.getResident().getStation() != null) {
//                result += HomesTools.getAsText(prescription.getResident().getStation().getEinrichtung());
//            }

            result += "<table id=\"fonttext\" border=\"1\" cellspacing=\"0\"><tr>" +
                    "<th >Medikament/Massnahme</th><th >Dosierung / Hinweise</th><th >Angesetzt</th></tr>";

            for (Prescription myprescription : list) {

                if (withDiscontinued || !myprescription.isDiscontinued()) {

                    result += "<tr>";
                    result += "<td valign=\"top\">" + getShortDescription(myprescription) + "</td>";
                    result += "<td valign=\"top\">" + getDose(myprescription, withmed) + "<br/>";
                    result += getRemark(myprescription) + "</td>";
                    result += "<td valign=\"top\">" + getON(myprescription);

                    if (myprescription.isDiscontinued()) {
                        result += getOFF(myprescription);
                    }

                    result += "</td>";
                    result += "</tr>";
                }
            }

            result += "</table>";
        } else {
            result += "<h2  id=\"fonth2\" >" + OPDE.lang.getString("nursingrecords.prescription") + "</h2><i>" + OPDE.lang.getString("misc.msg.currentlynoentry") + "</i>";
        }
        return result;
    }

    /**
     * Ermittelt die Anzahl der Verordnungen, die zu dieser Verordnung gemäß der VerordnungKennung gehören.
     * Verordnung, die über die Zeit mehrfach geändert werden, hängen über die VerordnungsKennung aneinander.
     *
     * @param verordnung
     * @return Anzahl der Verordnungen, die zu dieser gehören.e
     */
    public static int getNumVerodnungenMitGleicherKennung(Prescription verordnung) {
        EntityManager em = OPDE.createEM();
        Query query = em.createNamedQuery("Verordnung.findByVerKennung");
        query.setParameter("verKennung", verordnung.getRelation());
        int num = query.getResultList().size();
        em.close();
        return num;
    }

    public static String toPrettyString(Prescription verordnung) {
        String myPretty = "";

        if (verordnung.hasMed()) {
            myPretty = TradeFormTools.toPrettyString(verordnung.getTradeForm());
        } else {
            myPretty = verordnung.getIntervention().getBezeichnung();
        }

        myPretty += verordnung.isOnDemand() ? " (Nur bei Bedarf: " + verordnung.getSituation().getText() + ")" : "";

        return myPretty;
    }

    public static void absetzen(EntityManager em, Prescription verordnung) throws Exception {
        verordnung = em.merge(verordnung);
        em.lock(verordnung, LockModeType.OPTIMISTIC);
        verordnung.setTo(new Date());
        verordnung.setUserOFF(em.merge(OPDE.getLogin().getUser()));
        BHPTools.cleanup(em, verordnung);
    }

    public static void alleAbsetzen(EntityManager em, Resident resident) throws Exception {
        Query query = em.createQuery("SELECT b FROM Prescription b WHERE b.resident = :resident AND b.to >= :now");
        query.setParameter("resident", resident);
        query.setParameter("now", new Date());
        List<Prescription> verordnungen = query.getResultList();

        for (Prescription verordnung : verordnungen) {
            absetzen(em, verordnung);
        }
    }

}