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
 */
package op;

import com.jidesoft.utils.Lm;
import com.jidesoft.wizard.WizardStyle;
import entity.EntityTools;
import entity.Users;
import entity.UsersTools;
import entity.system.SYSLogin;
import entity.system.SYSPropsTools;
import entity.system.SyslogTools;
import entity.verordnungen.BHPTools;
import op.care.DFNImport;
import op.system.FrmInit;
import op.system.PrinterTypes;
import op.threads.DisplayManager;
import op.threads.PrintProcessor;
import op.tools.*;
import org.apache.commons.cli.*;
import org.apache.log4j.*;
import org.eclipse.persistence.config.QueryHints;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.swing.*;
import javax.swing.event.EventListenerList;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.InetAddress;
import java.sql.SQLException;
import java.text.DateFormat;
import java.util.*;

//import op.threads.BackgroundMonitor;

public class OPDE {
    public static final String internalClassID = "opde";

    public static final int INFO_TIME = 2;
    public static final int WARNING_TIME = 4;
    public static final int ERROR_TIME = 4;
    public static long uptime;
    public static ResourceBundle lang;
    protected static Database db;
    //    public static OPMain ocmain;
    public static FrmMain mainframe;
    protected static String url;
    protected static PrinterTypes printers;
    protected static Properties props;
    protected static boolean anonym;
    protected static SortedProperties localProps;
    protected static Logger logger;
    private static OCSec ocsec;
    public static HashMap[] anonymize = null;
    // Diese listener List ist dazu da, dass wir immer
    // wissen, welchen Fenstern wir bescheid sagen müssen, wenn
    // der User sich abmeldet.
    protected static EventListenerList listenerList = new EventListenerList();
    protected static HashMap<String, ActionListener> runningModules = new HashMap();
    protected static EntityManagerFactory emf;
    protected static AppInfo appInfo;
    protected static SYSLogin login;
    //    protected static SYSHosts host;
//    protected static BackgroundMonitor bm;
    // Vielleicht später
//    protected static ArrayList<ImageIcon> animationCache;
    protected static boolean animation = false;
    protected static boolean debug;
    protected static String opwd = "";
    protected static String css = "";
    public static Font arial14;

    /**
     * @return Das Arbeitsverzeichnis für OPDE.
     */
    public static String getOpwd() {
        return opwd;
    }

    public static String getOPCache() {
        return opwd + System.getProperty("file.separator") + "cache";
    }

    public static PrinterTypes getPrinters() {
        return printers;
    }

//    public static ArrayList<ImageIcon> getAnimationCache() {
//        return animationCache;
//    }

    public static boolean isDebug() {
        return debug;
    }

    public static int getErrorMessageTime() {
        return ERROR_TIME;
    }

    public static boolean isAnimation() {
        return animation;
    }

    public static boolean isAnonym() {
        return anonym;
    }

    public static void setProp(String key, String value) {
        props.put(key, value);
    }

    public static String getCSS() {
        return css;
    }

    public static EntityManagerFactory getEMF() {
        return emf;
    }

    public static String getUrl() {
        return url;
    }

    public static void initProps() {
        if (OPDE.getProps() != null) {
            OPDE.getProps().clear();
        }
        OPDE.getProps().putAll(SYSPropsTools.loadProps(null));
        OPDE.getProps().putAll(SYSPropsTools.loadProps(login.getUser()));
        OPDE.getProps().putAll(OPDE.getLocalProps());
    }

//    public static boolean addNewModule(String internalClassID, ActionListener listener) {
//        boolean success = false;
//        if (!runningModules.containsKey(internalClassID)) {
//            listenerList.add(ActionListener.class, listener);
//            runningModules.put(internalClassID, listener);
//            success = true;
//            OPDE.debug("Modul " + internalClassID + " hinzugefügt.");
//        }
//        return success;
//    }
//
//    public static void removeModule(String internalClassID) {
//        listenerList.remove(ActionListener.class, runningModules.get(internalClassID));
//        runningModules.remove(internalClassID);
//        OPDE.debug("Modul " + internalClassID + " entfernt.");
//    }
//
//    public static void notifyAboutLogout() {
//        Object[] listeners = listenerList.getListenerList();
//        // Each listener occupies two elements - the first is the listener class
//        // and the second is the listener instance
//        for (int i = 0; i < listeners.length; i += 2) {
//            ((ActionListener) listeners[i + 1]).actionPerformed(new ActionEvent(OPDE.class, 1, "LOGOUT"));
//        }
//        clearModules();
//    }
//
//    protected static void clearModules() {
//        Iterator<String> keys = runningModules.keySet().iterator();
//        while (keys.hasNext()) {
//            listenerList.remove(ActionListener.class, runningModules.get(keys.next()));
//        }
//        runningModules.clear();
//        listenerList = new EventListenerList();
//    }

