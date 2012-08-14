package entity.prescription;

import entity.info.Resident;
import entity.planung.DFN;
import entity.system.SYSPropsTools;
import op.OPDE;
import op.tools.Pair;
import op.tools.SYSCalendar;
import op.tools.SYSConst;
import op.tools.SYSTools;
import org.joda.time.*;
import org.joda.time.format.DateTimeFormat;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: tloehr
 * Date: 01.12.11
 * Time: 15:49
 * To change this template use File | Settings | File Templates.
 */
public class BHPTools {

    public static final byte STATE_OPEN = 0;
    public static final byte STATE_DONE = 1;
    public static final byte STATE_REFUSED = 2;
    public static final byte STATE_REFUSED_DISCARDED = 2;

    public static final byte SHIFT_ALL = -1;
    public static final byte SHIFT_VERY_EARLY = 0;
    public static final byte SHIFT_EARLY = 1;
    public static final byte SHIFT_LATE = 2;
    public static final byte SHIFT_VERY_LATE = 3;

    public static final String[] TIMEIDTEXTLONG = new String[]{"misc.msg.Time.long", "misc.msg.earlyinthemorning.long", "misc.msg.morning.long", "misc.msg.noon.long", "misc.msg.afternoon.long", "misc.msg.evening.long", "misc.msg.lateatnight.long"};
    public static final String[] TIMEIDTEXTSHORT = new String[]{"misc.msg.Time.short", "misc.msg.earlyinthemorning.short", "misc.msg.morning.short", "misc.msg.noon.short", "misc.msg.afternoon.short", "misc.msg.evening.short", "misc.msg.lateatnight.short"};

    public static final byte BYTE_TIMEOFDAY = 0;
    public static final byte BYTE_EARLY_IN_THE_MORNING = 1;
    public static final byte BYTE_MORNING = 2;
    public static final byte BYTE_NOON = 3;
    public static final byte BYTE_AFTERNOON = 4;
    public static final byte BYTE_EVENING = 5;
    public static final byte BYTE_LATE_AT_NIGHT = 6;

    public static final String STRING_TIMEOFDAY = "UZ";
    public static final String STRING_EARLY_IN_THE_MORNING = "FM";
    public static final String STRING_MORNING = "MO";
    public static final String STRING_NOON = "MI";
    public static final String STRING_AFTERNOON = "NM";
    public static final String STRING_EVENING = "AB";
    public static final String STRING_LATE_AT_NIGHT = "NA";


    public static final String[] SOLLZEITTEXT = new String[]{"Uhrzeit", "NachtMo", "Morgens", "Mittags", "Nachmittags", "Abends", "NachtAb"};

    public static long getNumBHPs(Prescriptions prescription) {
        EntityManager em = OPDE.createEM();
        Query query = em.createNamedQuery("BHP.numByNOTStatusAndVerordnung");
        query.setParameter("prescription", prescription);
        query.setParameter("status", STATE_OPEN);
        long num = (Long) query.getSingleResult();
        em.close();
        return num;
    }

    public static Date getMinDatum(Resident bewohner) {
        Date date;
        EntityManager em = OPDE.createEM();
        Query query = em.createQuery("SELECT b FROM BHP b WHERE b.resident = :resident ORDER BY b.bhpid");
        query.setParameter("resident", bewohner);
        query.setMaxResults(1);
        try {
            date = ((BHP) query.getSingleResult()).getSoll();
        } catch (Exception e) {
            date = new Date();
        }
        em.close();
        return date;
    }

