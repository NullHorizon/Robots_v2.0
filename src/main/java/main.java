import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by AsmodeusX on 30.11.2016.
 */
public class main {
    static ArrayList<Agent> agents;
    public static Frame fr;
    public static void logging(String msg)
    {
        Date date = new Date();
        SimpleDateFormat ft = new SimpleDateFormat("hh:mm:ss:SS ");
        System.out.println(ft.format(date) + msg);
    }
    public static void main(String args[])
    {
        testC();
    }

    public static void testA(){
        fr = new Frame();
        agents = new ArrayList<Agent>();
        Agent A = new Agent();
        Agent B = new Agent();
        Agent C = new Agent();
        Agent D = new Agent();
        Agent E = new Agent();
        agents.add(A);
        agents.add(B);
        agents.add(C);
        agents.add(D);
        agents.add(E);
        fr.repaint();
        agents.get(0).sendMessage(agents.get(1), new Message("HelloHelloHello", null, agents.get(1)));
        agents.get(0).sendMessage(agents.get(2), new Message("HelloHelloHello", null, agents.get(2)));
        agents.get(0).sendMessage(agents.get(3), new Message("HelloHelloHello", null, agents.get(3)));
        agents.get(0).sendMessage(agents.get(4), new Message("HelloHelloHello", null, agents.get(4)));
        //agents.get(0).sendMessage(agents.get(3), new Message("Hello", null, agents.get(4)));
        /*A.sendMessage(D, "Hello!");
        B.sendMessage(D, "HelloHelloHello!");
        C.sendMessage(D, "HelloHelloHelloHelloHelloHello!");*/
    }

    public static void testB(){
        fr = new Frame();
        agents=new ArrayList<Agent>();
        for (int i=0; i<CONST.N; i++){
            agents.add(new Agent());
        }
        Clusterator.clusterisation();
        fr.repaint();
    }

    public static void testC(){
        fr=new Frame();
        Generator.generate();
        Generator.generateTasks();
    }
}
