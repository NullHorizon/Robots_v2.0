import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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

    public static void logging(String msg)
    {
        Date date = new Date();
        SimpleDateFormat ft = new SimpleDateFormat("hh:mm:ss:SS ");
        //System.out.println(ft.format(date) + msg);
    }

    public static void chekTasks(){
        boolean flag=true;
        for (int i=0; i<tasks.size();i++){
            if (tasks.get(i).status==Task.Status.UNSOLVED){
                flag=false;
                break;
            }
        }
        if (flag){
            stats.calc();
        }
    }

    public static void next(){
        if (cur_exp<CONST.EXPERIMENT_NUM){
            java.util.Timer timer2 = new java.util.Timer();
            TimerTask task = new TimerTask() {
                public void run()
                {

                    cur_exp++;
                    Generator.generate();
                    Generator.generateTasks();
                }
            };
            timer2.schedule( task, 100 );
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
