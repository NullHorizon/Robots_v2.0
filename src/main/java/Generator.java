import java.awt.*;
import java.util.ArrayList;

/**
 * Created by shepkan on 13.12.2016.
 */
public class Generator {
    public static void generate(){
        Params.N=StRandom.nextInt(Params.MAX_AGENTS-Params.MIN_AGENTS)+Params.MIN_AGENTS;
        Params.SABOTEUR_PERSENT=StRandom.nextInt(Params.SABOTEUR_PERSENT_MAX-Params.SABOTEUR_PERSENT_MIN)
                + Params.SABOTEUR_PERSENT_MIN;
        Params.MESSAGE_CHECK_PERCENT=StRandom.nextInt(Params.MESSAGE_CHECK_PERCENT_MAX-Params.MESSAGE_CHECK_PERCENT_MIN)
                + Params.MESSAGE_CHECK_PERCENT_MIN;
        Params.AGENT_CHECK_PERCENT=StRandom.nextInt(Params.MAX_CENTER_CHECK-Params.MIN_CENTER_CHECK)
                + Params.MIN_CENTER_CHECK;
        if (!(Params.MAX_AGENTS==0)){
            Params.N=10+StRandom.nextInt(Params.MAX_AGENTS-10);
        }
        Clusterator.reset();
        Simulator.agents=null;
        Simulator.tasks=null;
        Simulator.taskType=null;
        Simulator.stats=null;
        Simulator.stats=new Stats();
        Simulator.fr.reset();
        switch (Params.LOGIC_TYPE){
            case "SIMPLE": Simulator.taskType=new SimpleTask(); break;
            case "LEADER": Simulator.taskType= new LeadTask(); break;
            case "CLUSTER": Simulator.taskType=new ClusterTask(); break;
            case "InfPhy": Simulator.taskType=new InfPhyTask(); break;
        }

        switch (Simulator.taskType.getType()){
            case "simple": simpleGenerate(); break;
            case "leader": leaderGenerate(); break;
            case "cluster": clusterGenerate(); break;
            case "InfPhy": infPhyGenerate(); break;
        }
        Simulator.setStatus(Simulator.Status.GENERATED);
    }

    private static void commonGenerate(){
        Simulator.agents=new ArrayList<>();
        for (int i = 0; i< Params.N; i++){
            Agent a = new Agent();
            if (Simulator.taskType.getType()=="InfPhy") {
                double R = Params.width / 2 - 60;
                int x = Params.width / 2;
                int y = Params.height / 2;
                double angle=Math.PI*2/ Params.N*i;
                int xplus = (int)(Math.sin(angle) * R), yplus =  (int)(Math.cos(angle) * R);
                if (x+xplus> Params.width / 2){
                    xplus+=50;
                } else {
                    xplus-=50;
                }
                a.setPos(new Point(x + xplus, y + yplus));
            }
            if (StRandom.nextInt(100)< Params.SABOTEUR_PERSENT){
                a.setSaboteur(true);
            }
            Simulator.agents.add(a);
        }

    }
    private static void infPhyGenerate(){
        commonGenerate();
        ((InfPhyTask) Simulator.taskType).setIterationNum(0);
        Clusterator.setClNum(2);
        Clusterator.infPhyClusterisation();
        ((InfPhyTask) Simulator.taskType).setIterationForThisExp(StRandom.nextInt(Params.ITERATION_NUM-10)+10);
        Simulator.stats.setIteration_num(((InfPhyTask) Simulator.taskType).getIterationForThisExp());
        for (Agent a:Clusterator.getClusters().get(1).getAgents()){
            Agent ta=Clusterator.getClusters().get(0).getAgents().get(StRandom.nextInt(Clusterator.getClusters().
                    get(0).getAgents().size()));
            ta.addTarget(a);
            a.setTargeted(ta);
        }
        Simulator.fr.setMaxIter(((InfPhyTask) Simulator.taskType).getIterationForThisExp());
        Simulator.fr.setIterNum(0);
        Simulator.fr.repaint();
    }
    private static void leaderGenerate(){
        commonGenerate();
        Clusterator.clusterisation();
        Simulator.fr.repaint();
        for (Clusterator.Cluster c : Clusterator.getClusters()){
            Agent l=c.getLeader();
            for (Agent a :c.getAgents()){
                l.addConnected(a);
                a.addConnected(l);
            }
            for (Clusterator.Cluster c1 : Clusterator.getClusters()){
                l.addConnected(c1.getLeader());
                c1.getLeader().addConnected(l);
            }
        }
    }