    /**
     * Diese Methode erzeugt den Tagesplan für die Behandlungspflegen. Dabei werden alle aktiven Verordnungen geprüft, ermittelt ob sie am betreffenden targetdate auch "dran" sind und dann
     * werden daraus Einträge in der BHP Tabelle erzeugt. Sie teilt sich die Arbeit mit der <code>erzeugen(EntityManager em, List<VerordnungpSchedule> list, Date targetdate, Date zeit)</code> Methode
     *
     * @param em, EntityManager Kontext
     * @return Anzahl der erzeugten BHPs
     */
    public static int generate(EntityManager em) throws Exception {

        String internalClassID = "nursingrecords.bhpimport";
        int numbhp = 0;


        DateMidnight lastbhp = new DateMidnight().minusDays(1);
        if (OPDE.getProps().containsKey("LASTDFNIMPORT")) {
            lastbhp = new DateMidnight(DateTimeFormat.forPattern("yyyy-MM-dd").parseDateTime(OPDE.getProps().getProperty("LASTBHPIMPORT")));
        }

        if (lastbhp.isAfterNow()) {
            throw new IndexOutOfBoundsException("The date of the last import is somewhere in the future. Can't be true.");
        }

        DateMidnight targetdate = null;

        // If (for technical reasons) the lastdfn lies in the past (more than the usual 1 day),
        // then the generation is interated until the current day.
        for (int days = 1; days <= Days.daysBetween(lastbhp.plusDays(1), new DateMidnight()).getDays() + 1; days++) {

            targetdate = lastbhp.plusDays(days);

            Query select = em.createQuery(" " +
                    " SELECT vp FROM PrescriptionSchedule vp " +
                    " JOIN vp.prescription v " +
                    // nur die Verordnungen, die überhaupt gültig sind
                    // das sind die mit Gültigkeit BAW oder Gültigkeit endet irgendwann in der Zukunft.
                    // Das heisst, wenn eine Verordnung heute endet, dann wird sie dennoch eingetragen.
                    // Also alle, die bis EINSCHLIEßLICH heute gültig sind.
                    " WHERE v.situation IS NULL AND v.anDatum <= :andatum AND v.abDatum >= :abdatum " +
                    // und nur diejenigen, deren Referenzdatum nicht in der Zukunft liegt.
                    " AND vp.lDatum <= :ldatum AND v.bewohner.adminonly <> 2 " +
                    " ORDER BY vp.bhppid ");

            // Diese Aufstellung ergibt mindestens die heute gültigen Einträge.
            // Wahrscheinlich jedoch mehr als diese. Anhand des LDatums müssen
            // die wirklichen Treffer nachher genauer ermittelt werden.

            OPDE.important(em, "[BHPImport] " + OPDE.lang.getString("misc.msg.writingto") + ": " + OPDE.getUrl());

            select.setParameter("andatum", new Date(SYSCalendar.startOfDay(targetdate.toDate())));
            select.setParameter("abdatum", new Date(SYSCalendar.endOfDay(targetdate.toDate())));
            select.setParameter("ldatum", new Date(SYSCalendar.endOfDay(targetdate.toDate())));

            List<PrescriptionSchedule> list = select.getResultList();

            numbhp += generate(em, list, targetdate, true);

            OPDE.important(em, "[BHPImport] Durchgeführt. targetdate: " + DateFormat.getDateInstance().format(targetdate.toDate()) + " Anzaghl erzeugter BHPs: " + numbhp);
        }

        SYSPropsTools.storeProp(em, "LASTBHPIMPORT", DateTimeFormat.forPattern("yyyy-MM-dd").print(targetdate));

        return numbhp;
    }


    /**
     * Löscht alle <b>heutigen</b> nicht <b>abgehakten</b> BHPs für eine bestimmte Verordnung <b>ab</b> ab dem aktuellen Zeitpunkt.
     * Es wird die aktuelle Schicht (bzw. Zeit) ermittelt. Bei BHPs,
     * die sich auf eine bestimmte Uhrzeit beziehen, werden nur diejenigen gelöscht, die <b>größer gleich</b> der aktuellen Uhrzeit sind sind.
     *
     * @param em           EntityManager, in dessen Kontext das hier ablaufen soll.
     * @param prescription um die es geht.
     */
    public static void cleanup(EntityManager em, Prescriptions prescription) throws Exception {
        Date now = new Date();

        int sollZeit = SYSCalendar.ermittleZeit(now.getTime());
        Query query = em.createQuery("SELECT b FROM BHP b WHERE b.prescription = :prescription AND b.soll >= :bofday AND b.soll <= :eofday");

        DateMidnight bofday = new DateMidnight();
        DateTime eofday = new DateMidnight().toDateTime().plusDays(1).minusSeconds(1).toDateTime();

        query.setParameter("prescription", prescription);
        query.setParameter("bofday", bofday.toDate());
        query.setParameter("eofday", eofday.toDate());

        List<BHP> bhps = query.getResultList();

        for (BHP bhp : bhps) {
            if (bhp.getSollZeit() > sollZeit || (bhp.getSollZeit() == 0 && SYSCalendar.compareTime(bhp.getSoll(), now) >= 0)) {
                em.remove(bhp);
            }
        }

    }

