import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.*;
import java.awt.*;
import java.io.File;
import java.lang.reflect.Field;

/**
 * Created by AsmodeusX on 30.11.2016.
 */
public class CONST {
    public final static int width;
    public final static int height;
    public final static int R;
    public final static Color color;
    public final static int MAXID;
    public final static double LENKOEF;
    public final static double ANALYZKOEF;
    public final static double DISTKOEF;
    public final static String READMSG;
    public final static String SENTMSG;
    public final static String DRAW;
    public static int N;
    public final static int CLUSTERS_NUM;
    public final static int FRIENDS_PAIR_NUM;
    public final static int SEED; //if seed=0 {seed=random}
    public static int TASK_NUM;
    public final static String LOGIC_TYPE; // SIMPLE/LEADER
    public final static int MAX_MSG_LEN;
    public final static int MIN_MSG_LEN;;
    public final static int EXPERIMENT_NUM;
    public final static int SABOTEUR_PERSENT;
    public final static int MAX_AGENTS;
    public final static int ITERATION_NUM;
    public final static int MESSAGE_CHECK_PERCENT;
    public final static int AGENT_CHECK_PERCENT;
    static {
        DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
        f.setValidating(false);
        Document doc;
        int width1 = 500,
                height1 = 500,
                MAX_AGENTS1=0,
                R1 = 6,
                MAXID1 = 1000000,
                LENKOEF1 = 64,
                ANALYZKOEF1 = 512,
                DISTKOEF1 = 100,
                N1 = 100,
                CLUSTERS_NUM1 = 8,
                FRIENDS_PAIR_NUM1 = 50,
                SEED1 = 0,
                TASK_NUM1 = 30,
                MAX_MSG_LEN1=10,
                MIN_MSG_LEN1=3,
                EXPERIMENT_NUM1=1,
                SABOTEUR_PERSENT1=0,
                ITERATION_NUM1=10,
                MESSAGE_CHECK_PERCENT1=0,
                AGENT_CHECK_PERCENT1=0;
                Color color1=Color.BLUE;
                String READMSG1 = "READ",
                SENTMSG1 = "SENT",
                LOGIC_TYPE1="SIMPLE",
                DRAW1="true";
        try {
            DocumentBuilder builder = f.newDocumentBuilder();
            doc = builder.parse(new File("src/params.xml"));
            Element e=doc.getDocumentElement();
            e.normalize();
            width1 = new Integer(e.getAttribute("width"));
            height1 = new Integer(e.getAttribute("height"));
            R1 = new Integer(e.getAttribute("R"));
            Field field = Class.forName("java.awt.Color").getField(e.getAttribute("color"));
            color1 = (Color)field.get(null);
            MAXID1 = new Integer(e.getAttribute("MAXID"));
            LENKOEF1 = new Integer(e.getAttribute("LENKOEF"));
            ANALYZKOEF1 = new Integer(e.getAttribute("ANALYZKOEF"));
            DISTKOEF1 = new Integer(e.getAttribute("DISTKOEF"));
            READMSG1 = e.getAttribute("READMSG");
            SENTMSG1 = e.getAttribute("SENTMSG");
            DRAW1=e.getAttribute("DRAW");
            N1= new Integer(e.getAttribute("N"));
            CLUSTERS_NUM1 = new Integer(e.getAttribute("CLUSTERS_NUM"));
            FRIENDS_PAIR_NUM1 = new Integer(e.getAttribute("FRIENDS_PAIR_NUM"));
            SEED1 = new Integer(e.getAttribute("SEED"));
            TASK_NUM1 = new Integer(e.getAttribute("TASK_NUM"));
            LOGIC_TYPE1 = e.getAttribute("LOGIC_TYPE");
            EXPERIMENT_NUM1=new Integer(e.getAttribute("EXPERIMENT_NUM"));
            MAX_MSG_LEN1 = new Integer(e.getAttribute("MAX_MSG_LEN"));
            MIN_MSG_LEN1 = new Integer(e.getAttribute("MIN_MSG_LEN"));
            SABOTEUR_PERSENT1=new Integer(e.getAttribute("SABOTEUR_PERSENT"));
            MAX_AGENTS1=new Integer(e.getAttribute("MAX_AGENTS"));
            ITERATION_NUM1=new Integer(e.getAttribute("ITERATION_NUM"));
            MESSAGE_CHECK_PERCENT1=new Integer(e.getAttribute("MESSAGE_CHECK_PERCENT"));
            AGENT_CHECK_PERCENT1=new Integer(e.getAttribute("AGENT_CHECK_PERCENT"));
        } catch (Exception e) {
            main.logging("XML parse error!");
        }
        width=width1;
        height=height1;
        R=R1;
        CLUSTERS_NUM=CLUSTERS_NUM1;
        FRIENDS_PAIR_NUM=FRIENDS_PAIR_NUM1;
        TASK_NUM=TASK_NUM1;
        SEED=SEED1;
        SENTMSG=SENTMSG1;
        ANALYZKOEF=ANALYZKOEF1;
        LENKOEF=LENKOEF1;
        color=color1;
        MAXID=MAXID1;
        DISTKOEF=DISTKOEF1;
        READMSG=READMSG1;
        N=N1;
        LOGIC_TYPE=LOGIC_TYPE1;
        MAX_MSG_LEN=MAX_MSG_LEN1;
        MIN_MSG_LEN=MIN_MSG_LEN1;
        EXPERIMENT_NUM=EXPERIMENT_NUM1;
        SABOTEUR_PERSENT=SABOTEUR_PERSENT1;
        MAX_AGENTS=MAX_AGENTS1;
        ITERATION_NUM=ITERATION_NUM1;
        AGENT_CHECK_PERCENT=AGENT_CHECK_PERCENT1;
        MESSAGE_CHECK_PERCENT=MESSAGE_CHECK_PERCENT1;
        DRAW=DRAW1;
    }
}