    private static void simpleGenerate(){
        commonGenerate();
        ArrayList<Pair> ar=new ArrayList<Pair>();
        int friends_pair_num= Params.N/4;
        for (int i = 0; i< Params.FRIENDS_PAIR_NUM; i++){
            int a=StRandom.nextInt(Simulator.agents.size()), b=StRandom.nextInt(Simulator.agents.size());
            int a1=a, b1=b;
            while (!(a!=b && pairChek(new Pair(a, b),ar))) {
                a=(a+1)% Simulator.agents.size();
                if (a==a1){
                    b=(b+1)% Simulator.agents.size();
                    if (b==b1){
                        Simulator.logging("To much friends pair.");
                        break;
                    }
                }
            }
            Simulator.agents.get(b).addConnected(Simulator.agents.get(a));
            Simulator.agents.get(a).addConnected(Simulator.agents.get(b));
            ar.add(new Pair(a, b));
        }
    }

    private static void clusterGenerate(){
        commonGenerate();
        Clusterator.clusterisation();
        Simulator.fr.repaint();
        ArrayList<Pair> ar=new ArrayList<Pair>();
        for (Clusterator.Cluster c : Clusterator.getClusters()){
            for (Agent a : c.getAgents()){
                for (Agent b : c.getAgents()){
                    a.addConnected(b);
                    b.addConnected(a);
                    ar.add(new Pair(Simulator.agents.indexOf(a), Simulator.agents.indexOf(b)));
                }
            }
        }
    }

    public static  void generateTasks(){
        Simulator.tasks=new ArrayList<Task>();
        Params.TASK_NUM=StRandom.nextInt(Params.N/2)+ Params.N/2;
        switch (Simulator.taskType.getType()) {
            case "InfPhy":
                Clusterator.randIPLead();
                int j=0;
                for (Agent a:Clusterator.getClusters().get(0).getAgents()){
                    for (Agent tar: a.getTargets()){
                        String msg=generateMessage(j);
                        j++;
                        Task t = Simulator.taskType.makeTask(a, tar, msg);
                        a.addTask(t);
                        Simulator.tasks.add(t);
                    }
                }
                break;
            default:
            for (int i = 0; i < Params.TASK_NUM; i++) {
                int agA = StRandom.nextInt(Simulator.agents.size()), agB = StRandom.nextInt(Simulator.agents.size());
                final Agent a = Simulator.agents.get(agA);
                if (agA == agB) {
                    agB = (agB + 1) % Simulator.agents.size();
                }
                Agent b = Simulator.agents.get(agB);
                String msg=generateMessage(i);
                Task t = Simulator.taskType.makeTask(a, b, msg);
                a.addTask(t);
                Simulator.tasks.add(t);
            }
        }
        Simulator.setStatus(Simulator.Status.WORKING);
    }

    private static String generateMessage(int i){
        String msg = "";
        int start_len = 5, i1 = i;
        while (i1 > 0) {
            i1 = i1 / 10;
            start_len++;
        }
        for (int j = 0; j < Params.MIN_MSG_LEN + StRandom.nextInt(Params.MAX_MSG_LEN - Params.MIN_MSG_LEN) - start_len; j++) {
            char c = (char) (StRandom.nextInt(25) + 97);
            msg = msg + c;
        }
        return i + "_TARG" + msg;
    }
    private static class Pair{
        int a, b;
        private Pair(int a, int b){
            this.a=a;
            this.b=b;
        }

        public boolean eq(Pair p){
            if ((p.a==this.a && p.b==this.b)||(p.a==this.b && p.b==this.a)){
                return true;
            } else {
                return false;
            }
        }
    }

    private static boolean pairChek(Pair p, ArrayList<Pair> ar){
        boolean ans=true;
        for (int i=0; i<ar.size();i++){
            if (p.eq(ar.get(i))){
                ans=false;
                break;
            }
        }
        return ans;
    }
}
