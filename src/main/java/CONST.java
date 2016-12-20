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
    public final static int N;
    public final static int CLUSTERS_NUM;
    public final static int FRIENDS_PAIR_NUM;
    public final static int SEED; //if seed=0 {seed=random}
    public final static int TASK_NUM;
    static {
        DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
        f.setValidating(false);
        Document doc;
        int width1 = 500,
                height1 = 500,
                R1 = 6,
                MAXID1 = 1000000,
                LENKOEF1 = 64,
                ANALYZKOEF1 = 512,
                DISTKOEF1 = 100,
                N1 = 100,
                CLUSTERS_NUM1 = 8,
                FRIENDS_PAIR_NUM1 = 50,
                SEED1 = 0,
                TASK_NUM1 = 30;
        Color color1=Color.BLUE;
        String READMSG1 = "READ",
                SENTMSG1 = "SENT";
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
            N1= new Integer(e.getAttribute("N"));
            CLUSTERS_NUM1 = new Integer(e.getAttribute("CLUSTERS_NUM"));
            FRIENDS_PAIR_NUM1 = new Integer(e.getAttribute("FRIENDS_PAIR_NUM"));
            SEED1 = new Integer(e.getAttribute("SEED"));
            TASK_NUM1 = new Integer(e.getAttribute("TASK_NUM"));
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
    }
}