    /**
     * Hiermit werden alle BHP Einträge erzeugt, die sich aus den Verordnungen in der zugehörigen Liste ergeben. Die Liste wird aber vorher
     * noch darauf geprüft, ob sie auch wirklich an dem besagten targetdate passt. Dabei gilt:
     * <ol>
     * <li>Alles was taeglich angeordnet ist (jeden Tag oder jeden soundsovielten Tag)</li>
     * <li>Alles was woechentlich ist und die Spalte (Attribut) mit dem aktuellen Wochentagsnamen größer null ist.</li>
     * <li>Monatliche Einträge. Aber nur dann, wenn
     * <ol>
     * <li>es der <i>n</i>.te Tag im Monat ist <br/><b>oder</b></li>
     * <li>oder der <i>n</i>.te Wochentag (z.B. Freitag) im Monat ist</li>
     * </ol>
     * </li>
     * </ol>
     * <p/>
     * Diese Methode kann von verschiednenen Seiten aufgerufen werden. Zum einen von der "anderen" erzeugen Methode, die einen vollständigen Tagesplan für
     * alle BWs erzeugt oder von dem Verordnungs Editor, der seinerseits nur eine einzige Verordnung nachtragen möchte. Auf jeden Fall kann die Liste <code>list</code>
     * auch Einträge enthalten, die unpassend sind. Sie dient nur der Vorauswahl und wird innerhalb dieser Methode dann genau geprüft. Sie "pickt" sich also
     * nur die passenden Elemente aus dieser Liste heraus.
     *
     * @param em         EntityManager Kontext
     * @param list       Liste der VerordnungpScheduleen, die ggf. einzutragen sind.
     * @param targetdate gibt an, für welches Datum die Einträge erzeugt werden. In der Regel ist das immer der aktuelle Tag.
     * @param wholeday   true, dann wird für den ganzen Tag erzeugt. false, dann ab der aktuellen Zeit.
     * @return die Anzahl der erzeugten BHPs.
     */
    public static int generate(EntityManager em, List<PrescriptionSchedule> list, DateMidnight targetdate, boolean wholeday) {
        int maxrows = list.size();
        int numbhp = 0;

        long now = System.currentTimeMillis();
        byte aktuelleZeit = SYSCalendar.ermittleZeit(now);

        int row = 0;

        OPDE.debug("MaxRows: " + maxrows);

        for (PrescriptionSchedule pSchedule : list) {

            pSchedule = em.merge(pSchedule);
            em.lock(pSchedule, LockModeType.OPTIMISTIC_FORCE_INCREMENT);
            em.lock(em.merge(pSchedule.getPrescription()), LockModeType.OPTIMISTIC_FORCE_INCREMENT);
            em.lock(pSchedule.getPrescription().getBewohner(), LockModeType.OPTIMISTIC);

            if (!SYSCalendar.isInFuture(pSchedule.getLDatum()) && (pSchedule.isTaeglich() || pSchedule.isPassenderWochentag(targetdate.toDate()) || pSchedule.isPassenderTagImMonat(targetdate.toDate()))) {

                row++;

                SYSTools.printProgBar(row / maxrows * 100);

                OPDE.debug("Generate BHPs Progress: " + ((float) row / maxrows) * 100 + "%");
                OPDE.debug("==========================================");
                OPDE.debug("BHPPID: " + pSchedule.getBhppid());
                OPDE.debug("BWKennung: " + pSchedule.getPrescription().getBewohner().getBWKennung());
                OPDE.debug("VerID: " + pSchedule.getPrescription().getVerid());


                boolean treffer = false;
                DateMidnight ldatum = new DateMidnight(pSchedule.getLDatum());

                OPDE.debug("LDatum: " + DateFormat.getDateTimeInstance().format(pSchedule.getLDatum()));
                OPDE.debug("targetdate: " + DateFormat.getDateTimeInstance().format(targetdate.toDate()));

                // Genaue Ermittlung der Treffer
                // =============================
                if (pSchedule.isTaeglich()) {
                    OPDE.debug("Eine tägliche pSchedule");
                    // Dann wird das LDatum solange um die gewünschte Tagesanzahl erhöht, bis
                    // der targetdate getroffen wurde oder überschritten ist.
                    while (Days.daysBetween(ldatum, targetdate).getDays() > 0) {
                        OPDE.debug("ldatum liegt vor dem targetdate. Addiere tage: " + pSchedule.getTaeglich());
                        ldatum = ldatum.plusDays(pSchedule.getTaeglich());
                    }
                    // Mich interssiert nur der Treffer, also die Punktlandung auf dem targetdate
                    treffer = Days.daysBetween(ldatum, targetdate).getDays() == 0;
                } else if (pSchedule.isWoechentlich()) {
                    OPDE.debug("Eine wöchentliche pSchedule");
                    while (Weeks.weeksBetween(ldatum, targetdate).getWeeks() > 0) {
                        OPDE.debug("ldatum liegt vor dem targetdate. Addiere Wochen: " + pSchedule.getWoechentlich());
                        ldatum = ldatum.plusWeeks(pSchedule.getWoechentlich());
                    }
                    // Ein Treffer ist es dann, wenn das Referenzdatum gleich dem targetdate ist ODER es zumindest in der selben Kalenderwoche liegt.
                    // Da bei der Vorauswahl durch die Datenbank nur passende Wochentage überhaupt zugelassen wurden, muss das somit der richtige sein.
                    treffer = Weeks.weeksBetween(ldatum, targetdate).getWeeks() == 0;
                } else if (pSchedule.isMonatlich()) {
                    OPDE.debug("Eine monatliche pSchedule");
                    while (Months.monthsBetween(ldatum, targetdate).getMonths() > 0) {
                        OPDE.debug("ldatum liegt vor dem targetdate. Addiere Monate: " + pSchedule.getMonatlich());
                        ldatum = ldatum.plusMonths(pSchedule.getMonatlich());
                    }
                    // Ein Treffer ist es dann, wenn das Referenzdatum gleich dem targetdate ist ODER es zumindest im selben Monat desselben Jahres liegt.
                    // Da bei der Vorauswahl durch die Datenbank nur passende Wochentage oder Tage im Monat überhaupt zugelassen wurden, muss das somit der richtige sein.
                    treffer = Months.monthsBetween(ldatum, targetdate).getMonths() == 0;
                }

                OPDE.debug("LDatum jetzt: " + DateFormat.getDateTimeInstance().format(ldatum.toDate()));
                OPDE.debug("Treffer ? : " + Boolean.toString(treffer));

                // Es wird immer erst eine Schicht später eingetragen. Damit man nicht mit bereits
                // abgelaufenen Zeitpunkten arbeitet.
                // Bei ganzerTag=true werden all diese booleans zu true und damit neutralisiert.
                boolean erstAbFM = wholeday || aktuelleZeit == BYTE_EARLY_IN_THE_MORNING;
                boolean erstAbMO = wholeday || erstAbFM || aktuelleZeit == BYTE_MORNING;
                boolean erstAbMI = wholeday || erstAbMO || aktuelleZeit == BYTE_NOON;
                boolean erstAbNM = wholeday || erstAbMI || aktuelleZeit == BYTE_AFTERNOON;
                boolean erstAbAB = wholeday || erstAbNM || aktuelleZeit == BYTE_EVENING;
                boolean erstAbNA = wholeday || erstAbAB || aktuelleZeit == BYTE_LATE_AT_NIGHT;
                boolean uhrzeitOK = wholeday || (pSchedule.getUhrzeit() != null && DateTimeComparator.getTimeOnlyInstance().compare(pSchedule.getUhrzeit(), new DateTime(now)) > 0);

                if (treffer) {
                    if (erstAbFM && pSchedule.getNachtMo().compareTo(BigDecimal.ZERO) > 0) {
                        //OPDE.debug(bhp);
                        OPDE.debug("SYSConst.FM, " + pSchedule.getNachtMo());
                        em.merge(new BHP(pSchedule, targetdate.toDate(), BYTE_EARLY_IN_THE_MORNING, pSchedule.getNachtMo()));
                        numbhp++;
                    }
                    if (erstAbMO && pSchedule.getMorgens().compareTo(BigDecimal.ZERO) > 0) {
                        OPDE.debug("SYSConst.MO, " + pSchedule.getMorgens());
                        em.merge(new BHP(pSchedule, targetdate.toDate(), BYTE_MORNING, pSchedule.getMorgens()));
                        numbhp++;
                    }
                    if (erstAbMI && pSchedule.getMittags().compareTo(BigDecimal.ZERO) > 0) {
                        OPDE.debug("SYSConst.MI, " + pSchedule.getMittags());
                        em.merge(new BHP(pSchedule, targetdate.toDate(), BYTE_NOON, pSchedule.getMittags()));
                        numbhp++;
                    }
                    if (erstAbNM && pSchedule.getNachmittags().compareTo(BigDecimal.ZERO) > 0) {
                        OPDE.debug("SYSConst.NM, " + pSchedule.getNachmittags());
                        em.merge(new BHP(pSchedule, targetdate.toDate(), BYTE_AFTERNOON, pSchedule.getNachmittags()));
                        numbhp++;
                    }
                    if (erstAbAB && pSchedule.getAbends().compareTo(BigDecimal.ZERO) > 0) {
                        OPDE.debug("SYSConst.AB, " + pSchedule.getAbends());
                        em.merge(new BHP(pSchedule, targetdate.toDate(), BYTE_EVENING, pSchedule.getAbends()));
                        numbhp++;
                    }
                    if (erstAbNA && pSchedule.getNachtAb().compareTo(BigDecimal.ZERO) > 0) {
                        OPDE.debug("SYSConst.NA, " + pSchedule.getNachtAb());
                        em.merge(new BHP(pSchedule, targetdate.toDate(), BYTE_LATE_AT_NIGHT, pSchedule.getNachtAb()));
                        numbhp++;
                    }
                    if (uhrzeitOK && pSchedule.getUhrzeit() != null) {
                        DateTime timeofday = new DateTime(pSchedule.getUhrzeit());
                        Period period = new Period(timeofday.getHourOfDay(), timeofday.getMinuteOfHour(), timeofday.getSecondOfMinute(), timeofday.getMillisOfSecond());
                        Date newTargetdate = targetdate.toDateTime().plus(period).toDate();
                        OPDE.debug("SYSConst.UZ, " + pSchedule.getUhrzeitDosis() + ", " + DateFormat.getDateTimeInstance().format(newTargetdate));
                        em.merge(new BHP(pSchedule, newTargetdate, SYSConst.UZ, pSchedule.getUhrzeitDosis()));
                        numbhp++;
                    }

                    // Nun noch das LDatum in der Tabelle DFNpSchedule neu setzen.
                    pSchedule.setLDatum(targetdate.toDate());

                }
            } else {
                OPDE.debug("///////////////////////////////////////////////////////////");
                OPDE.debug("Folgende pSchedule wurde nicht angenommen: " + pSchedule);
            }
        }
        OPDE.debug("Erzeugte BHPs: " + numbhp);
        return numbhp;
    }

    public static ArrayList<BHP> getBHPs(Resident resident, Date date) {
        EntityManager em = OPDE.createEM();
        ArrayList<BHP> listBHP = null;

        try {

            String jpql = " SELECT bhp " +
                    " FROM BHP bhp " +
                    " WHERE bhp.resident = :resident " +
                    " AND bhp.soll >= :von AND bhp.soll <= :bis ";

            Query query = em.createQuery(jpql);

            query.setParameter("resident", resident);
            query.setParameter("von", new DateTime(date).toDateMidnight().toDate());
            query.setParameter("bis", new DateTime(date).toDateMidnight().plusDays(1).toDateTime().minusSeconds(1).toDate());

            listBHP = new ArrayList<BHP>(query.getResultList());
            Collections.sort(listBHP);

        } catch (Exception se) {
            OPDE.fatal(se);
        } finally {
            em.close();
        }
        return listBHP;
    }

}