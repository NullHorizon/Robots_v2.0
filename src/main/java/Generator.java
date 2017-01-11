import java.awt.*;
import java.util.ArrayList;

/**
 * Created by shepkan on 13.12.2016.
 */
public class Generator {
    public static void generate(){
        main.fr=new Frame();
        switch (CONST.LOGIC_TYPE){
            case "SIMPLE": main.taskType=new SimpleTask(); break;
            case "LEADER": main.taskType= new LeadTask(); break;
        }
        switch (main.taskType.getType()){
            case "simple": simpleGenerate(); break;
            case "leader": leaderGenerate(); break;
        }
    }

    private static void leaderGenerate(){
        main.agents=new ArrayList<>();
        for (int i=0; i< CONST.N; i++){
            main.agents.add(new Agent());
        }
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
        main.agents=new ArrayList<Agent>();
        for (int i=0; i< CONST.N; i++){
            main.agents.add(new Agent());
        }
        for (int i=0; i<CONST.FRIENDS_PAIR_NUM;i++){
            ArrayList<Pair> ar=new ArrayList<Pair>();
            int a=StRandom.nextInt(main.agents.size()), b=StRandom.nextInt(main.agents.size());
            int a1=a, b1=b;
            while (ar.indexOf(new Pair(main.agents.get(a), main.agents.get(b)))!=-1 && a!=b){
                a=(a+1)%main.agents.size();
                if (a==a1){
                    b=(b+1)%main.agents.size();
                    if (b==b1){
                        main.logging("to much connectons. All agents are friends");
                    }
                }
            }
            main.agents.get(b).addConnected(main.agents.get(a));
            main.agents.get(a).addConnected(main.agents.get(b));
            ar.add(new Pair(main.agents.get(a),main.agents.get(b)));
        }
    }

    public static  void generateTasks(){
        for (int i=0; i<CONST.TASK_NUM;i++){
            int agA=StRandom.nextInt(main.agents.size()), agB=StRandom.nextInt(main.agents.size());
            final Agent a= main.agents.get(agA);
            if (agA==agB){agB=(agB+1)%main.agents.size();}
            Agent b = main.agents.get(agB);
            a.addTask(main.taskType.makeTask(a, b, "This is message number "+i));
        }
    }
    private static class Pair{
        Agent a, b;
        private Pair(Agent a, Agent b){
            this.a=a;
            this.b=b;
        }

        public boolean equals(Pair p){
            if ((p.a==this.a && p.b==this.b)||(p.a==this.b && p.b==this.a)){
                return true;
            } else {
                return false;
            }
        }
    }
}