    public static Logger getLogger() {
        return logger;
    }

    /**
     * Diese initDB() Methode wird verschwinden, sobald ich ganz auf JPA umgestellt habe.
     *
     * @throws SQLException
     */
    @Deprecated
    public static void initDB() throws Exception {
        if (db != null) return;
        String dbuser = localProps.getProperty("javax.persistence.jdbc.user");
        String dbpw = localProps.getProperty("javax.persistence.jdbc.password");
        String hostkey = OPDE.getLocalProps().getProperty("hostkey");
        // Passwort entschlüsseln
        DesEncrypter desEncrypter = new DesEncrypter(hostkey);
        db = new Database(url, dbuser, desEncrypter.decrypt(dbpw).toCharArray());
    }

    public static void warn(Object message) {
        logger.warn(message);
        SyslogTools.warn(message.toString());
    }

    @Deprecated
    public static void closeDB() throws SQLException {
        db.db.close();
        db = null;
    }

    public static void info(Object message) {
        logger.info(message);
    }

    public static void important(Object message) {
        logger.info(message);
        SyslogTools.info(message.toString());
    }

    public static void fatal(Throwable e) {
        logger.fatal(e.getMessage(), e);
        SyslogTools.fatal(e.getMessage());
        e.printStackTrace();

        String html = SYSTools.getThrowableAsHTML(e);

        File temp = SYSPrint.print(html, false);

        if (!isDebug() && props.containsKey("mail.smtp.host")) { //Stellvertretend für die anderen Keys.
            try {

                InetAddress localMachine = InetAddress.getLocalHost();

                javax.mail.Authenticator auth = new javax.mail.Authenticator() {
                    @Override
                    public PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(props.getProperty("mail.sender"), props.getProperty("mail.password"));
                    }
                };

                Session session = Session.getDefaultInstance(props, auth);

                Message msg = new MimeMessage(session);
                msg.setFrom(new InternetAddress(props.getProperty("mail.sender"), props.getProperty("mail.sender.personal")));
                msg.addRecipient(Message.RecipientType.TO, new InternetAddress(props.getProperty("mail.recipient"), props.getProperty("mail.recipient.personal")));
                msg.setSubject("OPDE Exception: " + e.getMessage());


                BodyPart messageBodyPart = new MimeBodyPart();

                messageBodyPart.setText("" +
                        "Es ist ein Fehler in OPDE aufgetreten.\n" +
                        "Auf Host: " + localMachine.getHostName() + "\n" +
                        "IP-Adresse: " + localMachine.getHostAddress() + "\n" +
                        "Angemelet war: " + getLogin().getUser().getUKennung() + "\n" +
                        "Zeitpunkt: " + DateFormat.getDateTimeInstance().format(new Date()) + "\n\n\n" +
                        "Siehe Anhang für Stacktrace");

                Multipart multipart = new MimeMultipart();
                multipart.addBodyPart(messageBodyPart);
                messageBodyPart = new MimeBodyPart();

                DataSource source = new FileDataSource(temp);
                messageBodyPart.setDataHandler(new DataHandler(source));
                messageBodyPart.setFileName(temp.getName());

                multipart.addBodyPart(messageBodyPart);
                msg.setContent(multipart);
                msg.saveChanges();

                Transport.send(msg);
            } catch (MessagingException e1) {
                OPDE.info(e1);
                OPDE.info("Mail-System nicht korrekt konfiguriert");
                e1.printStackTrace();
            } catch (UnsupportedEncodingException e1) {
                OPDE.info(e1);
                e1.printStackTrace();
            } catch (java.net.UnknownHostException uhe) {
                OPDE.info(uhe);
            }
        }

