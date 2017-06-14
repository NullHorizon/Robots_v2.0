import java.util.ArrayList;

/**
 * Created by shepkan on 21.12.2016.
 */
public abstract class Task {
    Agent agOwner, recipient;
    String message;
    Status status;
    protected String type;
    protected int steps, time;

    public Task(){}
    protected Task(Agent own, Agent rec, String mes){
        agOwner=own;
        recipient=rec;
        message=mes;
        steps=0;
        time=0;
        status=Status.UNSOLVED;
    }

    public String getType(){return type;}

    public void solve(){
        Simulator.stats.addSteps(steps);
        Simulator.stats.addTime(time);
        status=Status.SOLVED;
        Simulator.checkTasks();
    }

    public void feedBack(Message msg){
        if (msg.getChain().size()>1) {
            ArrayList<Agent> chain = msg.getChain();
            Agent cur = chain.remove(chain.size() - 1);
            Agent next = chain.get(chain.size() - 1);
            Message m = new Message(msg.getContent(), chain, next, cur, chain.get(0), Message.MSGType.FEED_BACK);
            m.setNegative(msg.isNegative());
            cur.sendMessage(next, m);
            Simulator.logging(m.toString());
        } else {
            if (msg.isNegative()){
                msg.getFinalTarget().setBroken(true);
            }
            int k = new Integer(msg.getContent().substring(0, msg.getContent().indexOf("_TARG")));
            Simulator.tasks.get(k).solve();
        }
    }

    public abstract Task makeTask(Agent own, Agent rec, String mes);
    public abstract void onGetMessage(Message msg);
    public abstract void step();
    protected boolean feedback(Message msg){
        if (msg.getType()== Message.MSGType.FEED_BACK){
            return true;
        } else {
            Message m= new Message("FEEDBACK", null,msg.getFrom(),msg.getTarget(), Message.MSGType.FEED_BACK);
            msg.getTarget().sendMessage(msg.getFrom(),m);
            return false;
        }
    }

    public enum Status{ UNSOLVED, SOLVED}
}
