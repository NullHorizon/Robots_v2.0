import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by shepkan on 01.12.2016.
 */
public class Clusterator {
    private static ArrayList<Cluster> clusters=new ArrayList<Cluster>();
    private static int clNum=StRandom.nextInt(3)+2;

    public static void reset(){
        clusters=new ArrayList<Cluster>();
        clNum=StRandom.nextInt(3)+2;
    }

    public static void setClNum(int clNum){
        Clusterator.clNum=clNum;
    }

    public static void clusterisation(){
        kMiddle();
    }

    private static void kMiddle(){
        final double crit=10.0;
        ArrayList<Point> coms=new ArrayList<Point>();
        Random r=StRandom.getR();
        for (int i=0; i<clNum; i++) {
            clusters.add(new Cluster());
            int nextCom= r.nextInt(main.agents.size());
            boolean f=true;
            while (f){
                f=false;
                for (int j=0; j<i; j++){
                    if (coms.get(j)==main.agents.get(nextCom).getPos()){
                        f=true;
                    }
                }
                nextCom=(nextCom+1)%main.agents.size();
            }
            coms.add(main.agents.get(nextCom).getPos());
            clusters.get(i).add(main.agents.get(nextCom));
        }


        boolean f=true;
        while (f) {

            for (Cluster c:clusters){
                c.clear();
            }
            for (int p=0;p< main.agents.size();p++) {
                Agent a=main.agents.get(p);
                double minD = a.getPos().distance(coms.get(0));
                int cluster = 0;
                for (int i = 1; i < clNum; i++) {
                    if (a.getPos().distance(coms.get(i)) < minD) {
                        minD = a.getPos().distance(coms.get(i));
                        cluster = i;
                    }
                }
                clusters.get(cluster).add(a);
            }

            f=false;
            for (int i=0;i<clNum;i++){
                if ((coms.get(i)!=clusters.get(i).getCom()) && (coms.get(i).distance(clusters.get(i).getCom())>crit)){
                    f=true;
                }
                coms.set(i,clusters.get(i).getCom());
            }
        }
        for (int i=0; i<clusters.size();i++){
            clusters.get(i).finish();
        }

    }

    public static void randIPLead() {
        Agent ans = null;
        Cluster c1=Clusterator.getClusters().get(0);
        while (ans == null) {
            Agent a = Clusterator.getClusters().get(0).getAgents().get(
                    StRandom.nextInt(Clusterator.getClusters().get(0).getAgents().size()));
            if (a.getTargets().size() > 0) {
                ans = a;
            }
        }
        if (c1.lead!=null){
            c1.lead.setLead(false);
        }
        c1.lead=ans;
        c1.lead.setLead(true);
        ((InfPhyTask) main.taskType).newCenterInf(ans);
        Agent a=c1.lead.getTargets().get(StRandom.nextInt(c1.lead.getTargets().size()));
        Cluster c2=Clusterator.getClusters().get(1);
        if (c2.lead!=null){
            c2.lead.setLead(false);
        }
        c2.lead=a;
        c2.lead.setLead(true);
        ((InfPhyTask) main.taskType).newCenterPhy(a);
    }

    public static class Cluster{
        private Color color;
        private ArrayList<Agent> agents;
        private int id;
        private Agent lead;

        private static final Color[] colors= {  Color.orange ,Color.yellow, Color.pink,
                Color.orange, Color.cyan, Color.GRAY, Color.magenta, Color.BLUE};
        private static int colorNum=0;

        private Cluster(){
            agents=new ArrayList<Agent>();
            color=colors[colorNum];
            colorNum=(colorNum+1)%colors.length;
            id=StRandom.nextInt();
        }



        private Point getCom(){
            if (agents.size()==0){
                return main.agents.get(StRandom.nextInt(main.agents.size())).getPos();
            }
            Point ans = new Point(0,0);
            for (Agent a: agents){
                ans.x=ans.x+a.getPos().x;
                ans.y=ans.y+a.getPos().y;
            }
            ans.x=ans.x/agents.size();
            ans.y=ans.y/agents.size();
            return ans;
        }

        private void finish(){
            for (Agent a:agents){
                a.setColor(color);
                a.setClusterId(this.id);
            }
            double minDist=CONST.height+CONST.width;
            lead=null;
            for (int i=0; i<agents.size();i++){
                Agent a= agents.get(i);
                if (getCom().distance(a.getPos())<minDist){
                    lead=a;
                    minDist=getCom().distance(a.getPos());
                }
            }
            lead.setLead(true);
        }

        private void clear(){agents.clear();}
        private void add(Agent a){ agents.add(a);}
        private void remove(Agent a){ agents.remove(a);}
        private Agent getAgent(int i){ return agents.get(i);}
        private Color getColor(){return color;}
        private int getId(){return id;}

        public Agent getLeader(){return this.lead;}
        public ArrayList<Agent> getAgents(){return agents;}

    }

    public static Cluster getCluster(int id){
        for (int i=0; i<clusters.size();i++){
            if (clusters.get(i).getId()==id){
                return clusters.get(i);
            }
        }
        return null;
    }

    public static void infPhyClusterisation(){
        clusters.add(new Cluster());
        clusters.add(new Cluster());
        for (Agent a:main.agents){
            if (a.getPos().x<CONST.width/2){
                clusters.get(0).add(a);
                a.setClusterId(clusters.get(0).id);
            } else {
                clusters.get(1).add(a);
                a.setClusterId(clusters.get(1).id);
            }
        }
        clusters.get(0).finish();
        clusters.get(1).finish();
    }


    public static ArrayList<Cluster> getClusters(){return clusters;}
}