        System.exit(1);
    }

    public static void error(Object message) {
        logger.error(message);
        SyslogTools.error(message.toString());
    }

    public static void debug(Object message) {
        logger.debug(message);
    }

    public static EntityManager createEM() {
        return emf.createEntityManager();
    }

    public static Properties getProps() {
        return props;
    }

    public static Properties getLocalProps() {
        return localProps;
    }

    public static void saveLocalProps() {
        try {
            FileOutputStream out = new FileOutputStream(new File(opwd + System.getProperty("file.separator") + "local.properties"));
            localProps.store(out, "Lokale Einstellungen für Offene-Pflege.de");
            out.close();
        } catch (Exception ex) {
            fatal(ex);
            System.exit(1);
        }
    }

    public static AppInfo getAppInfo() {
        return appInfo;
    }

    public static SYSLogin getLogin() {
        return login;
    }

    public static void setLogin(SYSLogin login) {
        OPDE.login = login;
    }

    /**
     * Hier ist die main Methode von OPDE. In dieser Methode wird auch festgestellt, wie OPDE gestartet wurde.
     * <ul>
     * <li>Im Standard Modus, das heisst mit graphischer Oberfläche. Das dürfte der häufigste Fall sein.</li>
     * <li>Im DFNImport Modus. Der wird meist auf dem Datenbankserver gebraucht um Nachts die Durchführungsnachweise anhand der
     * DFNImport Tabelle zu generieren. Das alles gehört zu der Pflegeplanung.</li>
     * <li>Im BHPImport Modus. Auch dieser Modus wird auf dem DB-Server gebraucht um die Behandlungspflege Massnahmen
     * anhand der ärztlichen Verordnungen zu generieren.</li>
     * </ul>
     *
     * @param args Hier stehen die Kommandozeilen Parameter. Diese werden mit
     */
    public static void main(String[] args) throws Exception {
        // throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException {
        uptime = SYSCalendar.now();
        arial14 = new Font("Arial", Font.PLAIN, 14);
        lang = ResourceBundle.getBundle("languageBundle", Locale.getDefault());

        // Das hier fängt alle ungefangenen Exceptions auf.
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                OPDE.fatal(e);
            }
        });

        localProps = new SortedProperties();
        props = new Properties();

        // LogSystem initialisieren.
        logger = Logger.getRootLogger();
        PatternLayout layout = new PatternLayout("%d{ISO8601} %-5p [%t] %c: %m%n");
        ConsoleAppender consoleAppender = new ConsoleAppender(layout);
        logger.addAppender(consoleAppender);

        appInfo = new AppInfo();

        // AUSWERTUNG KOMMANDOZEILE-----------------------------------------------------------------------------
        // Hier erfolgt die Unterscheidung, in welchem Modus OPDE gestartet wurde.
        Options opts = new Options();
        opts.addOption("h", "hilfe", false, "Gibt die Hilfeseite für OPDE aus.");
        opts.addOption("v", "version", false, "Zeigt die Versionsinformationen an.");
        opts.addOption("a", "anonym", false, "Blendet die Bewohnernamen in allen Ansichten aus. Spezieller Modus für Schulungsmaterial zu erstellen.");
        opts.addOption("l", "debug", false, "Schaltet alle Ausgaben ein auf der Konsole ein, auch die, die eigentlich nur während der Softwareentwicklung angezeigt werden.");

        Option konfigdir = OptionBuilder.hasOptionalArg().withDescription("Legt einen altenativen Pfad fest, in dem sich das .opde Verzeichnis befindet.").create("k");
        opts.addOption(konfigdir);


        opts.addOption(OptionBuilder.withLongOpt("jdbc").hasArg().withDescription("Setzt eine alternative URL zur Datenbank fest. Ersetzt die Angaben in der local.properties.").create("j"));

        Option dfnimport = OptionBuilder //.withArgName("datum")
                .withLongOpt("dfnimport") //.hasOptionalArg()
                .withDescription("Startet OPDE im DFNImport Modus für den aktuellen Tag.").create("d");

        opts.addOption(dfnimport);

        Option bhpimport = OptionBuilder.withLongOpt("bhpimport").hasOptionalArg().withDescription("Startet OPDE im BHPImport Modus für den aktuellen Tag.").create("b");
        bhpimport.setOptionalArg(true);
        bhpimport.setArgName("Anzahl der Tage (+ oder -) abweichend vom aktuellen Tag für den der Import durchgeführt werden soll. Nur in Ausnahmefällen anzuwenden.");

        opts.addOption(bhpimport);

        BasicParser parser = new BasicParser();
        CommandLine cl = null;
        String footer = "http://www.Offene-Pflege.de";

        try {
            cl = parser.parse(opts, args);
        } catch (ParseException ex) {
            HelpFormatter f = new HelpFormatter();
            f.printHelp("OffenePflege.jar [OPTION]", "Offene-Pflege.de, Version " + appInfo.getVersion()
                    + " Build:" + appInfo.getBuild(), opts, footer);
            System.exit(0);
        }

        if (cl.hasOption("h")) {
            HelpFormatter f = new HelpFormatter();
            f.printHelp("OffenePflege.jar [OPTION]", "Offene-Pflege.de, Version " + appInfo.getVersion()
                    + " Build:" + appInfo.getBuild(), opts, footer);
            System.exit(0);
        }

        // Alternatives Arbeitsverzeichnis setzen
        if (cl.hasOption("k")) {
            String homedir = cl.getOptionValue("k");
            opwd = homedir + System.getProperty("file.separator") + ".opde";
        } else {
            opwd = System.getProperty("user.home") + System.getProperty("file.separator") + ".opde";
        }

        if (cl.hasOption("a")) { // anonym Modus
            //localProps.put("anonym", "true");
            anonym = true;
            anonymize = new HashMap[]{SYSConst.getNachnamenAnonym(), SYSConst.getVornamenFrauAnonym(), SYSConst.getVornamenMannAnonym()};
        } else {
            anonym = false;
        }

        printers = new PrinterTypes();

        if (loadLocalProperties()) {

            String sep = System.getProperty("file.separator");
            try {
                FileAppender fileAppender = new FileAppender(layout, opwd + sep + "opde.log", true);
                logger.addAppender(fileAppender);
            } catch (IOException ex) {
                fatal(ex);
            }


            animation = localProps.containsKey("animation") && localProps.getProperty("animation").equals("true");

            logger.info("######### START ###########  " + SYSTools.getWindowTitle(""));
            logger.info(System.getProperty("os.name").toLowerCase());

            if (cl.hasOption("l") || SYSTools.catchNull(localProps.getProperty("debug")).equalsIgnoreCase("true")) {
                debug = true;
                logger.setLevel(Level.ALL);
            } else {
                debug = false;
                logger.setLevel(Level.INFO);
            }

            String hostkey = OPDE.getLocalProps().getProperty("hostkey");
            String cryptpassword = localProps.getProperty("javax.persistence.jdbc.password");

            // Passwort entschlüsseln
            DesEncrypter desEncrypter = new DesEncrypter(hostkey);

            Properties jpaProps = new Properties();
            jpaProps.put("javax.persistence.jdbc.user", localProps.getProperty("javax.persistence.jdbc.user"));
            jpaProps.put("javax.persistence.jdbc.password", desEncrypter.decrypt(cryptpassword));
            jpaProps.put("javax.persistence.jdbc.driver", localProps.getProperty("javax.persistence.jdbc.driver"));
            url = cl.hasOption("j") ? cl.getOptionValue("j") : localProps.getProperty("javax.persistence.jdbc.url");
            jpaProps.put("javax.persistence.jdbc.url", url);

            jpaProps.put(QueryHints.JDBC_TIMEOUT, "3");

            // Cache abschalten
            jpaProps.put("eclipselink.cache.shared.default", "false");

//            jpaProps.put("eclipselink.logging.level", JavaLog.FINEST_LABEL);
            jpaProps.put("eclipselink.session.customizer", "op.system.JPAEclipseLinkSessionCustomizer");


            emf = Persistence.createEntityManagerFactory("OPDEPU", jpaProps);

            String header = SYSTools.getWindowTitle("");

            if (cl.hasOption("v")) {
                System.out.println(header);
                System.out.println(footer);
                System.exit(0);
            }

            if (cl.hasOption("d")) {
                // initProps();
                String d = cl.getOptionValue("d");
                try {
                    DFNImport.importDFN();
                } catch (Exception ex) {
                    fatal(ex);
                }
                System.exit(0);
            }

            if (cl.hasOption("b")) {

                EntityManager em = OPDE.createEM();
                int offset = 0;

                if (isDebug()) {
                    String sOffset = cl.getOptionValue("b");
                    OPDE.debug(cl.getOptionValue("b"));
                    try {
                        offset = Integer.parseInt(sOffset);
                    } catch (NumberFormatException ex) {
                        offset = 0;
                    }
                }

                try {
                    em.getTransaction().begin();
                    Users rootUser = em.find(Users.class, "root");

                    SYSLogin rootLogin = new SYSLogin(rootUser);
                    EntityTools.persist(rootLogin);
                    OPDE.setLogin(rootLogin);
                    initProps();

                    Date stichtag = SYSCalendar.addDate(new Date(), offset);
                    BHPTools.erzeugen(em, stichtag);
                    em.getTransaction().commit();
                } catch (Exception ex) {
                    em.getTransaction().rollback();


                    fatal(ex);
                } finally {
                    em.close();
                }

                System.exit(0);
            }

            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());

            try {
                css = SYSTools.readFileAsString(opwd + sep + "standard.css");
            } catch (IOException ie) {
                css = "";
            }

            // JideSoft
            Lm.verifyLicense("Torsten Loehr", "Open-Pflege.de", "G9F4JW:Bm44t62pqLzp5woAD4OCSUAr2");
            WizardStyle.setStyle(WizardStyle.JAVA_STYLE);
            // JideSoft


            // !!!!!!!!!!!!!!!!!!!!!!!! HAUPTPROGRAMM !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
            mainframe = new FrmMain();
            mainframe.setVisible(true);
        }
    }


    public static DisplayManager getDisplayManager() {
        return mainframe.getDisplayManager();
    }

    public static PrintProcessor getPrintProcessor() {
        return mainframe.getPrintProcessor();
    }

    public static FrmMain getMainframe() {
        return mainframe;
    }

    private static boolean loadLocalProperties() {
        boolean success = false;
        Properties sysprops = System.getProperties();
        String sep = sysprops.getProperty("file.separator");
        try {
            FileInputStream in = new FileInputStream(new File(opwd + sep + "local.properties"));
            Properties p = new Properties();
            p.load(in);
            localProps.putAll(p);
            p.clear();

            in.close();

            success = true;
        } catch (FileNotFoundException ex) {
            // Keine local.properties. Wir richten wohl gerade einen neuen Client ein.

            FrmInit frame = new FrmInit();
            frame.setVisible(true);
            SYSTools.center(frame);

        } catch (IOException ex) {
            fatal(ex);
            System.exit(1);
        }
        return success;
    }


    public static Database getDb() {
        return db;
    }

    public static boolean isAdmin() {
        return UsersTools.isAdmin(login.getUser());
    }

//    public static boolean isExamen() {
//        return UsersTools.isExamen(login.getUser());
//    }

    public static OCSec getOCSec() {
        return ocsec;
    }

    public static void newOCSec() {
        if (ocsec != null) {
            ocsec.cleanup();
        }
        ocsec = new OCSec();
    }
}
