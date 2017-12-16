import java.awt.*;
import java.util.ArrayList;

public class Generator {
    public static void generate(){
       /* if (Params.MAX_AGENTS==0){
            Params.N=Params.MIN_AGENTS;
        } else {
            Params.N = StRandom.nextInt(Params.MAX_AGENTS - Params.MIN_AGENTS) + Params.MIN_AGENTS;
        }
        if (Params.SABOTEUR_PERSENT_MAX==0){
            Params.SABOTEUR_PERSENT=Params.SABOTEUR_PERSENT_MIN;
        } else {
            Params.SABOTEUR_PERSENT = StRandom.nextInt(Params.SABOTEUR_PERSENT_MAX - Params.SABOTEUR_PERSENT_MIN)
                    + Params.SABOTEUR_PERSENT_MIN;
        }*/

        Params.MESSAGE_CHECK_PERCENT=StRandom.nextInt(Params.MESSAGE_CHECK_PERCENT_MAX-Params.MESSAGE_CHECK_PERCENT_MIN)
                + Params.MESSAGE_CHECK_PERCENT_MIN;
        Params.AGENT_CHECK_PERCENT=StRandom.nextInt(Params.MAX_CENTER_CHECK-Params.MIN_CENTER_CHECK)
                + Params.MIN_CENTER_CHECK;
        Clusterator.reset();
        Simulator.setAgents(null);
        Simulator.tasks=null;
        Simulator.taskType=null;
        Simulator.stats=new Stats();
        Simulator.fr.reset();
        switch (Params.LOGIC_TYPE){
            case "SIMPLE": Simulator.taskType=new SimpleTask(); break;
            case "LEADER": Simulator.taskType= new LeadTask(); break;
            case "CLUSTER": Simulator.taskType=new ClusterTask(); break;
            case "InfPhy": Simulator.taskType=new InfPhyTask(); break;
            case "IterableInfPhy": Simulator.taskType=new IterableInfPhyTask(); break;
        }

        switch (Simulator.taskType.getType()){
            case "simple": simpleGenerate(); break;
            case "leader": leaderGenerate(); break;
            case "cluster": clusterGenerate(); break;
            case "InfPhy": infPhyGenerate(); break;
            case "IterableInfPhy": iterableInfPhyGenerate(); break;
        }
        Simulator.setStatus(Simulator.Status.GENERATED);
    }

    private static void commonGenerate(){
        Simulator.setAgents(new ArrayList<Agent>());
        for (int i = 0; i< Params.N; i++){
            Agent a = new Agent();
            if (Simulator.taskType.getType()=="InfPhy" || Simulator.taskType.getType()=="IterableInfPhy") {
                double R = Params.width / 2 - 60;
                int x = Params.width / 2;
                int y = Params.height / 2;
                double angle=Math.PI*2/ Params.N*i;
                int xplus = (int)(Math.sin(angle) * R), yplus =  (int)(Math.cos(angle) * R);
                a.setPos(new Point(x + xplus, y + yplus));
            }
            /*if (StRandom.nextInt(100)< Params.SABOTEUR_PERSENT){
                a.setSaboteur(true);
                Simulator.stats.addStartSaboteur();
            }*/
            Simulator.getAgents().add(a);
        }
        for (Agent a: Simulator.getAgents()){
            if (StRandom.nextInt(100)<Params.SABOTEUR_PERSENT){
                a.setSaboteur(true);
            }
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
            int a=StRandom.nextInt(Simulator.getAgents().size()), b=StRandom.nextInt(Simulator.getAgents().size());
            int a1=a, b1=b;
            while (!(a!=b && pairChek(new Pair(a, b),ar))) {
                a=(a+1)% Simulator.getAgents().size();
                if (a==a1){
                    b=(b+1)% Simulator.getAgents().size();
                    if (b==b1){
                        Simulator.logging("To much friends pair.");
                        break;
                    }
                }
            }
            Simulator.getAgents().get(b).addConnected(Simulator.getAgents().get(a));
            Simulator.getAgents().get(a).addConnected(Simulator.getAgents().get(b));
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
                    ar.add(new Pair(Simulator.getAgents().indexOf(a), Simulator.getAgents().indexOf(b)));
                }
            }
        }
    }

    private static void iterableInfPhyGenerate(){
        IterableInfPhyTask iterableInfPhyTaskType=(IterableInfPhyTask) Simulator.taskType;
        iterableInfPhyTaskType.setIterationForThisExp(Params.ITERATION_NUM);
        commonGenerate();
        Clusterator.setClNum(2);
        Clusterator.infPhyClusterisation();
        for (Agent a:Clusterator.getClusters().get(1).getAgents()){
            Agent ta=Clusterator.getClusters().get(0).getAgents().get(StRandom.nextInt(Clusterator.getClusters().
                    get(0).getAgents().size()));
            ta.addTarget(a);
            a.setTargeted(ta);
        }
        for (Agent a:Clusterator.getClusters().get(1).getAgents()){
            a.setLogicType(Agent.LogicType.PHY);
        }
        for (Agent a:Clusterator.getClusters().get(0).getAgents()){
            a.setLogicType(Agent.LogicType.INF);
        }
        if (Params.NOSUBGROUPS){
            iterableInfPhyTaskType.setSubGroups(Clusterator.getClusters().get(0).getAgents().size());
        } else {
            iterableInfPhyTaskType.setSubGroups(Params.SUB_GROUP_NUM);
        }
        Clusterator.setClNum(iterableInfPhyTaskType.getSubGroups());
        Clusterator.iterableInfPhyClusterisation();
        Simulator.fr.setMaxIter(iterableInfPhyTaskType.getIterationForThisExp());
        Simulator.fr.setIterNum(0);
        Simulator.fr.repaint();
    }

    public static  void generateTasks(){
        Simulator.tasks=new ArrayList<Task>();
        if (Simulator.taskType.getType()=="InfPhy" || Simulator.taskType.getType()=="IterableInfPhy") {
            if (Simulator.taskType.getType()=="InfPhy") {
                Clusterator.randIPLead();
            }
            int j = 0;
            ArrayList<Agent> infAgents=new ArrayList<>();
            for (Agent a: Simulator.getAgents()){
                if (a.getLogicType()== Agent.LogicType.INF){
                    infAgents.add(a);
                }
            }
            for (Agent a : infAgents) {
                for (Agent tar : a.getTargets()) {
                    String msg = generateMessage(j);
                    j++;
                    Task t = Simulator.taskType.makeTask(a, tar, msg);
                    a.addTask(t);
                    Simulator.tasks.add(t);
                }
            }
        } else {
            for (int i = 0; i < Params.TASK_NUM; i++) {
                int agA = StRandom.nextInt(Simulator.getAgents().size()), agB = StRandom.nextInt(Simulator.getAgents().size());
                final Agent a = Simulator.getAgents().get(agA);
                if (agA == agB) {
                    agB = (agB + 1) % Simulator.getAgents().size();
                }
                Agent b = Simulator.getAgents().get(agB);
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
