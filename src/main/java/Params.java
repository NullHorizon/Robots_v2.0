import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.*;
import java.awt.*;
import java.io.File;

/**
 * Created by AsmodeusX on 30.11.2016.
 */
public class Params {
    public static int width=700;
    public static int height=600;
    public static int R=6;
    public static Color color=Color.BLUE;
    public static String READMSG="READ";
    public static String SENTMSG="SENT";
    public static int MAXID=1000000;

    public static double LENKOEF=5;
    public static double ANALYZKOEF=0;
    public static double DISTKOEF=0;
    public static String LOGIC_TYPE="SIMPLE"; // SIMPLE/LEADER/CLUSTER/InfPhy
    public static int EXPERIMENT_NUM=2;
    public static String DRAW="true";
    public static int SEED=0; //if seed=0 {seed=random}
    public static int TASK_NUM=0;

    public static int SABOTEUR_PERSENT_MIN=10;
    public static int SABOTEUR_PERSENT_MAX=20;
    public static int MAX_AGENTS=30;
    public static int MIN_AGENTS=10;
    public static int MESSAGE_CHECK_PERCENT_MIN=0;
    public static int MESSAGE_CHECK_PERCENT_MAX=0;
    public static int MAX_CENTER_CHECK=0;
    public static int MIN_CENTER_CHECK=0;
    public static int MAX_MSG_LEN=20;
    public static int MIN_MSG_LEN=10;

    public static int ITERATION_NUM=10;
    public static int CLUSTERS_NUM=3;
    public static int FRIENDS_PAIR_NUM=10;

    public static int N=30;
    public static int SABOTEUR_PERSENT=15;
    public static int MESSAGE_CHECK_PERCENT=0;
    public static int AGENT_CHECK_PERCENT=0;
    public static int divide=width/2;
    public static int SUB_GROUP_NUM;

    public static boolean RECLUSTERISATION=true;
    public static boolean NOSUBGROUPS=false;

    public static void initFromFrame(){
        View f=Simulator.fr;
        N=f.getAgentsNum();
        ITERATION_NUM=f.getIterationNum();
        SABOTEUR_PERSENT=f.getSuboteurPercent();
        SUB_GROUP_NUM=f.getSubGroupsNum();

        LOGIC_TYPE=f.getLogic();
        MAX_AGENTS=f.getMaxAgent();
        MIN_AGENTS=f.getMinAgent();
        SABOTEUR_PERSENT_MIN=f.getMinSaboteurPercent();
        SABOTEUR_PERSENT_MAX=f.getMaxSaboteurPercent();
        MESSAGE_CHECK_PERCENT_MIN=f.getMinMessageFilterPercent();
        MESSAGE_CHECK_PERCENT_MAX=f.getMaxMessageFilterPercent();
        LENKOEF=f.getLenKoef();
        DISTKOEF=f.getDistKoef();
        ANALYZKOEF=f.getAnalyseKeefe();
        SEED=f.getSeed();
        MAX_MSG_LEN=f.getMaxMsgLen();
        MIN_MSG_LEN=f.getMinMsgLen();
        EXPERIMENT_NUM=f.getExpNum();
        TASK_NUM=f.getTaskNum();
        MAX_CENTER_CHECK=f.getMaxCenterFilterPercent();
        MIN_CENTER_CHECK=f.getMinCenterFilterPercent();
        RECLUSTERISATION=f.isReclusterisation();
        NOSUBGROUPS=f.isNoSubGroups();
        if (f.getDrawable()){
            DRAW="true";
        } else {
            DRAW="false";
        }
    }

    public static void initFromXml() {
        DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
        f.setValidating(false);
        Document doc;

        try {
            DocumentBuilder builder = f.newDocumentBuilder();
            doc = builder.parse(new File("src/params.xml"));
            Element e=doc.getDocumentElement();
            e.normalize();
            LENKOEF = new Integer(e.getAttribute("LENKOEF"));
            ANALYZKOEF = new Integer(e.getAttribute("ANALYZKOEF"));
            DISTKOEF = new Integer(e.getAttribute("DISTKOEF"));
            READMSG = e.getAttribute("READMSG");
            SENTMSG = e.getAttribute("SENTMSG");
            DRAW=e.getAttribute("DRAW");
            N= new Integer(e.getAttribute("N"));
            CLUSTERS_NUM = new Integer(e.getAttribute("CLUSTERS_NUM"));
            FRIENDS_PAIR_NUM = new Integer(e.getAttribute("FRIENDS_PAIR_NUM"));
            SEED = new Integer(e.getAttribute("SEED"));
            TASK_NUM = new Integer(e.getAttribute("TASK_NUM"));
            LOGIC_TYPE = e.getAttribute("LOGIC_TYPE");
            EXPERIMENT_NUM=new Integer(e.getAttribute("EXPERIMENT_NUM"));
            MAX_MSG_LEN = new Integer(e.getAttribute("MAX_MSG_LEN"));
            MIN_MSG_LEN= new Integer(e.getAttribute("MIN_MSG_LEN"));
            SABOTEUR_PERSENT_MIN=new Integer(e.getAttribute("SABOTEUR_PERSENT"));
            MAX_AGENTS=new Integer(e.getAttribute("MAX_AGENTS"));
            ITERATION_NUM=new Integer(e.getAttribute("ITERATION_NUM"));
            MESSAGE_CHECK_PERCENT=new Integer(e.getAttribute("MESSAGE_CHECK_PERCENT"));
            AGENT_CHECK_PERCENT=new Integer(e.getAttribute("AGENT_CHECK_PERCENT"));
        } catch (Exception e) {
            Simulator.logging("XML parse error!");
        }
    }
}
