import com.sun.scenario.effect.impl.sw.sse.SSEBlend_SRC_OUTPeer;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Simulator {
    private static ArrayList<Agent> agents;
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
        simulationThread.setExpFinished(true);
        if (taskType.getType().equals("IterableInfPhy") && ((IterableInfPhyTask)taskType).getIterationNum()<
                ((IterableInfPhyTask)taskType).getIterationForThisExp()){
            ((IterableInfPhyTask)taskType).nextIter();
            Generator.generateTasks();
            fr.setIterNum(((IterableInfPhyTask)taskType).getIterationNum());
            return;
        }
        fr.reset();
        stats.calc();
        if (cur_exp< Params.EXPERIMENT_NUM){
            if (taskType.getType().equals("IterableInfPhy")){
                IterableInfPhyTask.setIterationNum(1);
            }
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
            Thread.sleep(300);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Simulator.cur_exp=1;
        Params.initFromFrame();
        Generator.generate();
        Generator.generateTasks();
        simulationThread=new SimulationThread();
        simulationThread.start();

    }

    static class SimulationThread extends Thread{
        private boolean expFinished=false;

        public void setExpFinished(boolean expFinished) {
            this.expFinished = expFinished;
        }
        public void run(){
            while (!isInterrupted()){
                if (fr.getDrawable()) {
                    fr.repaint();
                    try {
                        Thread.sleep(70);
                    } catch (InterruptedException e) {

                    }
                }
                for (int i=0;i<agents.size();i++){
                    if (isInterrupted() || agents==null){
                        break;
                    }
                    int a=agents.size();
                    agents.get(i).getQ().step();
                    if (expFinished){
                        expFinished=false;
                        break;
                    }
                    if (a!=agents.size()){
                        System.out.println("ALARM! "+a+" "+agents.size());
                        //System.out.println(((InfPhyTask) taskType).getIterationNum()+" "+((InfPhyTask) taskType).getIterationForThisExp());
                    }
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

    public static void setAgents(ArrayList<Agent> agents) {
        Simulator.agents = agents;
    }

    public static ArrayList<Agent> getAgents() {
        return agents;
    }

    enum Status{
        READY, WORKING, GENERATED
    }

}
