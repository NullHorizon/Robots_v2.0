import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Simulator {
    static ArrayList<Agent> agents;
    static ArrayList<Task> tasks;
    static Stats stats;
    static View fr;
    static Task taskType;
    static int cur_exp=1;
    static File logFile=new File("./log.csv");
    static PrintWriter out;
    private static SimulationThread simulationThread;
    private static Status status=null;

    static void logging(String msg)
    {
        Date date = new Date();
        SimpleDateFormat ft = new SimpleDateFormat("hh:mm:ss:SS ");
        //System.out.println(ft.format(date) + msg);
    }

    static void checkTasks(){
        if (taskType.getType().equals("InfPhy")){
            for (Task task : tasks) {
                if (((InfPhyTask) task).getProgress() == InfPhyTask.Progress.FILTERED) {
                    task.status = Task.Status.SOLVED;
                }
            }
        }
        for (Task task : tasks) {
            if (task.status == Task.Status.UNSOLVED) {
                return;
            }
        }
        Simulator.next();
    }

    private static void next(){
        if (taskType.getType().equals("InfPhy") && ((InfPhyTask)taskType).getIterationNum()<
                ((InfPhyTask)taskType).getIterationForThisExp()){
            Generator.generateTasks();
            ((InfPhyTask)taskType).nextIter();
            fr.setIterNum(((InfPhyTask)taskType).getIterationNum());
            return;
        }
        fr.reset();
        stats.calc();
        if (cur_exp< Params.EXPERIMENT_NUM){
            cur_exp++;
            Generator.generate();
            Generator.generateTasks();
        }
    }


    static void init(){
        fr=new View();
        status=Status.READY;
    }

    static void go(){
        if (simulationThread!=null) {
            simulationThread.interrupt();
        }
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Params.initFromFrame();
        Generator.generate();
        Generator.generateTasks();
        simulationThread=new SimulationThread();
        simulationThread.start();
    }

    static class SimulationThread extends Thread{
        public void run(){
            while (!isInterrupted()){
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {

                }
                for (int i=0;i<agents.size();i++){
                    if (isInterrupted() || agents==null){
                        break;
                    }
                    agents.get(i).getQ().step();
                }

            }
        }
    }

    static Status getStatus() {
        return status;
    }

    static void setStatus(Status status) {
        Simulator.status = status;
    }

    enum Status{
        READY, WORKING, GENERATED
    }

}
