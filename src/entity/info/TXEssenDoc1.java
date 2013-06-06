package entity.info;

import com.itextpdf.text.Font;
import com.itextpdf.text.pdf.*;
import entity.files.SYSFilesTools;
import entity.nursingprocess.InterventionSchedule;
import entity.nursingprocess.InterventionScheduleTools;
import entity.nursingprocess.InterventionTools;
import entity.prescription.BHP;
import entity.prescription.BHPTools;
import entity.prescription.Prescription;
import entity.prescription.PrescriptionTools;
import op.OPDE;
import op.care.info.PnlBodyScheme;
import op.tools.SYSConst;
import op.tools.SYSTools;
import org.apache.commons.lang.ArrayUtils;

import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: tloehr
 * Date: 03.06.13
 * Time: 17:18
 * To change this template use File | Settings | File Templates.
 */
public class TXEssenDoc1 {

    public static final String SOURCEFILENAME = "essen-1-121029.pdf";
    private final HashMap<ResInfo, Properties> mapInfo2Properties;
    private Resident resident;
    private HashMap<String, String> content;
    //    private ArrayList<ResInfo> listAllInfos;
    private ArrayList<ResInfo> listICD;
    private HashMap<Integer, ResInfo> mapID2Info;

    private final Font pdf_font_small = new Font(Font.FontFamily.HELVETICA, 6);
    private final Font pdf_font_small_bold = new Font(Font.FontFamily.HELVETICA, 6, Font.BOLD);
    private final Font pdf_font_normal_bold = new Font(Font.FontFamily.HELVETICA, 9, Font.BOLD);
    private PdfContentByte over = null;
    private PdfWriter writer = null;
    PdfStamper stamper = null;

