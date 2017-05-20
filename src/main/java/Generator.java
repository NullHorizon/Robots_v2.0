import java.awt.*;
import java.util.ArrayList;

/**
 * Created by shepkan on 13.12.2016.
 */
public class Generator {
    public static void generate(){
        if (!(CONST.MAX_AGENTS==0)){
            CONST.N=10+StRandom.nextInt(CONST.MAX_AGENTS-10);
        }
        Clusterator.reset();
        main.agents=null;
        main.tasks=null;
        main.taskType=null;
        main.stats=null;
        main.stats=new Stats();
        switch (CONST.LOGIC_TYPE){
            case "SIMPLE": main.taskType=new SimpleTask(); break;
            case "LEADER": main.taskType= new LeadTask(); break;
            case "CLUSTER": main.taskType=new ClusterTask(); break;
            case "InfPhy": main.taskType=new InfPhyTask(); break;
        }
        if (main.fr!=null){
            main.fr.reset();
        }
        if (main.fr==null) {
            main.fr = new Frame();
        }
        switch (main.taskType.getType()){
            case "simple": simpleGenerate(); break;
            case "leader": leaderGenerate(); break;
            case "cluster": clusterGenerate(); break;
            case "InfPhy": infPhyGenerate(); break;
        }
    }

    private static void commonGenerate(){
        main.agents=new ArrayList<>();
        for (int i=0; i< CONST.N; i++){
            Agent a = new Agent();
            if (main.taskType.getType()=="InfPhy") {
                double R = CONST.width / 2 - 60;
                int x = CONST.width / 2;
                int y = CONST.height / 2;
                double angle=Math.PI*2/CONST.N*i;
                int xplus = (int)(Math.sin(angle) * R), yplus =  (int)(Math.cos(angle) * R);
                if (x+xplus>CONST.width / 2){
                    xplus+=50;
                } else {
                    xplus-=50;
                }
                a.setPos(new Point(x + xplus, y + yplus));
            }
            if (StRandom.nextInt(100)<CONST.SABOTEUR_PERSENT){
                a.setSaboteur(true);
            }
            main.agents.add(a);
        }

    }
    private static void infPhyGenerate(){
        commonGenerate();
        ((InfPhyTask)main.taskType).setIterationNum(0);
        Clusterator.setClNum(2);
        Clusterator.infPhyClusterisation();
        ((InfPhyTask)main.taskType).setIterationForThisExp(StRandom.nextInt(CONST.ITERATION_NUM-10)+10);
        main.stats.setIteration_num(((InfPhyTask)main.taskType).getIterationForThisExp());
        for (Agent a:Clusterator.getClusters().get(1).getAgents()){
            Agent ta=Clusterator.getClusters().get(0).getAgents().get(StRandom.nextInt(Clusterator.getClusters().
                    get(0).getAgents().size()));
            ta.addTarget(a);
            a.setTargeted(ta);
        }
        main.fr.setMaxIter(((InfPhyTask)main.taskType).getIterationForThisExp());
        main.fr.setIterNum(0);
        main.fr.repaint();
    }
    private static void leaderGenerate(){
        commonGenerate();
        Clusterator.clusterisation();
        main.fr.repaint();
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
        int friends_pair_num=CONST.N/4;
        for (int i=0; i<CONST.FRIENDS_PAIR_NUM;i++){
            int a=StRandom.nextInt(main.agents.size()), b=StRandom.nextInt(main.agents.size());
            int a1=a, b1=b;
            while (!(a!=b && pairChek(new Pair(a, b),ar))) {
                a=(a+1)%main.agents.size();
                if (a==a1){
                    b=(b+1)%main.agents.size();
                    if (b==b1){
                        main.logging("To much friends pair.");
                        break;
                    }
                }
            }
            main.agents.get(b).addConnected(main.agents.get(a));
            main.agents.get(a).addConnected(main.agents.get(b));
            ar.add(new Pair(a, b));
        }
    }

    private static void clusterGenerate(){
        commonGenerate();
        Clusterator.clusterisation();
        main.fr.repaint();
        ArrayList<Pair> ar=new ArrayList<Pair>();
        for (Clusterator.Cluster c : Clusterator.getClusters()){
            for (Agent a : c.getAgents()){
                for (Agent b : c.getAgents()){
                    a.addConnected(b);
                    b.addConnected(a);
                    ar.add(new Pair(main.agents.indexOf(a),main.agents.indexOf(b)));
                }
            }
        }
    }

    public static  void generateTasks(){
        main.tasks=new ArrayList<Task>();
        CONST.TASK_NUM=StRandom.nextInt(CONST.N/2)+CONST.N/2;
        switch (main.taskType.getType()) {
            case "InfPhy":
                Clusterator.randIPLead();
                int j=0;
                for (Agent a:Clusterator.getClusters().get(0).getAgents()){
                    for (Agent tar: a.getTargets()){
                        String msg=generateMessage(j);
                        j++;
                        Task t = main.taskType.makeTask(a, tar, msg);
                        a.addTask(t);
                        main.tasks.add(t);
                    }
                }
                break;
            default:
            for (int i = 0; i < CONST.TASK_NUM; i++) {
                int agA = StRandom.nextInt(main.agents.size()), agB = StRandom.nextInt(main.agents.size());
                final Agent a = main.agents.get(agA);
                if (agA == agB) {
                    agB = (agB + 1) % main.agents.size();
                }
                Agent b = main.agents.get(agB);
                String msg=generateMessage(i);
                Task t = main.taskType.makeTask(a, b, msg);
                a.addTask(t);
                main.tasks.add(t);
            }
        }
    }

    private static String generateMessage(int i){
        String msg = "";
        int start_len = 5, i1 = i;
        while (i1 > 0) {
            i1 = i1 / 10;
            start_len++;
        }
        for (int j = 0; j < CONST.MIN_MSG_LEN + StRandom.nextInt(CONST.MAX_MSG_LEN - CONST.MIN_MSG_LEN) - start_len; j++) {
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
