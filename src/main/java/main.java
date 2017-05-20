import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by AsmodeusX on 30.11.2016.
 */
public class main {
    public static ArrayList<Agent> agents;
    public static ArrayList<Task> tasks;
    public static Stats stats;
    public static Frame fr;
    public static Task taskType;
    public static int cur_exp=1;
    public static File logFile=new File("./log.csv");
    public static PrintWriter out;

    public static void logging(String msg)
    {
        Date date = new Date();
        SimpleDateFormat ft = new SimpleDateFormat("hh:mm:ss:SS ");
        //System.out.println(ft.format(date) + msg);
    }
    public static void alert(String msg){
        Date date = new Date();
        SimpleDateFormat ft = new SimpleDateFormat("hh:mm:ss:SS ");
        if (fr!=null) {
            fr.log(ft.format(date)+msg);
        }

    }

    public static void chekTasks(){
        for (int i=0; i<tasks.size();i++){
            if (taskType.getType()=="InfPhy"){
                for (int j=0;j<tasks.size();j++){
                    if (((InfPhyTask)tasks.get(j)).getProgress()== InfPhyTask.Progress.FILTERED){
                        tasks.get(i).status= Task.Status.SOLVED;
                    }
                }
            }
            if (tasks.get(i).status==Task.Status.UNSOLVED){
                return;
            }
        }
        java.util.Timer timer = new java.util.Timer();
        TimerTask tt = new TimerTask() {
            @Override
            public void run() {
                boolean flag = true;
                while (flag) {
                    flag = false;
                    for (int i = 0; i < agents.size(); i++) {
                        if (agents.get(i).getQ().isWorking()) {
                            flag = true;
                            break;
                        }
                    }
                }
                main.next();
            }
        };
        timer.schedule(tt, 100);
    }

    public static void next(){
        if (taskType.getType()=="InfPhy" && ((InfPhyTask)taskType).getIterationNum()<
                ((InfPhyTask)taskType).getIterationForThisExp()){
            Generator.generateTasks();
            ((InfPhyTask)taskType).nextIter();
            fr.setIterNum(((InfPhyTask)taskType).getIterationNum());
            return;
        }
        stats.calc();
        if (cur_exp<CONST.EXPERIMENT_NUM){
            cur_exp++;
            Generator.generate();
            Generator.generateTasks();
            /*java.util.Timer timer2 = new java.util.Timer();
            TimerTask task = new TimerTask() {
                public void run()
                {
                    cur_exp++;
                    Generator.generate();
                    Generator.generateTasks();
                }
            };
            timer2.schedule( task, 100 );*/
        }
    }

    public static void main(String args[])
    {
        testC();
    }

    public static void testC(){
        Generator.generate();
        Generator.generateTasks();
    }

}