    public TXEssenDoc1(Resident resident) {
        this.resident = resident;
        content = new HashMap<String, String>();
//        listAllInfos.addAll(ResInfoTools.getAll(resident));
        listICD = new ArrayList<ResInfo>();
        mapID2Info = new HashMap<Integer, ResInfo>();
        mapInfo2Properties = new HashMap<ResInfo, Properties>();

        for (ResInfo info : ResInfoTools.getAll(resident)) {
            if (!info.isSingleIncident() && !info.isNoConstraints()) {
                mapID2Info.put(info.getResInfoType().getType(), info);
                mapInfo2Properties.put(info, load(info.getProperties()));
            }
            if (info.getResInfoType().getType() == ResInfoTypeTools.TYPE_DIAGNOSIS) {
                listICD.add(info);
            }
        }


        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
            File stamperFile = new File(OPDE.getOPWD() + File.separator + OPDE.SUBDIR_CACHE + File.separator + "TX_" + resident.getRID() + "_" + sdf.format(new Date()) + ".pdf");
            stamper = new PdfStamper(new PdfReader(OPDE.getOPWD() + File.separator + OPDE.SUBDIR_TEMPLATES + File.separator + SOURCEFILENAME), new FileOutputStream(stamperFile));


            createContent4Section1();
            createContent4Section2();
            createContent4Section3();
            createContent4Section4();
            createContent4Section5();
            createContent4Section6();

            fillDataIntoPDF();
            fillWoundsIntoPDF();

            stamper.setFormFlattening(true);

            stamper.close();

            mapInfo2Properties.clear();
            listICD.clear();
            content.clear();

            SYSFilesTools.handleFile(stamperFile, Desktop.Action.OPEN);
        } catch (Exception e) {
            OPDE.fatal(e);
        }

    }

    private void fillDataIntoPDF() throws Exception {
        AcroFields form = stamper.getAcroFields();
        for (String key : content.keySet()) {
            if (!ArrayUtils.contains(PnlBodyScheme.PARTS, key)) { // this is a special case. The bodyparts and the pdfkeys have the same name.
                form.setField(key, content.get(key));
            }
        }

    }

    private void fillWoundsIntoPDF() {

    }


    /**
     * fills the usual stuff like resident name, insurances, dob and the rest on all three pages.
     * filling means, putting pairs into the content HashMap.
     * <p/>
     * Contains also "1 Soziale Aspekte"
     */
    private void createContent4Section1() {
        content.put(RESIDENT_GENDER, resident.getGender() == ResidentTools.MALE ? "1" : "0");
        content.put(RESIDENT_FIRSTNAME, resident.getFirstname());
        content.put(RESIDENT_FIRSTNAME_PAGE2, resident.getFirstname());
        content.put(RESIDENT_NAME, resident.getName());
        content.put(RESIDENT_NAME_PAGE2, resident.getName());
        content.put(RESIDENT_FULLNAME_PAGE3, ResidentTools.getFullName(resident));
        content.put(RESIDENT_DOB, DateFormat.getDateInstance().format(resident.getDOB()));
        content.put(RESIDENT_DOB_PAGE2, DateFormat.getDateInstance().format(resident.getDOB()));
        content.put(RESIDENT_DOB_PAGE3, DateFormat.getDateInstance().format(resident.getDOB()));
        if (resident.isActive()) {
            content.put(RESIDENT_STREET, resident.getStation().getHome().getStreet());
            content.put(RESIDENT_CITY, resident.getStation().getHome().getCity());
            content.put(RESIDENT_ZIP, resident.getStation().getHome().getZIP());
            content.put(RESIDENT_PHONE, resident.getStation().getHome().getTel());
            content.put(RESIDENT_PHONE_PAGE2, resident.getStation().getHome().getTel());
        }
        content.put(TX_DATE, DateFormat.getDateInstance().format(new Date()));
        content.put(TX_DATE_PAGE2, DateFormat.getDateInstance().format(new Date()));
        content.put(TX_DATE_PAGE3, DateFormat.getDateInstance().format(new Date()));
        content.put(TX2_DATE_PAGE3, DateFormat.getDateInstance().format(new Date()));
        content.put(TX_TIME, DateFormat.getTimeInstance(DateFormat.SHORT).format(new Date()));

        content.put(RESIDENT_HINSURANCE, getValue(ResInfoTypeTools.TYPE_HEALTH_INSURANCE, "hiname"));
        content.put(RESIDENT_HINSURANCE_PAGE3, getValue(ResInfoTypeTools.TYPE_HEALTH_INSURANCE, "hiname"));
        content.put(RESIDENT_HINSURANCEID, getValue(ResInfoTypeTools.TYPE_HEALTH_INSURANCE, "personno"));
        content.put(RESIDENT_HINSURANCENO, getValue(ResInfoTypeTools.TYPE_HEALTH_INSURANCE, "insuranceno"));

        content.put(COMMS_MOTHERTONGUE, getValue(ResInfoTypeTools.TYPE_COMMS, "mothertongue"));
        content.put(SOCIAL_RELIGION, getValue(ResInfoTypeTools.TYPE_PERSONALS, "konfession"));

        content.put(LEGAL_SINGLE, setCheckbox(getValue(ResInfoTypeTools.TYPE_PERSONALS, "single")));
        content.put(LEGAL_MINOR, setCheckbox(ResidentTools.isMinor(resident)));

        content.put(LC_GENERAL, setCheckbox(mapID2Info.containsKey(ResInfoTypeTools.TYPE_LEGALCUSTODIANS)));
        content.put(LC_FINANCE, setCheckbox(getValue(ResInfoTypeTools.TYPE_LEGALCUSTODIANS, "finance")));
        content.put(LC_HEALTH, setCheckbox(getValue(ResInfoTypeTools.TYPE_LEGALCUSTODIANS, "health")));
        content.put(LC_CUSTODY, setCheckbox(getValue(ResInfoTypeTools.TYPE_LEGALCUSTODIANS, "confinement")));
        content.put(LC_NAME, getValue(ResInfoTypeTools.TYPE_LEGALCUSTODIANS, "name"));
        content.put(LC_FIRSTNAME, getValue(ResInfoTypeTools.TYPE_LEGALCUSTODIANS, "firstname"));
        content.put(LC_PHONE, getValue(ResInfoTypeTools.TYPE_LEGALCUSTODIANS, "tel"));
        content.put(LC_STREET, getValue(ResInfoTypeTools.TYPE_LEGALCUSTODIANS, "street"));
        content.put(LC_ZIP, getValue(ResInfoTypeTools.TYPE_LEGALCUSTODIANS, "zip"));
        content.put(LC_CITY, getValue(ResInfoTypeTools.TYPE_LEGALCUSTODIANS, "city"));

        content.put(CONFIDANT_NAME, getValue(ResInfoTypeTools.TYPE_CONFIDANTS, "c1name"));
        content.put(CONFIDANT_FIRSTNAME, getValue(ResInfoTypeTools.TYPE_CONFIDANTS, "c1firstname"));
        content.put(CONFIDANT_STREET, getValue(ResInfoTypeTools.TYPE_CONFIDANTS, "c1street"));
        content.put(CONFIDANT_ZIP, getValue(ResInfoTypeTools.TYPE_CONFIDANTS, "c1zip"));
        content.put(CONFIDANT_CITY, getValue(ResInfoTypeTools.TYPE_CONFIDANTS, "c1city"));
        content.put(CONFIDANT_PHONE, getValue(ResInfoTypeTools.TYPE_CONFIDANTS, "c1tel"));
        content.put(SOCIAL_CONFIDANT_CARE, setCheckbox(getValue(ResInfoTypeTools.TYPE_CONFIDANTS, "c1ready2nurse")));

        content.put(SOCIAL_CURRENT_RESTHOME, "1");

        content.put(DATE_REQUESTED_INSURANCE_GRADE, getValue(ResInfoTypeTools.TYPE_NURSING_INSURANCE, "requestdate"));
        content.put(ASSIGNED_INSURANCE_GRADE, getValue(ResInfoTypeTools.TYPE_NURSING_INSURANCE, "result"));

        String grade = getValue(ResInfoTypeTools.TYPE_NURSING_INSURANCE, "grade");
        grade = grade.equalsIgnoreCase("refused") ? "none" : grade; // there is no such thing as refused request in this document.
        content.put(INSURANCE_GRADE, setRadiobutton(grade, new String[]{"none", "assigned", "requested"}));
        content.put(DATE_REQUESTED_INSURANCE_GRADE_PAGE3, setCheckbox(grade.equalsIgnoreCase("requested")));
    }


    private void createContent4Section2() {
        // nothing yet
    }

    private void createContent4Section3() {
        content.put(PERSONAL_CARE_LEVEL, setRadiobutton(getValue(ResInfoTypeTools.TYPE_CARE, "personal.care"), new String[]{"none", "lvl1", "lvl2", "lvl3"}));
        content.put(PERSONAL_CARE_BED, setCheckbox(getValue(ResInfoTypeTools.TYPE_CARE, "personal.care.bed")));
        content.put(PERSONAL_CARE_SHOWER, setCheckbox(getValue(ResInfoTypeTools.TYPE_CARE, "personal.care.shower")));
        content.put(PERSONAL_CARE_BASIN, setCheckbox(getValue(ResInfoTypeTools.TYPE_CARE, "personal.care.basin")));
        content.put(MOUTH_CARE_LEVEL, setRadiobutton(getValue(ResInfoTypeTools.TYPE_MOUTHCARE, "mouth.care"), new String[]{"none", "lvl1", "lvl2", "lvl3"}));
        content.put(MOUTH_CARE_BED, setCheckbox(getValue(ResInfoTypeTools.TYPE_MOUTHCARE, "mouth.care.bed")));
        content.put(MOUTH_CARE_SHOWER, setCheckbox(getValue(ResInfoTypeTools.TYPE_MOUTHCARE, "mouth.care.shower")));
        content.put(MOUTH_CARE_BASIN, setCheckbox(getValue(ResInfoTypeTools.TYPE_MOUTHCARE, "mouth.care.basin")));
        content.put(DENTURE_CARE_LEVEL, setRadiobutton(getValue(ResInfoTypeTools.TYPE_MOUTHCARE, "denture.care"), new String[]{"none", "lvl1", "lvl2", "lvl3"}));
        content.put(DENTURE_CARE_BED, setCheckbox(getValue(ResInfoTypeTools.TYPE_MOUTHCARE, "denture.care.bed")));
        content.put(DENTURE_CARE_SHOWER, setCheckbox(getValue(ResInfoTypeTools.TYPE_MOUTHCARE, "denture.care.shower")));
        content.put(DENTURE_CARE_BASIN, setCheckbox(getValue(ResInfoTypeTools.TYPE_MOUTHCARE, "denture.care.basin")));
        content.put(COMBING_CARE_LEVEL, setRadiobutton(getValue(ResInfoTypeTools.TYPE_CARE, "combing.care"), new String[]{"none", "lvl1", "lvl2", "lvl3"}));
        content.put(COMBING_CARE_BED, setCheckbox(getValue(ResInfoTypeTools.TYPE_CARE, "combing.care.bed")));
        content.put(COMBING_CARE_SHOWER, setCheckbox(getValue(ResInfoTypeTools.TYPE_CARE, "combing.care.shower")));
        content.put(COMBING_CARE_BASIN, setCheckbox(getValue(ResInfoTypeTools.TYPE_CARE, "combing.care.basin")));
        content.put(SHAVE_CARE_LEVEL, setRadiobutton(getValue(ResInfoTypeTools.TYPE_CARE, "shave.care"), new String[]{"none", "lvl1", "lvl2", "lvl3"}));
        content.put(SHAVE_CARE_BED, setCheckbox(getValue(ResInfoTypeTools.TYPE_CARE, "shave.care.bed")));
        content.put(SHAVE_CARE_SHOWER, setCheckbox(getValue(ResInfoTypeTools.TYPE_CARE, "shave.care.shower")));
        content.put(SHAVE_CARE_BASIN, setCheckbox(getValue(ResInfoTypeTools.TYPE_CARE, "shave.care.basin")));
        content.put(DRESSING_CARE_LEVEL, setRadiobutton(getValue(ResInfoTypeTools.TYPE_CARE, "dressing.care"), new String[]{"none", "lvl1", "lvl2", "lvl3"}));
        content.put(DRESSING_CARE_BED, setCheckbox(getValue(ResInfoTypeTools.TYPE_CARE, "dressing.care.bed")));
        content.put(DRESSING_CARE_SHOWER, setCheckbox(getValue(ResInfoTypeTools.TYPE_CARE, "dressing.care.shower")));
        content.put(DRESSING_CARE_BASIN, setCheckbox(getValue(ResInfoTypeTools.TYPE_CARE, "dressing.care.basin")));

        content.put(SKIN_DRY, setCheckbox(getValue(ResInfoTypeTools.TYPE_SKIN, "skin.dry")));
        content.put(SKIN_GREASY, setCheckbox(getValue(ResInfoTypeTools.TYPE_SKIN, "skin.greasy")));
        content.put(SKIN_ITCH, setCheckbox(getValue(ResInfoTypeTools.TYPE_SKIN, "skin.itch")));
        content.put(SKIN_NORMAL, setCheckbox(getValue(ResInfoTypeTools.TYPE_SKIN, "skin.normal")));

        content.put(PREFERRED_CAREPRODUCTS, getValue(ResInfoTypeTools.TYPE_CARE, "preferred.careproducts"));


    }

    private void createContent4Section4() {
        content.put(MOBILITY_GET_UP, setRadiobutton(getValue(ResInfoTypeTools.TYPE_MOBILITY, "stand"), new String[]{"none", "lvl1", "lvl2", "lvl3"}));
        content.put(MOBILITY_AID_GETUP, setCheckbox(getValue(ResInfoTypeTools.TYPE_MOBILITY, "stand.aid")));
        content.put(MOBILITY_WALKING, setRadiobutton(getValue(ResInfoTypeTools.TYPE_MOBILITY, "walk"), new String[]{"none", "lvl1", "lvl2", "lvl3"}));
        content.put(MOBILITY_AID_WALKING, setCheckbox(getValue(ResInfoTypeTools.TYPE_MOBILITY, "walk.aid")));
        content.put(MOBILITY_TRANSFER, setRadiobutton(getValue(ResInfoTypeTools.TYPE_MOBILITY, "transfer"), new String[]{"none", "lvl1", "lvl2", "lvl3"}));
        content.put(MOBILITY_AID_TRANSFER, setCheckbox(getValue(ResInfoTypeTools.TYPE_MOBILITY, "transfer.aid")));
        content.put(MOBILITY_TOILET, setRadiobutton(getValue(ResInfoTypeTools.TYPE_MOBILITY, "toilet"), new String[]{"none", "lvl1", "lvl2", "lvl3"}));
        content.put(MOBILITY_AID_TOILET, setCheckbox(getValue(ResInfoTypeTools.TYPE_MOBILITY, "toilet.aid")));
        content.put(MOBILITY_SITTING, setRadiobutton(getValue(ResInfoTypeTools.TYPE_MOBILITY, "sitting"), new String[]{"none", "lvl1", "lvl2", "lvl3"}));
        content.put(MOBILITY_AID_SITTING, setCheckbox(getValue(ResInfoTypeTools.TYPE_MOBILITY, "sitting.aid")));
        content.put(MOBILITY_BED, setRadiobutton(getValue(ResInfoTypeTools.TYPE_MOBILITY, "bedmovement"), new String[]{"none", "lvl1", "lvl2", "lvl3"}));
        content.put(MOBILITY_AID_BED, setCheckbox(getValue(ResInfoTypeTools.TYPE_MOBILITY, "bedmovement.aid")));

        content.put(MOBILITY_AID_CRUTCH, setCheckbox(getValue(ResInfoTypeTools.TYPE_MOBILITY, "crutch.aid")));
        content.put(MOBILITY_AID_CANE, setCheckbox(getValue(ResInfoTypeTools.TYPE_MOBILITY, "cane.aid")));
        content.put(MOBILITY_AID_WHEELCHAIR, setCheckbox(getValue(ResInfoTypeTools.TYPE_MOBILITY, "wheel.aid")));
        content.put(MOBILITY_AID_COMMODE, setCheckbox(getValue(ResInfoTypeTools.TYPE_INCOAID, "commode.aid"))); // <- from other section
        content.put(MOBILITY_AID_WALKER, setCheckbox(getValue(ResInfoTypeTools.TYPE_MOBILITY, "walker.aid")));
        content.put(MOBILITY_AID_COMMENT, getValue(ResInfoTypeTools.TYPE_MOBILITY, "other.aid"));
        content.put(MOBILITY_BEDRIDDEN, setCheckbox(getValue(ResInfoTypeTools.TYPE_MOBILITY, "bedridden")));

        String mobilityMeasures = "";
        long prev = -1;
        for (InterventionSchedule is : InterventionScheduleTools.getAllActiveByFlag(resident, InterventionTools.FLAG_MOBILITY)) {
            if (is.getIntervention().getMassID() != prev) {
                prev = is.getIntervention().getMassID();
                if (!mobilityMeasures.isEmpty()) {
                    mobilityMeasures += "; ";
                }
                mobilityMeasures += is.getIntervention().getBezeichnung() + ": ";
            }
            mobilityMeasures += InterventionScheduleTools.getTerminAsCompactText(is) + ", ";
        }
        content.put(MOBILITY_BEDPOSITION, mobilityMeasures.substring(0, mobilityMeasures.length() - 2)); // the last ", " has to be cut off again
    }

    private void createContent4Section5() {
        boolean weightControl = !PrescriptionTools.getAllActiveByFlag(resident, InterventionTools.FLAG_WEIGHT_MONITORING).isEmpty() ||
                !InterventionScheduleTools.getAllActiveByFlag(resident, InterventionTools.FLAG_WEIGHT_MONITORING).isEmpty();
        content.put(EXCRETIONS_CONTROL_WEIGHT, setCheckbox(weightControl));
        content.put(MONITORING_WEIGHT, setCheckbox(weightControl));

        Properties controlling = resident.getControlling();
        content.put(EXCRETIONS_LIQUID_BALANCE, setCheckbox(SYSTools.catchNull(controlling.getProperty("liquidbalance")).equalsIgnoreCase("on")));

        content.put(EXCRETIONS_AID_BEDPAN, setCheckbox(getValue(ResInfoTypeTools.TYPE_INCOAID, "bedpan.aid")));
        content.put(EXCRETIONS_AID_COMMODE, setCheckbox(getValue(ResInfoTypeTools.TYPE_INCOAID, "commode.aid")));
        content.put(EXCRETIONS_AID_URINAL, setCheckbox(getValue(ResInfoTypeTools.TYPE_INCOAID, "urinal.aid")));

        boolean noAid = getValue(ResInfoTypeTools.TYPE_INCOAID, "bedpan.aid").equalsIgnoreCase("false") &&
                getValue(ResInfoTypeTools.TYPE_INCOAID, "commode.aid").equalsIgnoreCase("false") &&
                getValue(ResInfoTypeTools.TYPE_INCOAID, "urinal.aid").equalsIgnoreCase("false");
        content.put(EXCRETIONS_AID_NO, setCheckbox(noAid));

        content.put(EXCRETIONS_DIARRHOEA_TENDENCY, setCheckbox(getValue(ResInfoTypeTools.TYPE_EXCRETIONS, "diarrhoe")));
        content.put(EXCRETIONS_OBSTIPATION_TENDENCY, setCheckbox(getValue(ResInfoTypeTools.TYPE_EXCRETIONS, "obstipation")));
        content.put(EXCRETIONS_DIGITAL, setCheckbox(getValue(ResInfoTypeTools.TYPE_EXCRETIONS, "digital")));

        boolean inco_urine = (mapID2Info.containsKey(ResInfoTypeTools.TYPE_INCO_PROFILE_DAY) && !getValue(ResInfoTypeTools.TYPE_INCO_PROFILE_DAY, "inkoprofil").equalsIgnoreCase("kontinenz")) ||
                (mapID2Info.containsKey(ResInfoTypeTools.TYPE_INCO_PROFILE_NIGHT) && !getValue(ResInfoTypeTools.TYPE_INCO_PROFILE_NIGHT, "inkoprofil").equalsIgnoreCase("kontinenz"));
        boolean inco_faecal = (mapID2Info.containsKey(ResInfoTypeTools.TYPE_INCO_FAECAL) && !getValue(ResInfoTypeTools.TYPE_INCO_FAECAL, "incolevel").equalsIgnoreCase("0"));
        content.put(EXCRETIONS_INCO_URINE, setCheckbox(inco_urine));
        content.put(EXCRETIONS_INCO_FAECAL, setCheckbox(inco_faecal));

        content.put(EXCRETIONS_INCOAID_NEEDSHELP, setCheckbox(getValue(ResInfoTypeTools.TYPE_INCOAID, "needshelp")));
        content.put(EXCRETIONS_INCOAID_SELF, setCheckbox(getValue(ResInfoTypeTools.TYPE_INCOAID, "needshelp").equalsIgnoreCase("false")));
        content.put(EXCRETIONS_TRANS_AID, setCheckbox(getValue(ResInfoTypeTools.TYPE_INCOAID, "trans.aid")));
        content.put(EXCRETIONS_SUP_AID, setCheckbox(getValue(ResInfoTypeTools.TYPE_INCOAID, "sup.aid")));


        boolean diapers = getValue(ResInfoTypeTools.TYPE_INCOAID, "windel").equalsIgnoreCase("true");
        boolean pads1 = getValue(ResInfoTypeTools.TYPE_INCOAID, "vorlagen1").equalsIgnoreCase("true") ||
                getValue(ResInfoTypeTools.TYPE_INCOAID, "vorlagen2").equalsIgnoreCase("true") ||
                getValue(ResInfoTypeTools.TYPE_INCOAID, "vorlagen3").equalsIgnoreCase("true");
        boolean pads2 = getValue(ResInfoTypeTools.TYPE_INCOAID, "dbinden").equalsIgnoreCase("true");
        boolean undersheet = getValue(ResInfoTypeTools.TYPE_INCOAID, "krunterlagen").equalsIgnoreCase("true");

        String incoaidtext = (diapers ? OPDE.lang.getString("misc.msg.diaper") + ", " : "") +
                (pads1 ? OPDE.lang.getString("misc.msg.incopad") + ", " : "") +
                (pads2 ? OPDE.lang.getString("misc.msg.sanitarypads") + ", " : "") +
                (undersheet ? OPDE.lang.getString("misc.msg.undersheet") + ", " : "");

        content.put(EXCRETIONS_ONEWAY_AID, setCheckbox(diapers || pads1 || pads2 || undersheet));
        content.put(EXCRETIONS_CURRENT_USED_AID, incoaidtext.substring(0, incoaidtext.length() - 2));

        ArrayList<Prescription> presCatheterChange = PrescriptionTools.getAllActiveByFlag(resident, InterventionTools.FLAG_CATHETER_CHANGE);
        if (!presCatheterChange.isEmpty()) {
            Date lastChange = SYSConst.DATE_THE_VERY_BEGINNING;
            for (Prescription prescription : presCatheterChange) { // usually there shouldn't be more than 1, but you never know
                BHP bhp = BHPTools.getLastBHP(prescription);
                if (bhp != null) {
                    lastChange = new Date(Math.max(lastChange.getTime(), BHPTools.getLastBHP(prescription).getIst().getTime()));
                }
            }
            if (!lastChange.equals(SYSConst.DATE_THE_VERY_BEGINNING)) {
                content.put(EXCRETIONS_LASTCHANGE, DateFormat.getDateInstance().format(lastChange));
            } else {
                content.put(EXCRETIONS_LASTCHANGE, "--");
            }
        }
    }

    private void createContent4Section6() {
        content.put(PROPH_CONTRACTURE, setCheckbox(!InterventionScheduleTools.getAllActiveByFlag(resident, InterventionTools.FLAG_PROPH_CONTRACTURE).isEmpty()));
        content.put(PROPH_BEDSORE, setCheckbox(!InterventionScheduleTools.getAllActiveByFlag(resident, InterventionTools.FLAG_PROPH_BEDSORE).isEmpty()));
        content.put(PROPH_SOOR, setCheckbox(!InterventionScheduleTools.getAllActiveByFlag(resident, InterventionTools.FLAG_PROPH_SOOR).isEmpty()));
        content.put(PROPH_THROMBOSIS, setCheckbox(!InterventionScheduleTools.getAllActiveByFlag(resident, InterventionTools.FLAG_PROPH_THROMBOSIS).isEmpty()));
        content.put(PROPH_PNEUMONIA, setCheckbox(!InterventionScheduleTools.getAllActiveByFlag(resident, InterventionTools.FLAG_PROPH_PNEUMONIA).isEmpty()));
        content.put(PROPH_INTERTRIGO, setCheckbox(!InterventionScheduleTools.getAllActiveByFlag(resident, InterventionTools.FLAG_PROPH_INTERTRIGO).isEmpty()));
        content.put(PROPH_FALL, setCheckbox(!InterventionScheduleTools.getAllActiveByFlag(resident, InterventionTools.FLAG_PROPH_FALL).isEmpty()));
        content.put(PROPH_OBSTIPATION, setCheckbox(!InterventionScheduleTools.getAllActiveByFlag(resident, InterventionTools.FLAG_PROPH_OBSTIPATION).isEmpty()));
    }

    private void createContent4Section7() {
        String braden = "Braden: " + getValue(ResInfoTypeTools.TYPE_SCALE_BRADEN, "scalesum") + " (" + getValue(ResInfoTypeTools.TYPE_SCALE_BRADEN, "risk") + ")";
        content.put(RISKSCALE_TYPE_BEDSORE, braden);
        int rating;
        try {
            rating = Integer.parseInt(getValue(ResInfoTypeTools.TYPE_SCALE_BRADEN, "rating"));
        } catch (NumberFormatException nfe){
            rating = 0;
        }
        content.put(SCALE_RISK_BEDSORE, setCheckbox(rating > 0));
    }

    private String getValue(int type, String propsKey) {
        if (!mapID2Info.containsKey(type)) {
            return "--";
        }
        return SYSTools.catchNull(mapInfo2Properties.get(mapID2Info.get(type)).getProperty(propsKey), "--");
    }

    private String setCheckbox(boolean in) {
        return in ? "1" : "0";
    }

    private String setCheckbox(Object in) {
        return SYSTools.catchNull(in).equalsIgnoreCase("true") ? "1" : "0";
    }

    private String setRadiobutton(Object in, String[] list) {
        String sIn = SYSTools.catchNull(in);
        return Integer.toString(Arrays.asList(list).indexOf(sIn));
    }

    private Properties load(String text) {
        Properties props = new Properties();
        try {
            StringReader reader = new StringReader(text);
            props.load(reader);
            reader.close();
        } catch (IOException ex) {
            OPDE.fatal(ex);
        }
        return props;
    }


    //       _                  _____                      _                    _          _
    //      / \   ___ _ __ ___ |  ___|__  _ __ _ __ ___   | | _____ _   _ ___  | |__   ___| | _____      __
    //     / _ \ / __| '__/ _ \| |_ / _ \| '__| '_ ` _ \  | |/ / _ \ | | / __| | '_ \ / _ \ |/ _ \ \ /\ / /
    //    / ___ \ (__| | | (_) |  _| (_) | |  | | | | | | |   <  __/ |_| \__ \ | |_) |  __/ | (_) \ V  V /
    //   /_/   \_\___|_|  \___/|_|  \___/|_|  |_| |_| |_| |_|\_\___|\__, |___/ |_.__/ \___|_|\___/ \_/\_/
    //                                                              |___/
    public static final String BACK_LOW = "back.low";
    public static final String BACK_MID = "back.mid";
    public static final String BACK_OF_THE_HEAD = "back.of.the.head";
    public static final String BACK_UPPER_LEFT_SIDE = "back.upper.left.side";
    public static final String BEDSORE = "bedsore";
    public static final String BODY1_DESCRIPTION = "body1.description";
    public static final String BODY2_DESCRIPTION = "body2.description";
    public static final String BODY3_DESCRIPTION = "body3.description";
    public static final String BODY4_DESCRIPTION = "body4.description";
    public static final String BODY5_DESCRIPTION = "body5.description";
    public static final String BODY6_DESCRIPTION = "body6.description";
    public static final String BOTTOM_BACK = "bottom.back";
    public static final String BOTTOM_LEFT_SIDE = "bottom.left.side";
    public static final String BOTTOM_RIGHT_SIDE = "bottom.right.side";
    public static final String CALF_LEFTBACK = "calf.leftback";
    public static final String CALF_LEFTSIDE = "calf.leftside";
    public static final String CALF_RIGHTBACK = "calf.rightback";
    public static final String CALF_RIGHT_SIDE = "calf.right.side";
    public static final String COMBING_CARE_BASIN = "combing.care.basin";
    public static final String COMBING_CARE_BED = "combing.care.bed";
    public static final String COMBING_CARE_LEVEL = "combing.care.level";
    public static final String COMBING_CARE_SHOWER = "combing.care.shower";
    public static final String COMMENTS_GENERAL = "comments.general";
    public static final String COMMS_HEARING_ABILITY = "comms.hearing.ability";
    public static final String COMMS_MOTHERTONGUE = "comms.mothertongue";
    public static final String COMMS_SEEING_ABILITY = "comms.seeing.ability";
    public static final String COMMS_SPEECH_ABILITY = "comms.speech.ability";
    public static final String COMMS_UNDERSTANDING_ABILITY = "comms.understanding.ability";
    public static final String COMMS_WRITING_ABILITY = "comms.writing.ability";
    public static final String CONFIDANT_CITY = "confidant.city";
    public static final String CONFIDANT_FIRSTNAME = "confidant.firstname";
    public static final String CONFIDANT_NAME = "confidant.name";
    public static final String CONFIDANT_PHONE = "confidant.phone";
    public static final String CONFIDANT_STREET = "confidant.street";
    public static final String CONFIDANT_ZIP = "confidant.zip";
    public static final String CONSCIOUSNESS_AWAKE = "consciousness.awake";
    public static final String CONSCIOUSNESS_COMA = "consciousness.coma";
    public static final String CONSCIOUSNESS_SOMNOLENT = "consciousness.somnolent";
    public static final String CONSCIOUSNESS_SOPOR = "consciousness.sopor";
    public static final String CROOK_ARM_LEFT = "crook.arm.left";
    public static final String CROOK_ARM_RIGHT = "crook.arm.right";
    public static final String DENTURE_CARE_BASIN = "denture.care.basin";
    public static final String DENTURE_CARE_BED = "denture.care.bed";
    public static final String DENTURE_CARE_LEVEL = "denture.care.level";
    public static final String DENTURE_CARE_SHOWER = "denture.care.shower";
    public static final String DIAG_ICD10 = "diag.icd10";
    public static final String DOCS_GP_REPORT = "docs.gp.report";
    public static final String DOCS_IMAGES = "docs.images";
    public static final String DOCS_LAB = "docs.lab";
    public static final String DOCS_MEDS_LIST = "docs.meds.list";
    public static final String DOCS_MISC = "docs.misc";
    public static final String DOCS_PREVIOUS = "docs.previous";
    public static final String DOCS_WITHPATIENT = "docs.withPatient";
    public static final String DRESSING_CARE_BASIN = "dressing.care.basin";
    public static final String DRESSING_CARE_BED = "dressing.care.bed";
    public static final String DRESSING_CARE_LEVEL = "dressing.care.level";
    public static final String DRESSING_CARE_SHOWER = "dressing.care.shower";
    public static final String ELLBOW_LEFT = "ellbow.left";
    public static final String ELLBOW_RIGHT = "ellbow.right";
    public static final String ELLBOW_RIGHTSIDE = "ellbow.rightside";
    public static final String ELLBOW_SIDE_LEFT = "ellbow.side.left";
    public static final String EXCRETIONS_AID_BEDPAN = "excretions.aid.bedpan";
    public static final String EXCRETIONS_AID_COMMODE = "excretions.aid.commode";
    public static final String EXCRETIONS_AID_NO = "excretions.aid.no";
    public static final String EXCRETIONS_AID_URINAL = "excretions.aid.urinal";
    public static final String EXCRETIONS_AP_AID = "excretions.ap.aid";
    public static final String EXCRETIONS_COMMENT = "excretions.comment";
    public static final String EXCRETIONS_CONTROL_WEIGHT = "excretions.control.weight";
    public static final String EXCRETIONS_CURRENT_USED_AID = "excretions.current.used.aid";
    public static final String EXCRETIONS_DIARRHOEA_TENDENCY = "excretions.diarrhoea.tendency";
    public static final String EXCRETIONS_DIGITAL = "excretions.digital";
    public static final String EXCRETIONS_INCOAID_NEEDSHELP = "excretions.incoaid.needshelp";
    public static final String EXCRETIONS_INCOAID_SELF = "excretions.incoaid.self";
    public static final String EXCRETIONS_INCO_FAECAL = "excretions.inco.faecal";
    public static final String EXCRETIONS_INCO_URINE = "excretions.inco.urine";
    public static final String EXCRETIONS_LASTCHANGE = "excretions.lastchange";
    public static final String EXCRETIONS_LIQUID_BALANCE = "excretions.liquid.balance";
    public static final String EXCRETIONS_NORMAL = "excretions.normal";
    public static final String EXCRETIONS_OBSTIPATION_TENDENCY = "excretions.obstipation.tendency";
    public static final String EXCRETIONS_ONEWAY_AID = "excretions.oneway.aid";
    public static final String EXCRETIONS_SUP_AID = "excretions.sup.aid";
    public static final String EXCRETIONS_TRANS_AID = "excretions.trans.aid";
    public static final String EXCRETIONS_TUBESIZE_CH = "excretions.tubesize.ch";
    public static final String FACE = "face";
    public static final String FOOD_ABROSIA = "food.abrosia";
    public static final String FOOD_ARTIFICIAL_FEEDING = "food.artificial.feeding";
    public static final String FOOD_ASSISTANCE_LEVEL = "food.assistance.level";
    public static final String FOOD_BITESIZE = "food.bitesize";
    public static final String FOOD_BMI = "food.bmi";
    public static final String FOOD_BREADUNTIS = "food.breaduntis";
    public static final String FOOD_DAILY_KCAL = "food.daily.kcal";
    public static final String FOOD_DAILY_ML = "food.daily.ml";
    public static final String FOOD_DRINKINGMOTIVATION = "food.drinkingmotivation";
    public static final String FOOD_DRINKSALONE = "food.drinksalone";
    public static final String FOOD_DYSPHAGIA = "food.dysphagia";
    public static final String FOOD_GRAVITY = "food.gravity";
    public static final String FOOD_LAST_MEAL = "food.last.meal";
    public static final String FOOD_LIQUIDS_DAILY_ML = "food.liquids.daily.ml";
    public static final String FOOD_ORALNUTRITION = "food.oralnutrition";
    public static final String FOOD_PARENTERAL = "food.parenteral";
    public static final String FOOD_PUMP = "food.pump";
    public static final String FOOD_SYRINGE = "food.syringe";
    public static final String FOOD_TEE_DAILY_ML = "food.tee.daily.ml";
    public static final String FOOD_TUBESINCE = "food.tubesince";
    public static final String FOOD_TUBETYPE = "food.tubetype";
    public static final String FOOT_LEFTBACK = "foot.leftback";
    public static final String FOOT_LEFT_FRONT = "foot.left.front";
    public static final String FOOT_RIGHTBACK = "foot.rightback";
    public static final String FOOT_RIGHT_FRONT = "foot.right.front";
    public static final String GROIN = "groin";
    public static final String HAND_LEFT_SIDE = "hand.left.side";
    public static final String HAND_RIGHT_SIDE = "hand.right.side";
    public static final String HEAD_LEFT_SIDE = "head.left.side";
    public static final String HEAD_RIGHT_SIDE = "head.right.side";
    public static final String HEEL_LEFT_SIDE = "heel.left.side";
    public static final String HEEL_RIGHT_SIDE = "heel.right.side";
    public static final String HIP_LEFT_SIDE = "hip.left.side";
    public static final String HIP_RIGHT_SIDE = "hip.right.side";
    public static final String KNEE_HOLLOWLEFT = "knee.hollowleft";
    public static final String KNEE_HOLLOWRIGHT = "knee.hollowright";
    public static final String KNEE_LEFT = "knee.left";
    public static final String KNEE_RIGHT = "knee.right";
    public static final String LC_CITY = "lc.city";
    public static final String LC_CUSTODY = "lc.custody";
    public static final String LC_FINANCE = "lc.finance";
    public static final String LC_FIRSTNAME = "lc.firstname";
    public static final String LC_GENERAL = "lc.general";
    public static final String LC_HEALTH = "lc.health";
    public static final String LC_NAME = "lc.name";
    public static final String LC_PHONE = "lc.phone";
    public static final String LC_STREET = "lc.street";
    public static final String LC_ZIP = "lc.zip";
    public static final String LEGAL_MINOR = "legal.minor";
    public static final String LEGAL_SINGLE = "legal.single";
    public static final String LOWER_BELLY = "lower.belly";
    public static final String LOWER_LEG_LEFT_SIDE = "lower.leg.left.side";
    public static final String LOWER_LEG_RIGHT_SIDE = "lower.leg.right.side";
    public static final String MEDS10_EVENING = "meds10.evening";
    public static final String MEDS10_MORNING = "meds10.morning";
    public static final String MEDS10_NIGHT = "meds10.night";
    public static final String MEDS10_NOON = "meds10.noon";
    public static final String MEDS10_TEXT = "meds10.text";
    public static final String MEDS11_EVENING = "meds11.evening";
    public static final String MEDS11_MORNING = "meds11.morning";
    public static final String MEDS11_NIGHT = "meds11.night";
    public static final String MEDS11_NOON = "meds11.noon";
    public static final String MEDS11_TEXT = "meds11.text";
    public static final String MEDS12_EVENING = "meds12.evening";
    public static final String MEDS12_MORNING = "meds12.morning";
    public static final String MEDS12_NIGHT = "meds12.night";
    public static final String MEDS12_NOON = "meds12.noon";
    public static final String MEDS12_TEXT = "meds12.text";
    public static final String MEDS13_EVENING = "meds13.evening";
    public static final String MEDS13_MORNING = "meds13.morning";
    public static final String MEDS13_NIGHT = "meds13.night";
    public static final String MEDS13_NOON = "meds13.noon";
    public static final String MEDS13_TEXT = "meds13.text";
    public static final String MEDS14_EVENING = "meds14.evening";
    public static final String MEDS14_MORNING = "meds14.morning";
    public static final String MEDS14_NIGHT = "meds14.night";
    public static final String MEDS14_NOON = "meds14.noon";
    public static final String MEDS14_TEXT = "meds14.text";
    public static final String MEDS15_EVENING = "meds15.evening";
    public static final String MEDS15_MORNING = "meds15.morning";
    public static final String MEDS15_NIGHT = "meds15.night";
    public static final String MEDS15_NOON = "meds15.noon";
    public static final String MEDS15_TEXT = "meds15.text";
    public static final String MEDS1_EVENING = "meds1.evening";
    public static final String MEDS1_MORNING = "meds1.morning";
    public static final String MEDS1_NIGHT = "meds1.night";
    public static final String MEDS1_NOON = "meds1.noon";
    public static final String MEDS1_TEXT = "meds1.text";
    public static final String MEDS2_EVENING = "meds2.evening";
    public static final String MEDS2_MORNING = "meds2.morning";
    public static final String MEDS2_NIGHT = "meds2.night";
    public static final String MEDS2_NOON = "meds2.noon";
    public static final String MEDS2_TEXT = "meds2.text";
    public static final String MEDS3_EVENING = "meds3.evening";
    public static final String MEDS3_MORNING = "meds3.morning";
    public static final String MEDS3_NIGHT = "meds3.night";
    public static final String MEDS3_NOON = "meds3.noon";
    public static final String MEDS3_TEXT = "meds3.text";
    public static final String MEDS4_EVENING = "meds4.evening";
    public static final String MEDS4_MORNING = "meds4.morning";
    public static final String MEDS4_NIGHT = "meds4.night";
    public static final String MEDS4_NOON = "meds4.noon";
    public static final String MEDS4_TEXT = "meds4.text";
    public static final String MEDS5_EVENING = "meds5.evening";
    public static final String MEDS5_MORNING = "meds5.morning";
    public static final String MEDS5_NIGHT = "meds5.night";
    public static final String MEDS5_NOON = "meds5.noon";
    public static final String MEDS5_TEXT = "meds5.text";
    public static final String MEDS6_EVENING = "meds6.evening";
    public static final String MEDS6_MORNING = "meds6.morning";
    public static final String MEDS6_NIGHT = "meds6.night";
    public static final String MEDS6_NOON = "meds6.noon";
    public static final String MEDS6_TEXT = "meds6.text";
    public static final String MEDS7_EVENING = "meds7.evening";
    public static final String MEDS7_MORNING = "meds7.morning";
    public static final String MEDS7_NIGHT = "meds7.night";
    public static final String MEDS7_NOON = "meds7.noon";
    public static final String MEDS7_TEXT = "meds7.text";
    public static final String MEDS8_EVENING = "meds8.evening";
    public static final String MEDS8_MORNING = "meds8.morning";
    public static final String MEDS8_NIGHT = "meds8.night";
    public static final String MEDS8_NOON = "meds8.noon";
    public static final String MEDS8_TEXT = "meds8.text";
    public static final String MEDS9_EVENING = "meds9.evening";
    public static final String MEDS9_MORNING = "meds9.morning";
    public static final String MEDS9_NIGHT = "meds9.night";
    public static final String MEDS9_NOON = "meds9.noon";
    public static final String MEDS9_TEXT = "meds9.text";
    public static final String MEDS_CONTROL = "meds.control";
    public static final String MEDS_DAILY_GLUCOSECHECK = "meds.daily.glucosecheck";
    public static final String MEDS_DAILY_RATION = "meds.daily.ration";
    public static final String MEDS_INJECTION_LEVEL = "meds.injection.level";
    public static final String MEDS_INSULIN_APPLICATION = "meds.insulin.application";
    public static final String MEDS_LAST_APPLICATION = "meds.last.application";
    public static final String MEDS_MARCUMARPASS = "meds.marcumarpass";
    public static final String MEDS_MORNING_GLUCOSE = "meds.morning.glucose";
    public static final String MEDS_SELF = "meds.self";
    public static final String MEDS_WEEKLY_GLUCOSECHECK = "meds.weekly.glucosecheck";
    public static final String MOBILITY_AID_BED = "mobility.aid.bed";
    public static final String MOBILITY_AID_CANE = "mobility.aid.cane";
    public static final String MOBILITY_AID_COMMENT = "mobility.aid.comment";
    public static final String MOBILITY_AID_COMMODE = "mobility.aid.commode";
    public static final String MOBILITY_AID_CRUTCH = "mobility.aid.crutch";
    public static final String MOBILITY_AID_SITTING = "mobility.aid.sitting";
    public static final String MOBILITY_AID_GETUP = "mobility.aid.stand";
    public static final String MOBILITY_AID_TOILET = "mobility.aid.toilet";
    public static final String MOBILITY_AID_TRANSFER = "mobility.aid.transfer";
    public static final String MOBILITY_AID_WALKER = "mobility.aid.walker";
    public static final String MOBILITY_AID_WALKING = "mobility.aid.walking";
    public static final String MOBILITY_AID_WHEELCHAIR = "mobility.aid.wheelchair";
    public static final String MOBILITY_BED = "mobility.bed";
    public static final String MOBILITY_BEDPOSITION = "mobility.bedposition";
    public static final String MOBILITY_BEDRIDDEN = "mobility.bedridden";
    public static final String MOBILITY_COMMENT = "mobility.comment";
    public static final String MOBILITY_GET_UP = "mobility.get.up";
    public static final String MOBILITY_SITTING = "mobility.sitting";
    public static final String MOBILITY_TOILET = "mobility.toilet";
    public static final String MOBILITY_TRANSFER = "mobility.transfer";
    public static final String MOBILITY_WALKING = "mobility.walking";
    public static final String MONITORING_BP = "monitoring.bp";
    public static final String MONITORING_EXCRETION = "monitoring.excretion";
    public static final String MONITORING_INTAKE = "monitoring.intake";
    public static final String MONITORING_PAIN = "monitoring.pain";
    public static final String MONITORING_PORT = "monitoring.port";
    public static final String MONITORING_PULSE = "monitoring.pulse";
    public static final String MONITORING_RESPIRATION = "monitoring.respiration";
    public static final String MONITORING_TEMP = "monitoring.temp";
    public static final String MONITORING_WEIGHT = "monitoring.weight";
    public static final String MOUTH_CARE_BASIN = "mouth.care.basin";
    public static final String MOUTH_CARE_BED = "mouth.care.bed";
    public static final String MOUTH_CARE_LEVEL = "mouth.care.level";
    public static final String MOUTH_CARE_SHOWER = "mouth.care.shower";
    public static final String ORIENTATION_LOCATION_ABILITY = "orientation.location.ability";
    public static final String ORIENTATION_PERSONAL_ABILITY = "orientation.personal.ability";
    public static final String ORIENTATION_RUNNAWAY_TENDENCY = "orientation.runnaway.tendency";
    public static final String ORIENTATION_SITUATION_ABILITY = "orientation.situation.ability";
    public static final String ORIENTATION_TIME_ABILITY = "orientation.time.ability";
    public static final String DATE_REQUESTED_INSURANCE_GRADE_PAGE3 = "page3.insurance.requested";
    public static final String TX2_DATE_PAGE3 = "page3.tx2.date";
    public static final String PERSONAL_CARE_BASIN = "personal.care.basin";
    public static final String PERSONAL_CARE_BED = "personal.care.bed";
    public static final String PERSONAL_CARE_COMMENT = "personal.care.comment";
    public static final String PERSONAL_CARE_LEVEL = "personal.care.level";
    public static final String PERSONAL_CARE_SHOWER = "personal.care.shower";
    public static final String PROPH_BEDSORE = "proph.bedsore";
    public static final String PROPH_CONTRACTURE = "proph.contracture";
    public static final String PROPH_FALL = "proph.fall";
    public static final String PROPH_INTERTRIGO = "proph.intertrigo";
    public static final String PROPH_OBSTIPATION = "proph.obstipation";
    public static final String PROPH_PNEUMONIA = "proph.pneumonia";
    public static final String PROPH_SOOR = "proph.soor";
    public static final String PROPH_THROMBOSIS = "proph.thrombosis";
    public static final String RESIDENT_CARDSTATE_HINSURANCE = "resident.cardstate.hinsurance";
    public static final String RESIDENT_CITY = "resident.city";
    public static final String RESIDENT_DOB = "resident.dob";
    public static final String RESIDENT_DOB_PAGE2 = "page2.resident.dob";
    public static final String RESIDENT_DOB_PAGE3 = "page3.resident.dob";
    public static final String RESIDENT_FIRSTNAME = "resident.firstname";
    public static final String RESIDENT_FIRSTNAME_PAGE2 = "page2.resident.firstname";
    public static final String RESIDENT_FULLNAME_PAGE3 = "page3.resident.fullname";
    public static final String RESIDENT_GENDER = "resident.gender";
    public static final String RESIDENT_HINSURANCE = "resident.hinsurance";
    public static final String RESIDENT_HINSURANCEID = "resident.hinsuranceid";
    public static final String RESIDENT_HINSURANCENO = "resident.hinsuranceno";
    public static final String RESIDENT_HINSURANCE_PAGE3 = "page3.resident.hinsurance";
    public static final String RESIDENT_NAME = "resident.name";
    public static final String RESIDENT_NAME_PAGE2 = "page2.resident.name";
    public static final String RESIDENT_PHONE = "resident.phone";
    public static final String RESIDENT_PHONE_PAGE2 = "page2.phone";
    public static final String RESIDENT_STREET = "resident.street";
    public static final String RESIDENT_ZIP = "resident.zip";
    public static final String RESPIRATION_ASPIRATE = "respiration.aspirate";
    public static final String RESPIRATION_ASTHMA = "respiration.asthma";
    public static final String RESPIRATION_CARDCONGEST = "respiration.cardcongest";
    public static final String RESPIRATION_COMMENT = "respiration.comment";
    public static final String RESPIRATION_COUGH = "respiration.cough";
    public static final String RESPIRATION_MUCOUS = "respiration.mucous";
    public static final String RESPIRATION_NORMAL = "respiration.normal";
    public static final String RESPIRATION_OTHER = "respiration.other";
    public static final String RESPIRATION_PAIN = "respiration.pain";
    public static final String RESPIRATION_RAUCHEN = "respiration.rauchen";
    public static final String RESPIRATION_SILICONTUBE = "respiration.silicontube";
    public static final String RESPIRATION_SILVERTUBE = "respiration.silvertube";
    public static final String RESPIRATION_SPUTUM = "respiration.sputum";
    public static final String RESPIRATION_STOMA = "respiration.stoma";
    public static final String RESPIRATION_TUBESIZE = "respiration.tubesize";
    public static final String RESPIRATION_TUBETYPE = "respiration.tubetype";
    public static final String RISKSCALE_TYPE_BEDSORE = "riskscale.type.bedsore";
    public static final String SCALE_RISK_BEDSORE = "scale.risk.bedsore";
    public static final String SHAVE_CARE_BASIN = "shave.care.basin";
    public static final String SHAVE_CARE_BED = "shave.care.bed";
    public static final String SHAVE_CARE_LEVEL = "shave.care.level";
    public static final String SHAVE_CARE_SHOWER = "shave.care.shower";
    public static final String SHIN_LEFT_FRONT = "shin.left.front";
    public static final String SHIN_RIGHT_FRONT = "shin.right.front";
    public static final String SHOULDER_BACK_LEFT = "shoulder.back.left";
    public static final String SHOULDER_BACK_RIGHT = "shoulder.back.right";
    public static final String SHOULDER_FRONT_LEFT = "shoulder.front.left";
    public static final String SHOULDER_FRONT_RIGHT = "shoulder.front.right";
    public static final String SHOULDER_LEFT_SIDE = "shoulder.left.side";
    public static final String SHOULDER_RIGHT_SIDE = "shoulder.right.side";
    public static final String SKIN_DRY = "skin.dry";
    public static final String SKIN_GREASY = "skin.greasy";
    public static final String SKIN_ITCH = "skin.itch";
    public static final String SKIN_NORMAL = "skin.normal";
    public static final String SLEEP_COMMENTS = "sleep.comments";
    public static final String SLEEP_INSOMNIA = "sleep.insomnia";
    public static final String SLEEP_NORMAL = "sleep.normal";
    public static final String SLEEP_POS_BACK = "sleep.pos.back";
    public static final String SLEEP_POS_FRONT = "sleep.pos.front";
    public static final String SLEEP_POS_LEFT = "sleep.pos.left";
    public static final String SLEEP_POS_RIGHT = "sleep.pos.right";
    public static final String SLEEP_RESTLESS = "sleep.restless";
    public static final String SOCIAL_CONFIDANT_CARE = "social.confidant.care";
    public static final String SOCIAL_CURRENT_RESTHOME = "social.current.resthome";
    public static final String SOCIAL_CURRENT_SITUATION_CONFIDANT = "social.current.situation.confidant";
    public static final String SOCIAL_CURRENT_SITUATION_NURSING_SERVICE = "social.current.situation.nursing.service";
    public static final String SOCIAL_CURRENT_SITUATION_SELF = "social.current.situation.self";
    public static final String SOCIAL_RELIGION = "social.religion";
    public static final String SPECIAL_ALLERGIEPASS = "special.allergiepass";
    public static final String SPECIAL_COMMENT_ALLERGY = "special.comment.allergy";
    public static final String SPECIAL_LASTCONTROL_PACER = "special.lastcontrol.pacer";
    public static final String SPECIAL_MRE = "special.mre";
    public static final String SPECIAL_MYCOSIS = "special.mycosis";
    public static final String SPECIAL_PACER = "special.pacer";
    public static final String SPECIAL_PALLIATIVE = "special.palliative";
    public static final String SPECIAL_WOUNDPAIN = "special.woundpain";
    public static final String SPECIAL_WOUNDS = "special.wounds";
    public static final String SPECIAL_YESNO_ALLERGY = "special.yesno.allergy";
    public static final String THERAPY_ERGO = "therapy.ergo";
    public static final String THERAPY_LOGO = "therapy.logo";
    public static final String THERAPY_PHYSIO = "therapy.physio";
    public static final String TX_DATE = "tx.date";
    public static final String TX_DATE_PAGE2 = "page2.date";
    public static final String TX_DATE_PAGE3 = "page3.tx.date";
    public static final String TX_FINAL = "tx.final";
    public static final String TX_LOGO = "tx.logo";
    public static final String TX_RECIPIENT = "tx.recipient";
    public static final String TX_RECIPIENT_OTHER = "tx.recipient.other";
    public static final String TX_TIME = "tx.time";
    public static final String UPPER_BACK_LEFT_SIDE = "upper.back.left.side";
    public static final String UPPER_BELLY = "upper.belly";
    public static final String UPPER_LEFTLEG_BACK = "upper.leftleg.back";
    public static final String UPPER_LEG_LEFT_FRONT = "upper.leg.left.front";
    public static final String UPPER_LEG_LEFT_SIDE = "upper.leg.left.side";
    public static final String UPPER_LEG_RIGHT_FRONT = "upper.leg.right.front";
    public static final String UPPER_LEG_RIGHT_SIDE = "upper.leg.right.side";
    public static final String UPPER_RIGHTLEG_BACK = "upper.rightleg.back";
    public static final String USERNAME_PAGE2 = "page2.username";
    public static final String VALUABLES_COURTORDER = "valuables.courtorder";
    public static final String VALUABLES_CREDITCARD = "valuables.creditcard";
    public static final String VALUABLES_HEALTHCARD = "valuables.healthcard";
    public static final String VALUABLES_KEYS = "valuables.keys";
    public static final String VALUABLES_LIVINGWILL = "valuables.livingwill";
    public static final String VALUABLES_ORGANDONOR = "valuables.organdonor";
    public static final String VALUABLES_OTHER = "valuables.other";
    public static final String VALUABLES_WALLET = "valuables.wallet";
    public static final String VALUABLES_WATCH = "valuables.watch";
    public static final String ASSIGNED_INSURANCE_GRADE = "assigned.insurance.grade";
    public static final String DATE_REQUESTED_INSURANCE_GRADE = "date.requested.insurance.grade";
    public static final String DOCS_ADDITIONALMEDSLIST = "docs.additionalMedsList";
    public static final String INSURANCE_GRADE = "insurance.grade";
    public static final String MEDS_EVENING_GLUCOSE = "meds.evening.glucose";
    public static final String MEDS_NOON_GLUCOSE = "meds.noon.glucose";
    public static final String PAGE3_DOCS_WITHPATIENT = "page3.docs.withPatient";
    public static final String PREFERRED_CAREPRODUCTS = "preferred.careproducts";


}