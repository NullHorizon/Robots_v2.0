import java.util.ArrayList;

/**
 * Created by shepkan on 13.12.2016.
 */
public class Generator {
    public static void generate(){
        for (int i=0; i< CONST.N; i++){
            main.agents.add(new Agent());
        }
        for (int i=0; i<CONST.friendPairNum;i++){
            ArrayList<Pair> ar=new ArrayList<Pair>();
            int a=StRandom.nextInt(main.agents.size()), b=StRandom.nextInt(main.agents.size());
            int a1=a, b1=b;
            while (ar.indexOf(new Pair(main.agents.get(a), main.agents.get(b)))!=-1){
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
